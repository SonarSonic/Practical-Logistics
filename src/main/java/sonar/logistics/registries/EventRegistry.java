package sonar.logistics.registries;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import sonar.logistics.Logistics;
import sonar.logistics.api.cache.CacheTypes;
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
			if (networks.isEmpty()) {
				return;
			}
			for (Entry<Integer, INetworkCache> set : networks.entrySet()) {
				INetworkCache cache = set.getValue();
				if (cache instanceof IRefreshCache) {
					((IRefreshCache) cache).updateNetwork(cache.getNetworkID());
				}
				if(CableRegistry.getCables(cache.getNetworkID()).size()==0){
					CacheRegistry.getNetworkCache().remove(Integer.valueOf(cache.getNetworkID()));
				}
			}
		}
	}
}
