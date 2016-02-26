package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.info.types.CategoryInfo;

import com.google.common.collect.Lists;

public class InfoReaderHandler extends TileHandler implements IWailaInfo {

	public InfoReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public List<Info> clientInfo;
	public List<Info> lastInfo;

	private int primaryUpdate = 0;
	private int secondaryUpdate = 0;
	public SyncGeneric<Info> primaryInfo = (SyncGeneric<Info>) new SyncGeneric(Logistics.infoTypes, 0).addSyncType(SyncType.SPECIAL);
	public SyncGeneric<Info> secondaryInfo = (SyncGeneric<Info>) new SyncGeneric(Logistics.infoTypes, 1).addSyncType(SyncType.SPECIAL);
	public BlockCoords coords;

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		boolean primary = false;
		boolean secondary = false;
		if (primaryInfo.getObject() == null || primaryInfo.getObject().updateTicks() == 1 || primaryUpdate >= primaryInfo.getObject().updateTicks()) {
			primary = true;
			primaryUpdate = 0;
		}
		if (secondaryInfo.getObject() == null || secondaryInfo.getObject().updateTicks() == 1 || secondaryUpdate >= secondaryInfo.getObject().updateTicks()) {
			secondary = true;
			secondaryUpdate = 0;
		}
		if (primary || secondary)
			updateData(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)), primary, secondary);

		primaryUpdate++;
		secondaryUpdate++;
	}

	public void updateData(TileEntity te, ForgeDirection dir, boolean primary, boolean secondary) {

		List<BlockCoords> connections = LogisticsAPI.getCableHelper().getConnections(te, dir.getOpposite());
		List<IConnectionNode> nodes = new ArrayList();
		List<TileEntityEntityNode> entityNodes = new ArrayList();

		for (BlockCoords connect : connections) {
			Object tile = connect.getTileEntity();
			if (tile instanceof IConnectionNode) {
				nodes.add((IConnectionNode) tile);
			}
			if (tile instanceof TileEntityEntityNode) {
				entityNodes.add((TileEntityEntityNode) tile);
			}
		}

		if (!nodes.isEmpty()) {
			if (primary)
				this.setData(te, LogisticsAPI.getInfoHelper().getLatestTileInfo(primaryInfo.getObject(), nodes.get(0)), true);
			if (secondary)
				this.setData(te, LogisticsAPI.getInfoHelper().getLatestTileInfo(secondaryInfo.getObject(), (IConnectionNode) nodes.get(0)), false);
			this.coords = new BlockCoords((TileEntityBlockNode) nodes.get(0));
		} else if (!entityNodes.isEmpty()) {
			if (primary)
				this.setData(te, LogisticsAPI.getInfoHelper().getLatestEntityInfo(primaryInfo.getObject(), (TileEntityEntityNode) entityNodes.get(0)), true);
			if (secondary)
				this.setData(te, LogisticsAPI.getInfoHelper().getLatestEntityInfo(secondaryInfo.getObject(), (TileEntityEntityNode) entityNodes.get(0)), false);
			this.coords = new BlockCoords((TileEntityEntityNode) entityNodes.get(0));
		} else {
			coords = null;
			// this.setData(te, null, true);
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te))) || dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
		}
	}

	public Info currentInfo(TileEntity te) {

		if (secondaryInfo.getObject() == null || !te.getWorldObj().isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) {

			if (primaryInfo.getObject() == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return primaryInfo.getObject();
		} else {
			if (secondaryInfo.getObject() == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return secondaryInfo.getObject();
		}
	}

	public Info getSecondaryInfo(TileEntity te) {
		if (primaryInfo.getObject() == null || !te.getWorldObj().isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) {

			if (secondaryInfo.getObject() == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return secondaryInfo.getObject();
		} else {
			if (primaryInfo.getObject() == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return primaryInfo.getObject();
		}
	}

	public void setData(TileEntity te, Info info, boolean primary) {
		if (info != null) {
			if (primary) {
				this.primaryInfo.setObject(info);
			} else {
				this.secondaryInfo.setObject(info);
			}
		} else if (primary && this.primaryInfo.getObject() != null) {
			this.primaryInfo.setObject(null);

		} else if (!primary && this.secondaryInfo.getObject() != null) {
			this.secondaryInfo.setObject(null);
		}

	}

	public void sendAvailableData(TileEntity te, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {

			this.lastInfo = clientInfo;
			List<Info> info = new ArrayList();
			if (this.coords != null) {
				info = LogisticsAPI.getInfoHelper().getInfoList(coords);
			}
			List<Info> newInfo = new ArrayList();
			Info lastInfo = null;
			for (Info blockInfo : info) {
				if (lastInfo == null || !lastInfo.getCategory().equals(blockInfo.getCategory())) {
					newInfo.add(CategoryInfo.createInfo(blockInfo.getCategory()));
				}
				newInfo.add(blockInfo);
				lastInfo = blockInfo;
			}
			clientInfo = newInfo;
			NBTTagCompound tag = new NBTTagCompound();
			this.writeData(tag, SyncType.SPECIAL);
			if (!tag.hasNoTags())
				SonarCore.network.sendTo(new PacketTileSync(te.xCoord, te.yCoord, te.zCoord, tag, SyncType.SPECIAL), (EntityPlayerMP) player);

		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (type == SyncType.SAVE) {
				if (nbt.hasKey("coords")) {
					if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
						coords = BlockCoords.readFromNBT(nbt.getCompoundTag("coords"));
					} else {
						coords = null;
					}
				}
			}
		}
		if (type == SyncType.SPECIAL) {
			if (nbt.hasKey("null")) {
				this.clientInfo = new ArrayList();
				return;
			}
			NBTTagList list = nbt.getTagList("Info", 10);
			if (this.clientInfo == null) {
				this.clientInfo = new ArrayList();
			}
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				int slot = compound.getInteger("Slot");
				boolean set = slot < clientInfo.size();
				switch (compound.getByte("f")) {
				case 0:
					if (set)
						clientInfo.set(slot, Logistics.infoTypes.readFromNBT(compound));
					else
						clientInfo.add(Logistics.infoTypes.readFromNBT(compound));
					break;
				case 1:
					// clientInfo.get(slot).readUpdate(compound);
					break;
				case 2:
					clientInfo.set(slot, null);
					break;
				}
			}

		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (type == SyncType.SAVE) {
				NBTTagCompound coordTag = new NBTTagCompound();
				if (coords != null) {
					BlockCoords.writeToNBT(coordTag, coords);
					coordTag.setBoolean("hasCoords", true);
				} else {
					coordTag.setBoolean("hasCoords", false);
				}
				nbt.setTag("coords", coordTag);
			}
		}

		if (type == SyncType.SPECIAL) {
			if (clientInfo == null) {
				clientInfo = new ArrayList();
			}
			if (lastInfo == null) {
				lastInfo = new ArrayList();
			}
			if (this.clientInfo.size() <= 0 && (!(this.lastInfo.size() <= 0))) {
				nbt.setBoolean("null", true);
				this.lastInfo = new ArrayList();
				return;
			}
			NBTTagList list = new NBTTagList();
			int size = Math.max(this.clientInfo.size(), this.lastInfo.size());
			for (int i = 0; i < size; ++i) {
				Info current = null;
				Info last = null;
				if (i < this.clientInfo.size()) {
					current = this.clientInfo.get(i);
				}
				if (i < this.lastInfo.size()) {
					last = this.lastInfo.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!current.areTypesEqual(last) || !current.equals(last) || (current != null && current instanceof StandardInfo && last instanceof StandardInfo && !((StandardInfo) last).data.equals(((StandardInfo) current).data))) {
							compound.setByte("f", (byte) 0);
							this.lastInfo.set(i, current);
							Logistics.infoTypes.writeToNBT(compound, this.clientInfo.get(i));
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastInfo.add(i, current);
						Logistics.infoTypes.writeToNBT(compound, this.clientInfo.get(i));
					}
				} else if (last != null) {
					this.lastInfo.set(i, null);
					compound.setByte("f", (byte) 2);
				}
				if (!compound.hasNoTags()) {
					compound.setInteger("Slot", i);
					list.appendTag(compound);
				}

				if (list.tagCount() != 0) {
					nbt.setTag("Info", list);
				}
			}
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(primaryInfo, secondaryInfo));
	}

	@Override
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
	}

}
