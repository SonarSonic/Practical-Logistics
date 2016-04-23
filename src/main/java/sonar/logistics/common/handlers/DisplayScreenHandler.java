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
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.registries.DisplayRegistry;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class DisplayScreenHandler extends TileHandler implements IByteBufTile {

	public ILogicInfo info;
	public ILogicInfo updateInfo;

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
			//SonarCore.sendPacketAround(te, 64, 0);
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
			syncNewInfo(packetTile, null);
			return;
		}
		ILogicInfo current = null;
		if (target instanceof IInfoReader) {
			IInfoReader infoReader = (IInfoReader) target;
			ILogicInfo currentInfo = infoReader.currentInfo();
			if (infoReader.currentInfo() != null && infoReader.getSecondaryInfo() != null) {
				current = LogisticsAPI.getInfoHelper().combineData(currentInfo, infoReader.getSecondaryInfo());
			} else if (currentInfo != null) {
				current = currentInfo;
			}
		} else if (target instanceof IInfoEmitter) {
			IInfoEmitter infoNode = (IInfoEmitter) target;
			current = infoNode.currentInfo();

		} else { 
			syncNewInfo(packetTile, null);
			return;
		}
		syncNewInfo(packetTile, current);
	}
	
	public void syncNewInfo(TileEntity te, ILogicInfo current){
		DisplayScreenHandler handler = (DisplayScreenHandler) FMPHelper.getHandler(te);
		if(current==null){
			current = InfoHelper.empty;
		}
		handler.updateInfo = current;		
		if (handler.info == null) {
			handler.info = handler.updateInfo;
			SonarCore.sendPacketAround(te, 64, 0);
		} else if (handler.info != null) {
			SyncType type =handler.info.getNextSyncType(handler.updateInfo);
			if (type != null) {
				if (type == SyncType.SAVE) {
					handler.info = handler.updateInfo;
					SonarCore.sendPacketAround(te, 64, 0);
				} else if (type == SyncType.SYNC) {
					SonarCore.sendPacketAround(te, 64, 2);
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
				boolean found = false;
				for (BlockCoords display : displays) {
					te = display.getTileEntity();
					TileHandler tilehandler = FMPHelper.getHandler(te);
					if (tilehandler != null && tilehandler instanceof LargeDisplayScreenHandler) {
						LargeDisplayScreenHandler handlerDisplay = (LargeDisplayScreenHandler) tilehandler;
						if (handlerDisplay.isHandler.getObject()) {
							screenInfo = handlerDisplay.info;
							found = true;
							break;
						}
					}
				}
				if (!found)
					return;
			}
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		if (network == null) {
			return;
		}
		*/
		ScreenType screenType = ScreenType.NORMAL;
		if (te instanceof ILargeDisplay) {
			screenType = ScreenType.LARGE;
			if (((ILargeDisplay) te).getSizing() != null) {
				screenType = ScreenType.CONNECTED;
			}
		}
		ILogicInfo screenInfo = info;
		TileEntity tile = te;
		if(screenType==ScreenType.CONNECTED || screenType==ScreenType.LARGE){
			LargeDisplayScreenHandler handler = (LargeDisplayScreenHandler) this;
			if(!handler.isHandler.getObject()){
				LargeDisplayScreenHandler screenHandler = handler.getHandler(te);
				if(screenHandler==null){
					return;
				}else{			
					tile = screenHandler.tile;
					screenInfo=screenHandler.info;					
				}
			}
		}
		InfoInteractionHandler handler = Logistics.infoInteraction.getInteractionHandler(screenInfo, screenType, tile);
		if (handler != null) {
			handler.handleInteraction(screenInfo, screenType, tile, player, x, y, z, interact);
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
		//if (id == 1) {
		//	ByteBufUtils.writeUTF8String(buf, info.getData());
		//}
		if (id == 2) {
			NBTTagCompound tag = new NBTTagCompound();
			if (updateInfo != null) {
				info.writeUpdate(updateInfo, tag);
				info = updateInfo;
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
		//if (id == 1) {
		//	LogicInfo standardInfo = (LogicInfo) info;
		//	standardInfo.setData(ByteBufUtils.readUTF8String(buf));
		//}
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
