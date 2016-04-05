package sonar.logistics.info.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.registries.CacheRegistry;

public class StoredFluidInteraction extends InfoInteractionHandler<FluidStackInfo> {

	@Override
	public String getName() {
		return "Fluid Stack";
	}

	@Override
	public void handleInteraction(FluidStackInfo info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		INetworkCache cache = CacheRegistry.getCache(info.cacheID);
		if (cache == null || cache.getNetworkID() == -1) {
			return;
		}
		if (interact.type == BlockInteractionType.RIGHT) {
			LogisticsAPI.getFluidHelper().drainHeldItem(player, cache);
		} else if (interact.type == BlockInteractionType.LEFT) {
			LogisticsAPI.getFluidHelper().fillHeldItem(player, cache, info.stack.setStackSize(1000));
		} else if (interact.type == BlockInteractionType.SHIFT_LEFT) {
			LogisticsAPI.getFluidHelper().fillHeldItem(player, cache, info.stack);
		}
	}

}
