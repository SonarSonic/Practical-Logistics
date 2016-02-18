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
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.StoredStackInfo;

public class StoredStackInteraction extends InfoInteractionHandler<StoredStackInfo> {

	@Override
	public String getName() {
		return "Stored Stack";
	}

	@Override
	public boolean canHandle(ScreenType type, TileEntity te, TileEntity object) {
		TileHandler handler = FMPHelper.getHandler(object);
		return handler != null && handler instanceof InventoryReaderHandler;
	}

	@Override
	public void handleInteraction(StoredStackInfo info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		InventoryReaderHandler handler = (InventoryReaderHandler) FMPHelper.getHandler(reader);
		
		if (interact.type == BlockInteractionType.RIGHT) {
			if (player.getHeldItem() != null && info.stack.equalStack(player.getHeldItem())) {
				if (!doubleClick) {
					handler.insertItem(player, reader, player.inventory.currentItem);
				} else {
					handler.insertInventory(player, reader, player.inventory.currentItem);
				}

			}
		} else if (interact.type != BlockInteractionType.SHIFT_RIGHT) {
			StoredItemStack extract = handler.extractItem(reader, info.stack, interact.type == BlockInteractionType.LEFT ? 1 : 64);
			if (extract != null) {
				LogisticsAPI.getItemHelper().spawnStoredItemStack(extract, screen.getWorldObj(), x, y, z, ForgeDirection.getOrientation(interact.side));
			}
		}
	}

}
