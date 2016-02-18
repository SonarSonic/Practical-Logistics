package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.StoredStackInfo;

public class StoredFluidInteraction extends InfoInteractionHandler<FluidStackInfo> {

	@Override
	public String getName() {
		return "Fluid Stack";
	}

	@Override
	public boolean canHandle(ScreenType type, TileEntity te, TileEntity object) {
		TileHandler handler = FMPHelper.getHandler(object);
		return handler != null && handler instanceof FluidReaderHandler;
	}

	@Override
	public void handleInteraction(FluidStackInfo info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		FluidReaderHandler handler = (FluidReaderHandler) FMPHelper.getHandler(reader);
		if (interact.type == BlockInteractionType.RIGHT) {
			handler.emptyFluid(player, reader, player.getHeldItem());
		} else if (interact.type == BlockInteractionType.LEFT) {
			handler.fillItemStack(player, reader, info.stack.setStackSize(1000));
		} else if (interact.type == BlockInteractionType.SHIFT_LEFT) {
			handler.fillItemStack(player, reader, info.stack);
		}
	}

}
