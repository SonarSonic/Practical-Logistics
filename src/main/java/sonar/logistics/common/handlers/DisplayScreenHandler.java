package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockInteraction;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.registries.DisplayRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DisplayScreenHandler extends TileHandler implements IByteBufTile {

	public ILogicInfo info;
	public ILogicInfo updateInfo;

	public int updateTicks, updateTime = 20;

	private long lastClickTime;
	private UUID lastClickUUID;

	public DisplayScreenHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		this.updateData(te, te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));

		if (updateTicks == updateTime) {
			updateTicks = 0;
			SonarCore.sendPacketAround(te, 64, 0);
		} else
			updateTicks++;
	}

	public void updateData(TileEntity te, TileEntity packetTile, ForgeDirection dir) {
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		if (network == null) {
			return;
		}
		Object target = FMPHelper.getTile(network.getFirstTileEntity(CacheTypes.EMITTER));
		if (target == null) {
			info = new LogicInfo((byte) -1, "INFO", " ", "NO DATA");
			return;
		}
		ILogicInfo current = null;
		boolean shouldUpdate = true;
		if (target instanceof IInfoReader) {
			IInfoReader infoReader = (IInfoReader) target;
			ILogicInfo currentInfo = infoReader.currentInfo();
			if (infoReader.currentInfo() != null && infoReader.getSecondaryInfo() != null) {
				ILogicInfo progress = LogisticsAPI.getInfoHelper().combineData(currentInfo, infoReader.getSecondaryInfo());
				if (!progress.equals(info) || (info != null && !currentInfo.getData().equals(info.getData()))) {
					current = progress;
				} else {
					shouldUpdate = false;
				}
			} else if (currentInfo != null) {
				if (!infoReader.currentInfo().equals(info) || (info != null && !currentInfo.getData().equals(info.getData()))) {
					current = currentInfo;
				} else {
					shouldUpdate = false;
				}
			}

		} else if (target instanceof IInfoEmitter) {
			IInfoEmitter infoNode = (IInfoEmitter) target;
			ILogicInfo currentInfo = infoNode.currentInfo();
			if (currentInfo != null) {
				if (!currentInfo.equals(info) || (info != null && !currentInfo.getData().equals(info.getData()))) {
					current = infoNode.currentInfo();
				} else {
					shouldUpdate = false;
				}
			}
		} else {
			info = new LogicInfo((byte) -1, "INFO", " ", "NO DATA");
			return;
		}		
		
		updateInfo = current;
		if (shouldUpdate) {
			if (info == null) {
				if (updateInfo != null) {
					info = updateInfo;
					SonarCore.sendPacketAround(packetTile, 64, 0);
				}
			} else {
				if (updateInfo != null) {
					if (updateInfo.areTypesEqual(info)) {
						if (updateInfo instanceof LogicInfo || updateInfo instanceof ManaInfo) {
							info = updateInfo;
							SonarCore.sendPacketAround(packetTile, 64, 0);
						} else {
							SonarCore.sendPacketAround(packetTile, 64, 2);
						}
					} else {
						info = updateInfo;
						SonarCore.sendPacketAround(packetTile, 64, 0);
					}
				} else {
					info = null;
					SonarCore.sendPacketAround(packetTile, 64, 0);
				}
			}
		}
	}

	public void screenClicked(World world, EntityPlayer player, int x, int y, int z, BlockInteraction interact) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (interact.side != FMPHelper.getMeta(te)) {
			return;
		}
		boolean doubleClick = false;
		if (world.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) {
			doubleClick = true;
		}
		lastClickTime = world.getTotalWorldTime();
		lastClickUUID = player.getPersistentID();
		ILogicInfo screenInfo = info;
		if (te instanceof ILargeDisplay) {
			List<BlockCoords> displays = DisplayRegistry.getScreens(((ILargeDisplay) te).registryID());
			if (!displays.isEmpty()) {
				te = displays.get(0).getTileEntity();
				TileHandler tilehandler = FMPHelper.getHandler(te);
				if (tilehandler != null && tilehandler instanceof LargeDisplayScreenHandler) {
					LargeDisplayScreenHandler handlerDisplay = (LargeDisplayScreenHandler) tilehandler;
					if (handlerDisplay.connectedTile != null) {
						screenInfo = handlerDisplay.info;
					} else {
						return;
					}
				}
			}
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		if (network == null) {
			return;
		}
		ScreenType screenType = ScreenType.NORMAL;
		if (te instanceof ILargeDisplay) {
			screenType = ScreenType.LARGE;
			if (((ILargeDisplay) te).getSizing() != null) {
				screenType = ScreenType.CONNECTED;
			}
		}
		InfoInteractionHandler handler = Logistics.infoInteraction.getInteractionHandler(screenInfo, screenType, te);
		if (handler != null) {
			handler.handleInteraction(screenInfo, screenType, te, player, x, y, z, interact, doubleClick);
		}
	}

	public ILogicInfo currentInfo() {
		return info;
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		if (info != null) {
			currenttip.add("Current Data: " + info.getDisplayableData());
		}
		return currenttip;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			if (info != null) {
				buf.writeBoolean(true);
				Logistics.infoTypes.writeToBuf(buf, info);
			} else {
				buf.writeBoolean(false);
			}
		}
		if (id == 1) {
			ByteBufUtils.writeUTF8String(buf, info.getData());
		}
		if (id == 2) {
			NBTTagCompound tag = new NBTTagCompound();
			if (updateInfo != null && updateInfo.areTypesEqual(info)) {
				info.writeUpdate(updateInfo, tag);
			}
			if (!tag.hasNoTags()) {
				buf.writeBoolean(true);
				ByteBufUtils.writeTag(buf, tag);
			} else {
				buf.writeBoolean(false);
			}

		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			if (buf.readBoolean()) {
				info = Logistics.infoTypes.readFromBuf(buf);
			} else {
				info = null;
			}
		}
		if (id == 1) {
			LogicInfo standardInfo = (LogicInfo) info;
			standardInfo.setData(ByteBufUtils.readUTF8String(buf));
		}
		if (id == 2) {
			if (buf.readBoolean()) {
				NBTTagCompound tag = ByteBufUtils.readTag(buf);
				if (tag != null && info != null)
					info.readUpdate(tag);
			}
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			if (nbt.hasKey("currentInfo")) {
				info = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag("currentInfo"));
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			if (info != null) {
				NBTTagCompound infoTag = new NBTTagCompound();
				Logistics.infoTypes.writeToNBT(infoTag, info);
				nbt.setTag("currentInfo", infoTag);
			}
		}
	}
}
