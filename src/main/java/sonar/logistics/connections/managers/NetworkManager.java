package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.connections.DefaultNetwork;
import sonar.logistics.connections.monitoring.ChannelMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;

public class NetworkManager {

	public boolean updateEmitters;

	public Map<Integer, INetworkCache> cache = new ConcurrentHashMap<Integer, INetworkCache>();
	public Map<Integer, MonitoredList<MonitoredBlockCoords>> coordMap = new ConcurrentHashMap<Integer, MonitoredList<MonitoredBlockCoords>>();
	public ChannelMonitorHandler handler = new ChannelMonitorHandler();
	
	
	public void removeAll() {
		cache.clear();
	}

	public Entry<BlockCoords, EnumFacing> getFirstConnection(int networkID) {
		INetworkCache network = getNetwork(networkID);
		return network != null ? network.getExternalBlock(true) : null;
	}

	public HashMap<BlockCoords, EnumFacing> getChannelArray(int networkID) {
		INetworkCache network = getNetwork(networkID);
		return network != null ? network.getExternalBlocks(true) : new HashMap();
	}

	public void tick() {
		if (cache.isEmpty()) {
			return;
		}
		Set<Entry<Integer, INetworkCache>> entrySet = cache.entrySet();
		for (final Iterator<Entry<Integer, INetworkCache>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<Integer, INetworkCache> entry = iterator.next();
			INetworkCache entryValueCache = entry.getValue();
			if (entryValueCache instanceof IRefreshCache)
				((IRefreshCache) entryValueCache).updateNetwork(entryValueCache.getNetworkID());

			if (Logistics.instance.cableManager.getConnections(entryValueCache.getNetworkID()).size() == 0) {
				iterator.remove();
			}
		}
	}

	public INetworkCache getNetwork(int networkID) {
		INetworkCache networkCache = cache.get(networkID);
		return networkCache != null ? networkCache : EmptyNetworkCache.INSTANCE;
	}

	public INetworkCache getOrCreateNetwork(int networkID) {
		INetworkCache networkCache = cache.get(networkID);
		if (networkCache == null || networkCache.isFakeNetwork()) {
			cache.put(networkID, new DefaultNetwork(networkID));
			networkCache = cache.get(networkID);
			networkCache.markDirty(RefreshType.FULL);
		}
		return networkCache;
	}

	public void refreshNetworks(int oldID, ArrayList<Integer> newNetworks) {
		boolean removeOld = true;
		for (int id : newNetworks) {
			INetworkCache network = getNetwork(id);
			if (network == null || network.isFakeNetwork()) {
				network = new DefaultNetwork(id);
				cache.put(id, network);
			}
			network.markDirty(RefreshType.FULL);
			if (id == oldID) {
				removeOld = false;
			}
		}
		if (removeOld) {
			cache.remove(oldID);
		}
	}

	public void markNetworkDirty(int networkID, RefreshType type) {
		INetworkCache cache = getOrCreateNetwork(networkID);
		cache.markDirty(type);
	}

	public void connectNetworks(int oldID, int newID) {
		if (oldID != newID)
			cache.remove(oldID);
		INetworkCache networkCache = cache.get(newID);
		if (networkCache != null && networkCache instanceof INetworkCache) {
			((DefaultNetwork) networkCache).refreshCache(newID, RefreshType.FULL);
		} else {
			DefaultNetwork network = new DefaultNetwork(newID);
			network.refreshCache(newID, RefreshType.FULL);
			cache.put(newID, network);
		}
	}
	public Map<Integer, INetworkCache> getNetworkCache() {
		return cache;
	}

	public Map<Integer, MonitoredList<MonitoredBlockCoords>> getCoordMap() {
		return coordMap;
	}
}
