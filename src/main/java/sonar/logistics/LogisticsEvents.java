package sonar.logistics;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.connections.CableRegistry;
import sonar.logistics.connections.CacheRegistry;
import sonar.logistics.connections.EmitterRegistry;
import sonar.logistics.connections.LogicMonitorCache;

public class LogisticsEvents {
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if (event.side == Side.CLIENT) {
			return;
		}		
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
			LogicMonitorCache.onServerTick();
		}
		if (event.phase == Phase.END) {
			EmitterRegistry.tick(); // this must happen at the end, since the dirty boolean will be changed and will upset tiles which need need
		}		
	}
	
	@SubscribeEvent
	public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		//if (!LogicMonitorCache.enableEvents()) {
		//	return;
		//}
		LogicMonitorCache.sendFullPacket(event.player);
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		//if (!LogicMonitorCache.enableEvents()) {
		//	return;
		//}
		LogicMonitorCache.sendFullPacket(event.player);
	}
}
