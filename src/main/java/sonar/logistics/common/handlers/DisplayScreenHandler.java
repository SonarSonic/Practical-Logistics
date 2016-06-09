package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
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
import sonar.logistics.api.connecting.IInfoScreen;
import sonar.logistics.api.connecting.IInfoScreen.ScreenLayout;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.registries.DisplayRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DisplayScreenHandler extends TileHandler implements IByteBufTile {

	public ILogicInfo[] dInfo = new ILogicInfo[] { InfoHelper.empty, InfoHelper.empty, InfoHelper.empty, InfoHelper.empty };
	public ILogicInfo[] updateDInfo = new ILogicInfo[] { InfoHelper.empty, InfoHelper.empty, InfoHelper.empty, InfoHelper.empty };

	public int updateTicks, updateTime = 20;

	public long lastClickTime;
	public UUID lastClickUUID;

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
			// SonarCore.sendPacketAround(te, 64, 0);
		} else
			updateTicks++;

	}

	public void updateData(TileEntity te, TileEntity packetTile, ForgeDirection dir) {
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		if (network == null) {
			return;
		}

		ArrayList<BlockCoords> emitters = network.getConnections(CacheTypes.EMITTER, true);
		for (int i = 0; i < 4 && i < emitters.size(); i++) {
			Object target = FMPHelper.checkObject(emitters.get(i).getTileEntity());
			if (target == null) {
				syncNewInfo(packetTile, i, null);
				continue;
			}
			ILogicInfo current = null;
			if (target instanceof IInfoReader) {
				IInfoReader infoReader = (IInfoReader) target;
				ILogicInfo[] currentInfo = infoReader.getSelectedInfo();
				if (currentInfo[0] != null && currentInfo[1] != null) {
					current = LogisticsAPI.getInfoHelper().combineData(currentInfo[0], currentInfo[1]);
				} else if (currentInfo[0] != null) {
					current = currentInfo[0];
				}

			} else if (target instanceof IInfoEmitter) {
				IInfoEmitter infoNode = (IInfoEmitter) target;
				current = infoNode.currentInfo();

			} else {
				syncNewInfo(packetTile, i, null);
				continue;
			}
			syncNewInfo(packetTile, i, current);
		}
	}

	public void syncNewInfo(TileEntity te, int i, ILogicInfo current) {
		DisplayScreenHandler handler = (DisplayScreenHandler) FMPHelper.getHandler(te);
		if (current == null) {
			current = InfoHelper.empty;
		}
		handler.updateDInfo[i] = current;
		if (handler.dInfo[i] == null) {
			handler.dInfo[i] = handler.updateDInfo[i];
			SonarCore.sendPacketAround(te, 64, i + 10);
		} else if (handler.dInfo[i] != null) {
			SyncType type = handler.dInfo[i].getNextSyncType(handler.updateDInfo[i]);
			if (type != null) {
				if (type == SyncType.SAVE) {
					handler.dInfo[i] = handler.updateDInfo[i];
					SonarCore.sendPacketAround(te, 64, i + 10);
				} else if (type == SyncType.SYNC) {
					SonarCore.sendPacketAround(te, 64, i + 20);
				}
			}
		}
	}

	public void screenClicked(World world, EntityPlayer player, int x, int y, int z, BlockInteraction interact) {
		TileEntity te = world.getTileEntity(x, y, z);
		if (interact.side != FMPHelper.getMeta(te)) {
			return;
		}
		/*
		 * boolean doubleClick = false; if (world.getTotalWorldTime() - lastClickTime < 10 && player.getPersistentID().equals(lastClickUUID)) { doubleClick = true; } lastClickTime = world.getTotalWorldTime(); lastClickUUID = player.getPersistentID(); ILogicInfo screenInfo = info; if (te instanceof ILargeDisplay) { List<BlockCoords> displays = DisplayRegistry.getScreens(((ILargeDisplay) te).registryID()); if (!displays.isEmpty()) { boolean found = false; for (BlockCoords display : displays) { te = display.getTileEntity(); TileHandler tilehandler = FMPHelper.getHandler(te); if (tilehandler != null && tilehandler instanceof LargeDisplayScreenHandler) { LargeDisplayScreenHandler handlerDisplay = (LargeDisplayScreenHandler) tilehandler; if (handlerDisplay.isHandler.getObject()) { screenInfo =
		 * handlerDisplay.info; found = true; break; } } } if (!found) return; } } INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()); if (network == null) { return; }
		 */
		ScreenType screenType = ScreenType.NORMAL;
		if (te instanceof ILargeDisplay) {
			screenType = ScreenType.LARGE;
			if (((ILargeDisplay) te).getSizing() != null) {
				screenType = ScreenType.CONNECTED;
			}
		}
		ILogicInfo screenInfo = dInfo[0];
		TileEntity tile = te;
		if (screenType == ScreenType.CONNECTED || screenType == ScreenType.LARGE) {
			LargeDisplayScreenHandler handler = (LargeDisplayScreenHandler) this;
			if (!handler.isHandler.getObject()) {
				LargeDisplayScreenHandler screenHandler = handler.getHandler(te);
				if (screenHandler == null) {
					return;
				} else {
					tile = screenHandler.tile;
					screenInfo = screenHandler.dInfo[0];
				}
			}
		}
		InfoInteractionHandler handler = Logistics.infoInteraction.getInteractionHandler(screenInfo, screenType, tile);
		if (handler != null) {
			handler.handleInteraction(screenInfo, screenType, tile, player, x, y, z, interact);
		}

	}

	public ILogicInfo[] getDisplayInfo() {
		return dInfo;
	}

	public ScreenLayout getScreenLayout() {
		return ScreenLayout.ONE;
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		/*
		 * if (info != null) { currenttip.add("Current Data: " + info.getDisplayableData()); }
		 */
		return currenttip;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id >= 10 && id < 10 + 4) {
			if (dInfo[id - 10] != null) {
				buf.writeBoolean(true);
				Logistics.infoTypes.writeToBuf(buf, dInfo[id - 10]);
			} else {
				buf.writeBoolean(false);
			}
		} else if (id > 10 + 4) {
			NBTTagCompound tag = new NBTTagCompound();
			if (updateDInfo[id - 20] != null) {
				dInfo[id - 20].writeUpdate(updateDInfo[id - 20], tag);
				dInfo[id - 20] = updateDInfo[id - 20];
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
		if (id >= 10 && id < 10 + 4) {
			if (buf.readBoolean()) {
				dInfo[id - 10] = Logistics.infoTypes.readFromBuf(buf);
			} else {
				dInfo[id - 10] = null;
			}
		} else if (id > 10 + 4) {
			if (buf.readBoolean()) {
				NBTTagCompound tag = ByteBufUtils.readTag(buf);
				if (tag != null && dInfo[id - 20] != null)
					dInfo[id - 20].readUpdate(tag);
			}
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			for (int i = 0; i < dInfo.length; i++) {
				if (nbt.hasKey("info" + i)) {
					dInfo[i] = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag("info" + i));
				}
				if (dInfo[i] == null) {
					dInfo[i] = InfoHelper.empty;
				}
			}
			// only for helping compatibility with older versions
			if (nbt.hasKey("currentInfo")) {
				dInfo[0] = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag("currentInfo"));
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			for (int i = 0; i < dInfo.length; i++) {
				if (dInfo[i] != null) {
					NBTTagCompound infoTag = new NBTTagCompound();
					Logistics.infoTypes.writeToNBT(infoTag, dInfo[i]);
					nbt.setTag("info" + i, infoTag);
				}
			}
		}
	}
}
