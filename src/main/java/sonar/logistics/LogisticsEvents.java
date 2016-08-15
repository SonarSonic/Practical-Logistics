package sonar.logistics;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.connections.CableRegistry;
import sonar.logistics.connections.CacheRegistry;

public class LogisticsEvents {
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == Phase.START) {
			LinkedHashMap<Integer, INetworkCache> networks = (LinkedHashMap<Integer, INetworkCache>) CacheRegistry.getNetworkCache().clone();
			if (networks.isEmpty()) {
				return;
			}
			for (Entry<Integer, INetworkCache> set : networks.entrySet()) {
				INetworkCache cache = set.getValue();
				if (cache instanceof IRefreshCache) {
					((IRefreshCache) cache).updateNetwork(cache.getNetworkID());
				}
				if (CableRegistry.getCables(cache.getNetworkID()).size() == 0) {
					CacheRegistry.getNetworkCache().remove(Integer.valueOf(cache.getNetworkID()));
				}
			}
		}
	}
	/*
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onEvent(PopulateChunkEvent.Pre event) {		
		Chunk chunk = event.world.getChunkFromChunkCoords(event.chunkX, event.chunkZ);
		SapphireOreGen.generateOre(BlockRegistry.sapphire_ore, event.world, event.rand, event.chunkX, event.chunkZ, 2, 10, 25, 1, 100, Blocks.stone);
		chunk.isModified = true;		
	}
	*/
}
