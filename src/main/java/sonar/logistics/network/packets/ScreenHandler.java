package sonar.logistics.network.packets;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.DisplayScreenHandler;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.registries.DisplayRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public abstract class ScreenHandler<T extends PacketCoords> implements IMessageHandler<T, IMessage> {

	public abstract void handlePacket(TileEntity te, T message, MessageContext ctx, ILogicInfo screenInfo, EntityPlayerMP player, boolean doubleClick);

	@Override
	public final IMessage onMessage(T message, MessageContext ctx) {
		EntityPlayerMP player = ctx.getServerHandler().playerEntity;
		if (player != null) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			TileHandler handler = FMPHelper.getHandler(te);
			if (handler != null) {
				if (handler instanceof DisplayScreenHandler) {
					DisplayScreenHandler screen = (DisplayScreenHandler) handler;

					boolean doubleClick = false;
					if (te.getWorldObj().getTotalWorldTime() - screen.lastClickTime < 10 && player.getPersistentID().equals(screen.lastClickUUID)) {
						doubleClick = true;
					}
					screen.lastClickTime = world.getTotalWorldTime();
					screen.lastClickUUID = player.getPersistentID();
					//need to get clicked info instead and dimensions to this...need to have screen layout :)
					ILogicInfo screenInfo = screen.dInfo[0];
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
										screenInfo = handlerDisplay.dInfo[0];
										found = true;
										break;
									}
								}
							}
							if (!found)
								return null;
						}
					}
					INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
					if (network == null) {
						return null;
					}
					ScreenType screenType = ScreenType.NORMAL;
					if (te instanceof ILargeDisplay) {
						screenType = ScreenType.LARGE;
						if (((ILargeDisplay) te).getSizing() != null) {
							screenType = ScreenType.CONNECTED;
						}
					}
					handlePacket(te, message, ctx, screenInfo, player, doubleClick);
				}

			}
		}

		return null;
	}
}