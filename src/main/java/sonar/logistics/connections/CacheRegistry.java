package sonar.logistics.connections;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.monitoring.ChannelMonitorHandler;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class CacheRegistry {

	public LinkedHashMap<Integer, INetworkCache> cache = new LinkedHashMap<Integer, INetworkCache>();
	public LinkedHashMap<Integer, MonitoredList<MonitoredBlockCoords>> coordMap = new LinkedHashMap();
	public ChannelMonitorHandler handler = new ChannelMonitorHandler();

	public static void removeAll() {
		getNetworkCache().clear();
	}

	public static Entry<BlockCoords, EnumFacing> getFirstConnection(int networkID) {
		INetworkCache network = getCache(networkID);
		return network != null ? network.getExternalBlock(true) : null;
	}

	public static HashMap<BlockCoords, EnumFacing> getChannelArray(int networkID) {
		INetworkCache network = getCache(networkID);
		return network != null ? network.getExternalBlocks(true) : new HashMap();
	}

	public static void tick() {
		if (getNetworkCache().isEmpty()) {
			return;
		}
		Set<Entry<Integer, INetworkCache>> entrySet = getNetworkCache().entrySet();

		Iterator<Entry<Integer, INetworkCache>> iterator = entrySet.iterator();
		iterator.forEachRemaining(entry -> {
			INetworkCache entryValueCache = entry.getValue();			
			if (entryValueCache instanceof IRefreshCache)
				((IRefreshCache) entryValueCache).updateNetwork(entryValueCache.getNetworkID());	
			
			if (CableRegistry.getCables(entryValueCache.getNetworkID()).size() == 0)
				iterator.remove();
		});

	}

	public static INetworkCache getCache(int networkID) {
		INetworkCache networkCache = getNetworkCache().get(networkID);
		return networkCache != null ? networkCache : EmptyNetworkCache.INSTANCE;
	}

	public static void refreshCache(int oldID, int newID) {
		if (oldID != newID)
			getNetworkCache().remove(oldID);
		INetworkCache networkCache = getNetworkCache().get(newID);
		if (networkCache != null && networkCache instanceof INetworkCache) {
			((NetworkCache) networkCache).refreshCache(newID, RefreshType.FULL);
		} else {
			NetworkCache network = new NetworkCache();
			network.refreshCache(newID, RefreshType.FULL);
			getNetworkCache().put(newID, network);
		}

	}

	public static LinkedHashMap<Integer, INetworkCache> getNetworkCache() {
		return Logistics.instance.REGISTRY.cache;

	}

	public static LinkedHashMap<Integer, MonitoredList<MonitoredBlockCoords>> getCoordMap() {
		return Logistics.instance.REGISTRY.coordMap;
	}
}
