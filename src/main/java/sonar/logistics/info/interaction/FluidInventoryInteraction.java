package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.info.types.FluidInventoryInfo;

public class FluidInventoryInteraction extends InfoInteractionHandler<FluidInventoryInfo> {

	@Override
	public String getName() {
		return "Fluid Inventory";
	}

	@Override
	public boolean canHandle(ScreenType type, TileEntity te, TileEntity object) {
		TileHandler handler = FMPHelper.getHandler(object);
		return handler != null && handler instanceof FluidReaderHandler;
	}

	@Override
	public void handleInteraction(FluidInventoryInfo info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		FluidReaderHandler handler = (FluidReaderHandler) FMPHelper.getHandler(reader);
		ForgeDirection dir = ForgeDirection.getOrientation(interact.side);

		BlockCoords screenCoords = new BlockCoords(screen);
		if (interact.type == BlockInteractionType.RIGHT) {
			handler.emptyFluid(player, reader, player.getHeldItem());
		} else {

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
				if (slot >= 0 && slot < info.stacks.size()) {
					StoredFluidStack stack = info.stacks.get(slot);
					if (stack != null) {
						if (interact.type == BlockInteractionType.LEFT) {
							handler.fillItemStack(player, reader, stack.setStackSize(1000));
						} else if (interact.type == BlockInteractionType.SHIFT_LEFT) {
							handler.fillItemStack(player, reader, stack);
						}
					}
				}
			}
		}
	}

}
