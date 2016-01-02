package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.packets.PacketProviders;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityInfoReader extends TileEntityNode implements IDataConnection, IInfoReader {

	// the latest data
	public List<Info> clientInfo;

	// the last sent data
	public List<Info> lastInfo;

	public Info primaryInfo;
	public Info secondaryInfo;
	public BlockCoords coords;

	@Override
	public Info currentInfo() {

		if (secondaryInfo == null || !this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {

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

	@Override
	public Info getSecondaryInfo() {
		if (primaryInfo == null || !this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {

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

	public void setData(Info info, boolean primary) {

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

		CableHelper.updateAdjacentCoord(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, ForgeDirection.getOrientation(this.getBlockMetadata()));

	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(this.getBlockMetadata())) || dir.equals(ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite());
	}

	public void updateEntity() {
		super.updateEntity();
		if (worldObj.isRemote) {
			return;
		}
		updateData(ForgeDirection.getOrientation(this.getBlockMetadata()));
	}

	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
		}
	}

	@Override
	public void updateData(ForgeDirection dir) {

		Object target = CableHelper.getConnectedTile(this, dir.getOpposite());

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
					this.setData(InfoHelper.getLatestTileInfo(primaryInfo, (TileEntityBlockNode) tile), true);
					this.setData(InfoHelper.getLatestTileInfo(secondaryInfo, (TileEntityBlockNode) tile), false);
				} else if (tile != null && tile instanceof TileEntityEntityNode) {
					this.setData(InfoHelper.getLatestEntityInfo(primaryInfo, (TileEntityEntityNode) tile), true);
					this.setData(InfoHelper.getLatestEntityInfo(secondaryInfo, (TileEntityEntityNode) tile), false);
				} else {
					this.setData(null, true);
					this.setData(null, false);
				}
			} else {
				this.setData(null, true);
				this.setData(null, false);
			}
		}

	}

	public void sendAvailableData(EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			if (this.coords != null) {
				TileEntity te = this.coords.getTileEntity();
				if (te instanceof TileEntityBlockNode) {
					Logistics.network.sendTo(new PacketProviders(xCoord, yCoord, zCoord, InfoHelper.getTileInfo((TileEntityBlockNode) te)), (EntityPlayerMP) player);
				} else if (te instanceof TileEntityEntityNode) {
					Logistics.network.sendTo(new PacketProviders(xCoord, yCoord, zCoord, InfoHelper.getEntityInfo((TileEntityEntityNode) te)), (EntityPlayerMP) player);

				}
			} else {
				Logistics.network.sendTo(new PacketProviders(xCoord, yCoord, zCoord, null), (EntityPlayerMP) player);
			}
		}
	}

	public boolean maxRender() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		if (primaryInfo != null) {
			currenttip.add("Current Data: " + primaryInfo.getDisplayableData());
		}
		currenttip.add("Owner: " + playerName);
		return currenttip;
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
