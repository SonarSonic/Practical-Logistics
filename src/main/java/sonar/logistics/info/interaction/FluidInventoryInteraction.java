package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.api.StoredFluidStack;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.types.FluidInventoryInfo;
import sonar.logistics.network.packets.PacketScreenInteraction;
import sonar.logistics.registries.CacheRegistry;

public class FluidInventoryInteraction extends InfoInteractionHandler<FluidInventoryInfo> {

	@Override
	public String getName() {
		return "Fluid Inventory";
	}

	@Override
	public void handleInteraction(FluidInventoryInfo info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact) {
		// INetworkCache cache = CacheRegistry.getCache(info.cacheID);
		// if (cache == null || cache.getNetworkID() == -1) {
		// return;
		// }
		ForgeDirection dir = ForgeDirection.getOrientation(interact.side);

		BlockCoords screenCoords = new BlockCoords(screen);
		if (interact.type == BlockInteractionType.RIGHT) {
			Logistics.network.sendToServer(new PacketScreenInteraction.PacketFluidStack(screen.xCoord, screen.yCoord, screen.zCoord, x, y, z, interact, null));
			// LogisticsAPI.getFluidHelper().drainHeldItem(player, cache);
		}
		if (screen instanceof ILargeDisplay) {
			ILargeDisplay largeScreen = (ILargeDisplay) screen;
			LargeScreenSizing sizing = largeScreen.getSizing();
			if (sizing != null) {
				int slot = -1;
				if (dir == ForgeDirection.NORTH) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH));
					int yPos = (sizing.maxY - (y - screenCoords.getY()));
					int hPos = (sizing.maxH - (x - screenCoords.getX()));
					slot = ((yPos * hSlots) + hPos) + (yPos);
				} else if (dir == ForgeDirection.SOUTH) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH));
					int yPos = (sizing.maxY - (y - screenCoords.getY()));
					int hPos = (sizing.maxH - sizing.minH + (x - screenCoords.getX()));
					slot = ((yPos * hSlots) + hPos) + (yPos) - sizing.maxH;
				} else if (dir == ForgeDirection.EAST) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH));
					int yPos = (sizing.maxY - (y - screenCoords.getY()));
					int hPos = (sizing.maxH - (z - screenCoords.getZ()));
					slot = ((yPos * hSlots) + hPos) + (yPos);
				} else if (dir == ForgeDirection.WEST) {
					int hSlots = (Math.round(sizing.maxH - sizing.minH));
					int yPos = (sizing.maxY - (y - screenCoords.getY()));
					int hPos = (sizing.maxH - sizing.minH + (z - screenCoords.getZ()));
					slot = ((yPos * hSlots) + hPos) + (yPos) - sizing.maxH;
				}
				if (slot >= 0 && slot < info.stacks.fluids.size()) {
					StoredFluidStack stack = info.stacks.fluids.get(slot);
					if (stack != null) {
						Logistics.network.sendToServer(new PacketScreenInteraction.PacketFluidStack(screen.xCoord, screen.yCoord, screen.zCoord, x, y, z, interact, stack));
						/*
						 * if (interact.type == BlockInteractionType.LEFT) { LogisticsAPI.getFluidHelper().fillHeldItem(player, cache, stack.setStackSize(1000)); } else if (interact.type == BlockInteractionType.SHIFT_LEFT) { LogisticsAPI.getFluidHelper().fillHeldItem(player, cache, stack); }
						 */
					}
				}
			}
		}
	}

}
