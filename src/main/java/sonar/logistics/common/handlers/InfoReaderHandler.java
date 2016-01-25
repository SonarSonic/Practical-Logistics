package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.CategoryInfo;

public class InfoReaderHandler extends TileHandler implements IWailaInfo {

	public InfoReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public List<Info> clientInfo;
	public List<Info> lastInfo;

	public SyncGeneric<Info> primaryInfo = new SyncGeneric(Logistics.infoTypes, 0);
	public SyncGeneric<Info> secondaryInfo = new SyncGeneric(Logistics.infoTypes, 1);
	public BlockCoords coords;

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		updateData(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
	}

	public void updateData(TileEntity te, ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(te, dir.getOpposite());
		target = FMPHelper.checkObject(target);
		if (target == null) {
			this.setCoords(null);
		} else {
			if (target instanceof TileEntityBlockNode || target instanceof TileEntityEntityNode) {
				TileEntity node = (TileEntity) target;
				this.setCoords(new BlockCoords(node, node.getWorldObj().provider.dimensionId));
			} else {
				this.setCoords(null);
			}
		}

		if (this.primaryInfo.getObject() != null) {
			if (this.coords != null) {
				TileEntity tile = this.coords.getTileEntity();
				if (tile != null && tile instanceof TileEntityBlockNode) {
					this.setData(te, InfoHelper.getLatestTileInfo(primaryInfo.getObject(), (TileEntityBlockNode) tile), true);
					this.setData(te, InfoHelper.getLatestTileInfo(secondaryInfo.getObject(), (TileEntityBlockNode) tile), false);
				} else if (tile != null && tile instanceof TileEntityEntityNode) {
					this.setData(te, InfoHelper.getLatestEntityInfo(primaryInfo.getObject(), (TileEntityEntityNode) tile), true);
					this.setData(te, InfoHelper.getLatestEntityInfo(secondaryInfo.getObject(), (TileEntityEntityNode) tile), false);
				} else {
					this.setData(te, null, true);
					this.setData(te, null, false);
				}
			} else {
				this.setData(te, null, true);
				this.setData(te, null, false);
			}
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
			this.primaryInfo.getObject().emptyData();

		} else if (!primary && this.secondaryInfo.getObject() != null) {
			this.secondaryInfo.getObject().emptyData();
		}

		CableHelper.updateAdjacentCoord(te, new BlockCoords(te.xCoord, te.yCoord, te.zCoord), false, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));

	}

	@Override
	public void removed(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		Object tile = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (cable.getCoords() != null) {
					cable.setCoords(null);
				}
			}
		}

	}

	public void sendAvailableData(TileEntity te, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {

			if (this.coords != null) {
				this.lastInfo = clientInfo;
				TileEntity tile = this.coords.getTileEntity();
				List<Info> info = new ArrayList();

				if (tile != null && tile instanceof TileEntityBlockNode) {
					info = InfoHelper.getTileInfo((TileEntityBlockNode) tile);
				} else if (tile != null && tile instanceof TileEntityEntityNode) {
					info = InfoHelper.getEntityInfo((TileEntityEntityNode) tile);
				} else {
					info = new ArrayList();
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
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			primaryInfo.readFromNBT(nbt, type);
			secondaryInfo.readFromNBT(nbt, type);
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
						clientInfo.add(slot, Logistics.infoTypes.readFromNBT(compound));
					break;
				case 1:
					long stored = compound.getLong("Stored");
					if (stored != 0) {
						clientInfo.set(slot, Logistics.infoTypes.readFromNBT(compound));
					} else {
						clientInfo.set(slot, null);

					}
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
			this.primaryInfo.writeToNBT(nbt, type);
			this.secondaryInfo.writeToNBT(nbt, type);

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
						if (!current.isEqualType(last) || !current.isDataEqualType(last)) {
							compound.setByte("f", (byte) 0);
							this.lastInfo.set(i, current);
							Logistics.infoTypes.writeToNBT(compound, this.clientInfo.get(i));
						} else if (!current.isDataEqualType(last)) {
							/*
							 * compound.setByte("f", (byte) 1);
							 * this.lastInfo.set(i, current);
							 * InfoHelper.writeInfo(compound,
							 * this.clientInfo.get(i));
							 */
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

	@Override
	public List<String> getWailaInfo(List<String> currenttip) {
		//currenttip.add("TESTING WAILA!");
		return currenttip;
	}

}
