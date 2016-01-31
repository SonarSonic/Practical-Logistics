package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.SyncString;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.network.SyncIdentifiedCoords;
import sonar.logistics.registries.EmitterRegistry;

public class ChannelSelectorHandler extends TileHandler {

	public List<IdentifiedCoords> channels;
	public List<IdentifiedCoords> lastChannels;

	public SyncIdentifiedCoords channel = new SyncIdentifiedCoords(0);

	public ChannelSelectorHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		//System.out.print(channels.size());
	}

	public void sendAvailableData(TileEntity te, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			List<BlockCoords> coords = CableHelper.getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		//	System.out.print(coords.size());
			channels = new ArrayList();
			for (BlockCoords coord : coords) {
				TileEntity target = coord.getTileEntity();
				if (target != null) {
					String name = StatCollector.translateToLocal(target.getBlockType().getLocalizedName());
					channels.add(new IdentifiedCoords(name, coord));
				}
			}
			NBTTagCompound syncData = new NBTTagCompound();
			writeData(syncData, SyncType.SYNC);
			SonarCore.network.sendTo(new PacketTileSync(te.xCoord, te.yCoord, te.zCoord, syncData, SyncType.SYNC), (EntityPlayerMP) player);
		}

	}

	public IdentifiedCoords getChannel(TileEntity te) {
		IdentifiedCoords currentCoords = channel.getCoords();
		if (currentCoords == null) {
			return null;
		}
		List<BlockCoords> coords = CableHelper.getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		for (BlockCoords coord : coords) {
			if(BlockCoords.equalCoords(coord, currentCoords.coords)){
				return currentCoords;
			}
		}
		return null;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			channel.readFromNBT(nbt, type);
		}
		if (type == SyncType.SPECIAL) {
			channel.readFromNBT(nbt, SyncType.SYNC);

			if (nbt.hasKey("null")) {
				this.channels = new ArrayList();
				return;
			}
			NBTTagList list = nbt.getTagList("COORDS", 10);
			if (this.channels == null) {
				this.channels = new ArrayList();
			}
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				byte slot = compound.getByte("Slot");
				boolean set = slot < channels.size();
				switch (compound.getByte("f")) {
				case 0:
					if (set)
						channels.set(slot, IdentifiedCoords.readFromNBT(compound));
					else
						channels.add(slot, IdentifiedCoords.readFromNBT(compound));
					break;
				case 1:
					String name = compound.getString("Name");
					if (name != null) {
						channels.set(slot, new IdentifiedCoords(name, channels.get(slot).coords));
					} else {
						channels.set(slot, null);
					}
					break;
				case 2:
					if (set)
						channels.set(slot, null);
					else
						channels.add(slot, null);
					break;
				}

			}

		}
		if (type == SyncType.SYNC) {
			channel.readFromNBT(nbt, SyncType.SAVE);
			NBTTagList list = nbt.getTagList("COORDS", 10);
			this.channels = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.channels.add(IdentifiedCoords.readFromNBT(compound));

			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			channel.writeToNBT(nbt, type);
		}
		if (type == SyncType.SPECIAL) {
			channel.writeToNBT(nbt, SyncType.SYNC);

			if (channels == null) {
				channels = new ArrayList();
			}
			if (lastChannels == null) {
				lastChannels = new ArrayList();
			}
			if (this.channels.size() <= 0 && (!(this.lastChannels.size() <= 0))) {
				nbt.setBoolean("null", true);
				this.lastChannels = new ArrayList();
				return;
			}
			NBTTagList list = new NBTTagList();
			int size = Math.max(this.channels.size(), this.lastChannels.size());
			for (int i = 0; i < size; ++i) {
				IdentifiedCoords current = null;
				IdentifiedCoords last = null;
				if (i < this.channels.size()) {
					current = this.channels.get(i);
				}
				if (i < this.lastChannels.size()) {
					last = this.lastChannels.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!BlockCoords.equalCoords(current.coords, last.coords)) {
							compound.setByte("f", (byte) 0);
							this.lastChannels.set(i, current);
							IdentifiedCoords.writeToNBT(compound, this.channels.get(i));

						} else if (!current.name.equals(last.name)) {
							compound.setByte("f", (byte) 1);
							this.lastChannels.set(i, current);
							compound.setString("Name", current.name);
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastChannels.add(i, current);
						IdentifiedCoords.writeToNBT(compound, this.channels.get(i));
					}
				} else if (last != null) {
					this.lastChannels.set(i, null);
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
			channel.writeToNBT(nbt, SyncType.SAVE);
			NBTTagList list = new NBTTagList();
			if (channels == null) {
				channels = new ArrayList();
			}
			for (int i = 0; i < this.channels.size(); i++) {
				if (this.channels.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setByte("Slot", (byte) i);
					IdentifiedCoords.writeToNBT(compound, this.channels.get(i));
					list.appendTag(compound);
				}
			}
			nbt.setTag("COORDS", list);
		}
	}

	public int canRenderConnection(TileEntity te, ForgeDirection dir) {
		return CableHelper.canRenderConnection(te, dir);
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return true;
	}
}
