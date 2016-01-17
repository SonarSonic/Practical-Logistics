package sonar.logistics.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.network.SyncEmitter;
import sonar.logistics.registries.EmitterRegistry;

public class TileEntityDataReceiver extends TileEntityNode implements IDataReceiver {

	// client list
	public List<DataEmitter> emitters;
	public List<DataEmitter> lastemitters;

	public SyncEmitter emitter = new SyncEmitter(0);

	@Override
	public DataEmitter getEmitter() {
		if (emitter.getEmitter() != null) {
			TileEntity tile = emitter.getEmitter().coords.getTileEntity();
			if (tile != null && tile instanceof TileEntityDataEmitter) {
				TileEntityDataEmitter dataEmitter = (TileEntityDataEmitter) tile;
				emitter.setEmitter(new DataEmitter(dataEmitter.clientName.getString(), emitter.getEmitter().coords));
			}
		}
		return emitter.getEmitter();
	}

	@Override
	public Info currentInfo() {
		if (emitter.getEmitter() != null) {
			return new StandardInfo((byte) -1, "DEFAULT", "Connected: ", emitter.getEmitter().coords.getRender());
		} else {
			return new StandardInfo((byte) -1, "DEFAULT", "Connection: ", "NOT CONNECTED");
		}
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return ForgeDirection.getOrientation(this.getBlockMetadata()) == dir;
	}

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		CableHelper.updateAdjacentCoord(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, ForgeDirection.getOrientation(this.getBlockMetadata()));

	}
	
	
	public boolean maxRender() {
		return true;
	}

	public void sendAvailableData(TileEntity te, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {			
			emitters = EmitterRegistry.getEmitters(playerName);			
			NBTTagCompound syncData = new NBTTagCompound();
			writeData(syncData, SyncType.SPECIAL);
			SonarCore.network.sendTo(new PacketTileSync(te.xCoord, te.yCoord, te.zCoord, syncData, SyncType.SPECIAL), (EntityPlayerMP) player);
		}

	}
	
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			emitter.readFromNBT(nbt, type);
		}
		if (type == SyncType.SPECIAL) {
			emitter.readFromNBT(nbt, SyncType.SYNC);
			if (nbt.hasKey("null")) {
				this.emitters = new ArrayList();
				return;
			}
			NBTTagList list = nbt.getTagList("Emitters", 10);
			if (this.emitters == null) {
				this.emitters = new ArrayList();
			}
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				byte slot = compound.getByte("Slot");
				boolean set = slot < emitters.size();
				switch (compound.getByte("f")) {
				case 0:
					if (set)
						emitters.set(slot, DataEmitter.readFromNBT(compound));
					else
						emitters.add(slot, DataEmitter.readFromNBT(compound));
					break;
				case 1:
					String name = compound.getString("Name");
					if (name != null) {
						emitters.set(slot, new DataEmitter(name, emitters.get(slot).coords));
					} else {
						emitters.set(slot, null);
					}
					break;
				case 2:
					if (set)
						emitters.set(slot, null);
					else
						emitters.add(slot, null);
					break;
				}

			}
		}
		if (type == SyncType.SYNC) {
			emitter.readFromNBT(nbt, SyncType.SAVE);
			NBTTagList list = nbt.getTagList("Emitters", 10);
			this.emitters = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.emitters.add(DataEmitter.readFromNBT(compound));

			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			emitter.writeToNBT(nbt, type);
		}
		if (type == SyncType.SPECIAL) {
			emitter.writeToNBT(nbt, SyncType.SYNC);
			if (emitters == null) {
				emitters = new ArrayList();
			}
			if (lastemitters == null) {
				lastemitters = new ArrayList();
			}
			if (this.emitters.size() <= 0 && (!(this.lastemitters.size() <= 0))) {
				nbt.setBoolean("null", true);
				this.lastemitters = new ArrayList();
				return;
			}
			NBTTagList list = new NBTTagList();
			int size = Math.max(this.emitters.size(), this.lastemitters.size());
			for (int i = 0; i < size; ++i) {
				DataEmitter current = null;
				DataEmitter last = null;
				if (i < this.emitters.size()) {
					current = this.emitters.get(i);
				}
				if (i < this.lastemitters.size()) {
					last = this.lastemitters.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!BlockCoords.equalCoords(current.coords, last.coords)) {
							compound.setByte("f", (byte) 0);
							this.lastemitters.set(i, current);
							DataEmitter.writeToNBT(compound, this.emitters.get(i));

						} else if (!current.name.equals(last.name)) {
							compound.setByte("f", (byte) 1);
							this.lastemitters.set(i, current);
							compound.setString("Name", current.name);
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastemitters.add(i, current);
						DataEmitter.writeToNBT(compound, this.emitters.get(i));
					}
				} else if (last != null) {
					this.lastemitters.set(i, null);
					compound.setByte("f", (byte) 2);
				}
				if (!compound.hasNoTags()) {
					compound.setByte("Slot", (byte) i);
					list.appendTag(compound);
				}

			}
			if (list.tagCount() != 0) {
				nbt.setTag("Emitters", list);
			}
		}
		if (type == SyncType.SYNC) {
			emitter.writeToNBT(nbt, SyncType.SAVE);
			NBTTagList list = new NBTTagList();
			if (emitters == null) {
				emitters = new ArrayList();
			}
			for (int i = 0; i < this.emitters.size(); i++) {
				if (this.emitters.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					DataEmitter.writeToNBT(compound, this.emitters.get(i));
					list.appendTag(compound);
				}
			}

			nbt.setTag("Emitters", list);
		}

	}
}