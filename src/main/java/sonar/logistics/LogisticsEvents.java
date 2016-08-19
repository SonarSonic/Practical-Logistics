package sonar.logistics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.connections.CableRegistry;
import sonar.logistics.connections.CacheRegistry;
import sonar.logistics.connections.LogicMonitorCache;

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

	@SubscribeEvent
	public void onWatchChunk(ChunkWatchEvent.Watch event) {
		if (!LogicMonitorCache.enableEvents()) {
			return;
		}
		World world = event.getPlayer().getEntityWorld();
		int dimension = world.provider.getDimension();
		ArrayList<ChunkPos> monitored = LogicMonitorCache.monitoredChunks.putIfAbsent(dimension, new ArrayList());
		if (!monitored.isEmpty() && monitored.contains(event.getChunk())) {
			ArrayList<ChunkPos> chunks = LogicMonitorCache.activeChunks.putIfAbsent(event.getPlayer(), new ArrayList());
			if (!chunks.contains(event.getChunk())) {
				chunks.add(event.getChunk());
			}
		}
	}

	@SubscribeEvent
	public void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
		if (!LogicMonitorCache.enableEvents()) {
			return;
		}
		LogicMonitorCache.activeChunks.putIfAbsent(event.player, new ArrayList()).clear();
		LogicMonitorCache.sendFirstPacket(event.player);
	}

	@SubscribeEvent
	public void onUnwatchChunk(ChunkWatchEvent.UnWatch event) {
		if (!LogicMonitorCache.enableEvents()) {
			return;
		}
		LogicMonitorCache.activeChunks.putIfAbsent(event.getPlayer(), new ArrayList()).remove(event.getChunk());
	}

	@SubscribeEvent
	public void onChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
		if (!LogicMonitorCache.enableEvents()) {
			return;
		}
		LogicMonitorCache.activeChunks.putIfAbsent(event.player, new ArrayList()).clear();
		LogicMonitorCache.sendFirstPacket(event.player);
	}

	@SubscribeEvent
	public void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
		if (!LogicMonitorCache.enableEvents()) {
			return;
		}
		LogicMonitorCache.sendFirstPacket(event.player);
	}

	@SubscribeEvent
	public void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
		if (!LogicMonitorCache.enableEvents()) {
			return;
		}
		LogicMonitorCache.activeChunks.putIfAbsent(event.player, new ArrayList()).clear();
	}
}
