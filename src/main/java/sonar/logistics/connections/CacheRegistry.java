package sonar.logistics.connections;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.monitoring.ChannelMonitorHandler;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class CacheRegistry {

	private static LinkedHashMap<Integer, INetworkCache> cache = new LinkedHashMap<Integer, INetworkCache>();
	public static LinkedHashMap<Integer, MonitoredList<MonitoredBlockCoords>> coordMap = new LinkedHashMap();
	public static ChannelMonitorHandler handler = new ChannelMonitorHandler();
	
	
	public static void removeAll() {
		cache.clear();
	}

	public static Entry<BlockCoords, EnumFacing> getFirstConnection(int networkID) {
		INetworkCache network = getCache(networkID);
		return network != null ? network.getExternalBlock(true) : null;
	}

	//public static ArrayList<ILogicTile> getCacheList(CacheTypes type, int networkID) {
	//	INetworkCache network = getCache(networkID);
	//	return network != null ? network.getConnections(type, true) : new ArrayList();
	//}

	public static HashMap<BlockCoords, EnumFacing> getChannelArray(int networkID) {
		INetworkCache network = getCache(networkID);
		return network != null ? network.getExternalBlocks(true) : new HashMap();
	}

	public static INetworkCache getCache(int networkID) {
		INetworkCache networkCache = cache.get(networkID);
		return networkCache != null ? networkCache : EmptyNetworkCache.INSTANCE;
	}

	public static void refreshCache(int oldID, int newID) {
		if (oldID != newID)
			cache.remove(oldID);
		INetworkCache networkCache = cache.get(newID);
		if (networkCache != null && networkCache instanceof INetworkCache) {
			((NetworkCache) networkCache).refreshCache(newID, RefreshType.FULL);
		} else {
			NetworkCache network = new NetworkCache();
			network.refreshCache(newID, RefreshType.FULL);
			cache.put(newID, network);			
		}
	}

	public static LinkedHashMap<Integer, INetworkCache> getNetworkCache() {
		return cache;
	}
}
