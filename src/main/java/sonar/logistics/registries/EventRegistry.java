package sonar.logistics.registries;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import sonar.logistics.Logistics;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class EventRegistry {
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.phase == Phase.START) {
			LinkedHashMap<Integer, INetworkCache> networks = (LinkedHashMap<Integer, INetworkCache>) CacheRegistry.getNetworkCache().clone();
			for (Entry<Integer, INetworkCache> set : networks.entrySet()) {
				INetworkCache cache = set.getValue();
				if (cache instanceof IRefreshCache) {
					try {
						((IRefreshCache) cache).updateNetwork(cache.getNetworkID());
					} catch (Exception exception) {
						Logistics.logger.error("FAILED TO REFRESH NETWORK CACHE - TELL MOD AUTHOR", exception);
					}
				}
			}
		}
	}
}
