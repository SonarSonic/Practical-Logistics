package sonar.logistics.common.handlers;

import java.util.List;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McMetaPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.SonarAPI;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.packets.PacketProviders;

public class InfoReaderHandler extends TileHandler {

	public InfoReaderHandler(boolean isMultipart) {
		super(isMultipart);
	}

	public List<Info> clientInfo;
	public List<Info> lastInfo;

	public Info primaryInfo;
	public Info secondaryInfo;
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

		if (this.primaryInfo != null) {
			if (this.coords != null) {
				TileEntity tile = this.coords.getTileEntity();
				if (tile != null && tile instanceof TileEntityBlockNode) {
					this.setData(te, InfoHelper.getLatestTileInfo(primaryInfo, (TileEntityBlockNode) tile), true);
					this.setData(te, InfoHelper.getLatestTileInfo(secondaryInfo, (TileEntityBlockNode) tile), false);
				} else if (tile != null && tile instanceof TileEntityEntityNode) {
					this.setData(te, InfoHelper.getLatestEntityInfo(primaryInfo, (TileEntityEntityNode) tile), true);
					this.setData(te, InfoHelper.getLatestEntityInfo(secondaryInfo, (TileEntityEntityNode) tile), false);
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

		if (secondaryInfo == null || !te.getWorldObj().isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) {

			if (primaryInfo == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return primaryInfo;
		} else {
			if (secondaryInfo == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return secondaryInfo;
		}
	}

	public Info getSecondaryInfo(TileEntity te) {
		if (primaryInfo == null || !te.getWorldObj().isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) {

			if (secondaryInfo == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return secondaryInfo;
		} else {
			if (primaryInfo == null) {
				return new StandardInfo((byte) -1, "", "", "NO DATA");
			}
			return primaryInfo;
		}
	}

	public void setData(TileEntity te, Info info, boolean primary) {
		if (info != null) {
			if (primary) {
				this.primaryInfo = info;
			} else {
				this.secondaryInfo = info;
			}
		} else if (primary && this.primaryInfo != null) {
			this.primaryInfo.emptyData();

		} else if (!primary && this.secondaryInfo != null) {
			this.secondaryInfo.emptyData();
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
				TileEntity tile = this.coords.getTileEntity();
				if (tile instanceof TileEntityBlockNode) {
					Logistics.network.sendTo(new PacketProviders(te.xCoord, te.yCoord, te.zCoord, InfoHelper.getTileInfo((TileEntityBlockNode) tile)), (EntityPlayerMP) player);
				} else if (tile instanceof TileEntityEntityNode) {
					Logistics.network.sendTo(new PacketProviders(te.xCoord, te.yCoord, te.zCoord, InfoHelper.getEntityInfo((TileEntityEntityNode) tile)), (EntityPlayerMP) player);

				}
			} else {
				Logistics.network.sendTo(new PacketProviders(te.xCoord, te.yCoord, te.zCoord, null), (EntityPlayerMP) player);
			}
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (nbt.getBoolean("hasPrimary")) {
				if (nbt.hasKey("primary")) {
					primaryInfo = InfoHelper.readInfo(nbt.getCompoundTag("primary"));
				}
			} else {
				primaryInfo = null;
			}

			if (nbt.getBoolean("hasSecondary")) {
				if (nbt.hasKey("secondaryInfo")) {
					secondaryInfo = InfoHelper.readInfo(nbt.getCompoundTag("secondaryInfo"));
				}
			} else {
				secondaryInfo = null;
			}
			if (nbt.hasKey("coords")) {
				if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
					coords = BlockCoords.readFromNBT(nbt.getCompoundTag("coords"));
				} else {
					coords = null;
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (primaryInfo != null) {
				nbt.setBoolean("hasPrimary", true);
				NBTTagCompound infoTag = new NBTTagCompound();
				InfoHelper.writeInfo(infoTag, primaryInfo);
				nbt.setTag("primary", infoTag);
			} else {
				nbt.setBoolean("hasPrimary", false);
			}
			if (secondaryInfo != null) {
				nbt.setBoolean("hasSecondary", true);
				NBTTagCompound infoTag = new NBTTagCompound();
				InfoHelper.writeInfo(infoTag, secondaryInfo);
				nbt.setTag("secondaryInfo", infoTag);
			} else {
				nbt.setBoolean("hasSecondary", false);
			}
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
	}

}
