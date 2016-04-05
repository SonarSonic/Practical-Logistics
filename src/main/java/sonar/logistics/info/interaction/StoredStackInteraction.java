package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.registries.CacheRegistry;

public class StoredStackInteraction extends InfoInteractionHandler<StoredStackInfo> {

	@Override
	public String getName() {
		return "Stored Stack";
	}

	@Override
	public void handleInteraction(StoredStackInfo info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		INetworkCache cache = CacheRegistry.getCache(info.cacheID);
		if (cache == null || cache.getNetworkID() == -1) {
			return;
		}
		if (interact.type == BlockInteractionType.RIGHT) {
			if (player.getHeldItem() != null && info.stack.equalStack(player.getHeldItem())) {
				if (!doubleClick) {
					LogisticsAPI.getItemHelper().insertItemFromPlayer(player, cache, player.inventory.currentItem);
				} else {
					LogisticsAPI.getItemHelper().insertInventoryFromPlayer(player, cache, player.inventory.currentItem);
				}
			}
		} else if (interact.type != BlockInteractionType.SHIFT_RIGHT) {
			if (info.stack != null) {
				StoredItemStack extract = LogisticsAPI.getItemHelper().extractItem(cache, info.stack.setStackSize(interact.type == BlockInteractionType.LEFT ? 1 : 64));
				if (extract != null) {
					SonarAPI.getItemHelper().spawnStoredItemStack(extract, screen.getWorldObj(), x, y, z, interact.getDir());
				}
			}

		}
	}

}
