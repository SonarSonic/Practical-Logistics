package sonar.logistics.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.utils.ExternalCoords;
import sonar.logistics.api.utils.IdentifiedCoords;
import sonar.logistics.network.SyncIdentifiedCoords;
import sonar.logistics.registries.BlockRegistry;
import sonar.logistics.registries.EmitterRegistry;

public class TileEntityDataReceiver extends TileEntityNode implements IChannelProvider, IInfoEmitter {

	public List<IdentifiedCoords> emitters;
	public List<IdentifiedCoords> lastemitters;

	public SyncIdentifiedCoords emitter = new SyncIdentifiedCoords(0);

	@Override
	public ExternalCoords getChannel() {
		if (emitter.getCoords() != null)
			return new ExternalCoords(emitter.getCoords(), ForgeDirection.getOrientation(this.getBlockMetadata()));
		return null;
	}

	@Override
	public ILogicInfo currentInfo() {
		if (emitter.getCoords() != null) {
			return new LogicInfo((byte) -1, "DEFAULT", "Connected: ", emitter.getCoords().blockCoords.toString());
		} else {
			return new LogicInfo((byte) -1, "DEFAULT", "Connection: ", "NOT CONNECTED");
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
		if (emitter.getCoords() != null) {
			TileEntity tile = emitter.getCoords().blockCoords.getTileEntity();
			if (tile != null && tile instanceof TileEntityDataEmitter) {
				TileEntityDataEmitter dataEmitter = (TileEntityDataEmitter) tile;
				emitter.setCoords(new IdentifiedCoords(dataEmitter.clientName.getObject(), new ItemStack(BlockRegistry.dataEmitter), emitter.getCoords().blockCoords));
			}
		}
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
			NBTTagList list = nbt.getTagList("COORDS", 10);
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
						emitters.set(slot, IdentifiedCoords.readFromNBT(compound));
					else
						emitters.add(slot, IdentifiedCoords.readFromNBT(compound));
					break;
				case 1:
					String name = compound.getString("Name");
					if (name != null) {
						emitters.set(slot, new IdentifiedCoords(name, new ItemStack(BlockRegistry.dataEmitter), emitters.get(slot).blockCoords));
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
			NBTTagList list = nbt.getTagList("COORDS", 10);
			this.emitters = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.emitters.add(IdentifiedCoords.readFromNBT(compound));

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
				IdentifiedCoords current = null;
				IdentifiedCoords last = null;
				if (i < this.emitters.size()) {
					current = this.emitters.get(i);
				}
				if (i < this.lastemitters.size()) {
					last = this.lastemitters.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!BlockCoords.equalCoords(current.blockCoords, last.blockCoords)) {
							compound.setByte("f", (byte) 0);
							this.lastemitters.set(i, current);
							IdentifiedCoords.writeToNBT(compound, this.emitters.get(i));

						} else if (!current.coordString.equals(last.coordString)) {
							compound.setByte("f", (byte) 1);
							this.lastemitters.set(i, current);
							compound.setString("Name", current.coordString);
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastemitters.add(i, current);
						IdentifiedCoords.writeToNBT(compound, this.emitters.get(i));
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
				nbt.setTag("COORDS", list);
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
					IdentifiedCoords.writeToNBT(compound, this.emitters.get(i));
					list.appendTag(compound);
				}
			}
			nbt.setTag("COORDS", list);
		}
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote) {
			this.addConnections();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			this.removeConnections();
		}
	}

	@Override
	public void addConnections() {
		if (!this.worldObj.isRemote) {
			LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(this.getBlockMetadata()));
		}
	}

	@Override
	public void removeConnections() {
		if (!this.worldObj.isRemote) {
			LogisticsAPI.getCableHelper().removeConnection(this, ForgeDirection.getOrientation(this.getBlockMetadata()));
		}
	}

}