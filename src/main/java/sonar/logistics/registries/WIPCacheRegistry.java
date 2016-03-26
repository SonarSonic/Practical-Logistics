package sonar.logistics.registries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.cache.NetworkCache;

public class WIPCacheRegistry {

	public static final EmptyNetworkCache EMPTY_CACHE = new EmptyNetworkCache();
	private static ArrayList<INetworkCache> caches = new ArrayList<INetworkCache>();

	public static void removeAll() {
		caches.clear();
	}

	public static Entry<BlockCoords, ForgeDirection> getFirstConnection(int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null) {
			return network.getExternalBlock(true);
		} else
			return null;
	}

	public static ArrayList<BlockCoords> getCacheList(CacheTypes type, int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null)
			return network.getConnections(type, true);
		else
			return new ArrayList();
	}

	public static LinkedHashMap<BlockCoords, ForgeDirection> getChannelArray(int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null)
			return network.getExternalBlocks(true);
		else
			return new LinkedHashMap();
	}

	public static INetworkCache getCache(int networkID) {
		INetworkCache networkCache = null;
		if (caches.size() > networkID)
			networkCache = caches.get(networkID);
		if (networkCache == null) {
			return EMPTY_CACHE;
		}
		return networkCache;
	}

	public static void refreshCache(int oldID, int newID) {
		if (oldID != newID) {
			caches.remove(oldID);
		}
		INetworkCache networkCache = caches.get(newID);
		if (networkCache != null && networkCache instanceof INetworkCache) {
			((NetworkCache) networkCache).refreshCache(newID);
		}
		NetworkCache network = new NetworkCache();
		network.refreshCache(newID);
		caches.set(newID, network);
		Logistics.logger.debug("refreshed cache");
	}

	public static ArrayList<INetworkCache> getNetworkCache() {
		return caches;
	}
}
