package sonar.logistics.integration.multipart;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.LogisticsGui;
import sonar.logistics.network.packets.PacketProviders;
import sonar.logistics.registries.BlockRegistry;
import codechicken.lib.vec.Cuboid6;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class InfoReaderPart extends SonarTilePart implements IDataConnection, IWailaInfo, IInfoReader {

	public List<Info> info;
	public Info primaryInfo, secondaryInfo;
	public BlockCoords coords;
	public String playerName;

	public InfoReaderPart() {
		super();
	}

	public InfoReaderPart(int meta) {
		super(meta);
	}

	@Override
	public Cuboid6 getBounds() {
		if (meta == 2 || meta == 3) {
			return new Cuboid6(6 * 0.0625, 6 * 0.0625, 0.0F, 1.0F - 6 * 0.0625, 1.0F - 6 * 0.0625, 1.0F);
		}
		if (meta == 4 || meta == 5) {
			return new Cuboid6(0.0F, 6 * 0.0625, 6 * 0.0625, 1.0F, 1.0F - 6 * 0.0625, 1.0F - 6 * 0.0625);
		}
		return new Cuboid6(4 * 0.0625, 4 * 0.0625, 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625, 1 - 4 * 0.0625);

	}

	@Override
	public Object getSpecialRenderer() {
		return new RenderHandlers.InfoNode();
	}

	@Override
	public Block getBlock() {
		return BlockRegistry.infoReader;
	}

	@Override
	public String getType() {
		return "Info Reader";
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(meta)) || dir.equals(ForgeDirection.getOrientation(meta).getOpposite());
	}

	public void update() {
		if (!world().isRemote) {
			updateData(ForgeDirection.getOrientation(meta));
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

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		if (primaryInfo != null) {
			currenttip.add("Current Data: " + primaryInfo.getDisplayableData());
		}
		currenttip.add("Owner: " + playerName);
		return currenttip;
	}

	public void setData(Info info) {
		if (info != null) {
			this.primaryInfo = info;
		} else if (this.info != null) {
			this.primaryInfo.emptyData();
			;
		}
		CableHelper.updateAdjacentCoord(tile(), new BlockCoords(this.x(), this.y(), this.z()), false, ForgeDirection.getOrientation(meta));
	}

	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
		}
	}

	@Override
	public void updateData(ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(tile(), dir.getOpposite());
		target = FMPHelper.checkObject(target);
		if (target == null) {
			this.setCoords(null);
		} else {
			if (target instanceof TileEntityBlockNode) {
				TileEntityBlockNode node = (TileEntityBlockNode) target;
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

	public boolean activate(EntityPlayer player, MovingObjectPosition pos, ItemStack stack) {
		if (player != null) {
			if (player.getGameProfile().getName().equals(playerName)) {
				sendAvailableData(player);
				player.openGui(Logistics.instance, LogisticsGui.infoNode, tile().getWorldObj(), x(), y(), z());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onWorldSeparate() {
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		Object tile = world().getTileEntity(x() + dir.offsetX, y() + dir.offsetY, z() + dir.offsetZ);
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

	public void sendAdditionalPackets(EntityPlayer player) {
		sendAvailableData(player);
	}

	@Override
	public Info currentInfo() {

		if (secondaryInfo == null || !this.world().isBlockIndirectlyGettingPowered(x(), y(), z())) {

			if (primaryInfo == null) {
				return new StandardInfo((byte)-1, "", "", "NO DATA");
			}
			return primaryInfo;
		} else {
			if (secondaryInfo == null) {
				return new StandardInfo((byte)-1, "", "", "NO DATA");
			}
			return secondaryInfo;
		}
	}

	@Override
	public Info getSecondaryInfo() {
		if (primaryInfo == null || !this.world().isBlockIndirectlyGettingPowered(x(), y(), z())) {

			if (secondaryInfo == null) {
				return new StandardInfo((byte)-1, "", "", "NO DATA");
			}
			return secondaryInfo;
		} else {
			if (primaryInfo == null) {
				return new StandardInfo((byte)-1, "", "", "NO DATA");
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
		CableHelper.updateAdjacentCoord(tile(), new BlockCoords(this.x(), this.y(), this.z()), false, ForgeDirection.getOrientation(meta));

	}

	public void sendAvailableData(EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			if (this.coords != null) {
				TileEntity te = this.coords.getTileEntity();
				if (te instanceof TileEntityBlockNode) {
					Logistics.network.sendTo(new PacketProviders(this.x(), this.y(), this.z(), InfoHelper.getTileInfo((TileEntityBlockNode) te)), (EntityPlayerMP) player);
				}
			} else {
				Logistics.network.sendTo(new PacketProviders(this.x(), this.y(), this.z(), null), (EntityPlayerMP) player);
			}
		}
	}

	public boolean maxRender() {
		return true;
	}

}
