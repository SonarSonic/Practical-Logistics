package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
import sonar.logistics.connections.DefaultNetwork;
import sonar.logistics.connections.monitoring.ChannelMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;

public class NetworkManager {

	public static boolean updateEmitters;

	public Map<Integer, INetworkCache> cache = new ConcurrentHashMap<Integer, INetworkCache>();
	public Map<Integer, MonitoredList<MonitoredBlockCoords>> coordMap = new ConcurrentHashMap<Integer, MonitoredList<MonitoredBlockCoords>>();
	public ChannelMonitorHandler handler = new ChannelMonitorHandler();

	public static void removeAll() {
		getNetworkCache().clear();
	}

	public static Entry<BlockCoords, EnumFacing> getFirstConnection(int networkID) {
		INetworkCache network = getNetwork(networkID);
		return network != null ? network.getExternalBlock(true) : null;
	}

	public static HashMap<BlockCoords, EnumFacing> getChannelArray(int networkID) {
		INetworkCache network = getNetwork(networkID);
		return network != null ? network.getExternalBlocks(true) : new HashMap();
	}

	public static void tick() {
		if (getNetworkCache().isEmpty()) {
			return;
		}
		NetworkManager manager = Logistics.instance.REGISTRY;
		Set<Entry<Integer, INetworkCache>> entrySet = getNetworkCache().entrySet();
		for (final Iterator<Entry<Integer, INetworkCache>> iterator = entrySet.iterator(); iterator.hasNext();) {
			Entry<Integer, INetworkCache> entry = iterator.next();
			INetworkCache entryValueCache = entry.getValue();
			if (entryValueCache instanceof IRefreshCache)
				((IRefreshCache) entryValueCache).updateNetwork(entryValueCache.getNetworkID());

			if (CableManager.getCables(entryValueCache.getNetworkID()).size() == 0){
				iterator.remove();
			}
		}
	}

	public static INetworkCache getNetwork(int networkID) {
		INetworkCache networkCache = getNetworkCache().get(networkID);
		return networkCache != null ? networkCache : EmptyNetworkCache.INSTANCE;
	}
	
	public static INetworkCache getOrCreateNetwork(int networkID) {
		INetworkCache networkCache = getNetworkCache().get(networkID);
		if(networkCache==null || networkCache.isFakeNetwork()){
			getNetworkCache().put(networkID, new DefaultNetwork(networkID));
			networkCache = getNetworkCache().get(networkID);
			networkCache.markDirty(RefreshType.FULL);
		}
		return networkCache;
	}

	public static void refreshNetworks(int oldID, ArrayList<Integer> newNetworks) {
		boolean removeOld = true;
		for (int id : newNetworks) {
			INetworkCache network = getNetwork(id);
			if (network == null || network.isFakeNetwork()) {
				network = new DefaultNetwork(id);
				getNetworkCache().put(id, network);
			}
			network.markDirty(RefreshType.FULL);
			if (id == oldID) {
				removeOld = false;
			}
		}
		if(removeOld){
			getNetworkCache().remove(oldID);
		}
	}

	public static void markNetworkDirty(int networkID, RefreshType type){
		INetworkCache cache = getOrCreateNetwork(networkID);
		cache.markDirty(type);
	}
	
	public static void connectNetworks(int oldID, int newID) {
		if (oldID != newID)
			getNetworkCache().remove(oldID);
		INetworkCache networkCache = getNetworkCache().get(newID);
		if (networkCache != null && networkCache instanceof INetworkCache) {
			((DefaultNetwork) networkCache).refreshCache(newID, RefreshType.FULL);
		} else {
			DefaultNetwork network = new DefaultNetwork(newID);
			network.refreshCache(newID, RefreshType.FULL);
			getNetworkCache().put(newID, network);
		}
	}

	public static Map<Integer, INetworkCache> getNetworkCache() {
		return Logistics.instance.REGISTRY.cache;
	}

	public static Map<Integer, MonitoredList<MonitoredBlockCoords>> getCoordMap() {
		return Logistics.instance.REGISTRY.coordMap;
	}
}
