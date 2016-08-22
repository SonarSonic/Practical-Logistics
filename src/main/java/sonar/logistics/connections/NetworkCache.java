package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import mcmultipart.multipart.IMultipart;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.IRemovable;
import sonar.core.utils.Pair;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.parts.DataCablePart;

public class NetworkCache extends MonitorCache implements IRefreshCache {

	public int networkID = -1;
	private ArrayList<Class<?>> cacheTypes = Lists.newArrayList(IDataCable.class, ILogicTile.class, ILogicMonitor.class);
	private LinkedHashMap<Class<?>, ArrayList<ILogicTile>> connections = getFreshMap();
	private LinkedHashMap<BlockCoords, EnumFacing> blockCache = new LinkedHashMap();
	private LinkedHashMap<BlockCoords, EnumFacing> networkedCache = new LinkedHashMap();

	public LinkedHashMap<Class<?>, ArrayList<ILogicTile>> getFreshMap() {
		LinkedHashMap<Class<?>, ArrayList<ILogicTile>> connections = new LinkedHashMap();
		cacheTypes.forEach(classType -> connections.put(classType, new ArrayList()));
		return connections;
	}

	public ArrayList<Class<?>> getValidClasses(ILogicTile tile) {
		ArrayList<Class<?>> valid = new ArrayList();
		for (Class<?> classType : cacheTypes) {
			if (classType.isInstance(tile)) {
				valid.add(classType);
			}
		}
		return valid;
	}

	@Override
	@Deprecated
	public Entry<BlockCoords, EnumFacing> getExternalBlock(boolean includeChannels) {
		// FIXME - cache this
		ArrayList<ILogicTile> toRemove = new ArrayList();
		for (IDataCable part : getConnections(IDataCable.class, includeChannels)) {
			if (part == null || !(part instanceof IDataCable) || !((IDataCable) part).hasConnections()) {
				toRemove.add(part);
				continue;
			} else {
				DataCablePart cable = (DataCablePart) part;
				LinkedHashMap<BlockCoords, EnumFacing> map = new LinkedHashMap();
				cable.getContainer().getParts().forEach(multipart -> {
					if (multipart instanceof IConnectionNode) {
						((IConnectionNode) multipart).addConnections(map);
					}
				});
				for (Entry<BlockCoords, EnumFacing> set : map.entrySet()) {
					break;
				}
			}
		}
		return null;
	}

	public <T extends ILogicTile> ArrayList<T> getConnections(Class<T> classType, boolean includeChannels) {
		ArrayList<T> list = (ArrayList<T>) connections.getOrDefault(classType, (ArrayList<ILogicTile>) new ArrayList<T>());
		if (includeChannels) {
			ArrayList<Integer> networks = getFinalNetworkList();
			for (Integer id : networks) {
				INetworkCache network = CacheRegistry.getCache(id);
				ArrayList<T> connections = ((ArrayList<T>) network.getConnections(classType, false));
				for (T connection : connections) {
					if (!list.contains(connection)) {
						list.add(connection);
					}
				}
			}
		}
		return list;
	}

	@Override
	public LinkedHashMap<BlockCoords, EnumFacing> getExternalBlocks(boolean includeChannels) {
		return includeChannels ? networkedCache : blockCache;
	}

	@Override
	public void refreshCache(int networkID, RefreshType refresh) {
		this.networkID = networkID;
		if (refresh.shouldRefreshConnections()) {
			LinkedHashMap<Class<?>, ArrayList<ILogicTile>> newConnections = getFreshMap();
			for (BlockCoords coord : CableRegistry.getCables(networkID)) {
				IDataCable tile = LogisticsAPI.getCableHelper().getCableFromCoords(coord);
				if (tile != null) {
					tile.configureConnections(this);
					DataCablePart cablePart = (DataCablePart) tile;
					for (IMultipart p : cablePart.getContainer().getParts()) {
						if (!(p instanceof ILogicTile)) {
							continue;
						}
						ILogicTile part = (ILogicTile) p;
						if (part == cablePart) {
							if (cablePart.hasConnections()) {
								newConnections.get(IDataCable.class).add(cablePart);
							}
						} else {
							getValidClasses(part).forEach(classType -> newConnections.get(classType).add(part));
						}
					}
				}
			}
			this.connections = newConnections;
		}
		LinkedHashMap<BlockCoords, EnumFacing> map = new LinkedHashMap<BlockCoords, EnumFacing>();
		ArrayList<ILogicTile> toRemove = new ArrayList();
		for (ILogicTile part : connections.getOrDefault(IDataCable.class, new ArrayList<>())) {
			if (part == null || !(part instanceof IDataCable)) {
				toRemove.add(part);
				continue;
			} else {
				DataCablePart cablePart = (DataCablePart) part;
				cablePart.getContainer().getParts().forEach(multipart -> {
					if (!(multipart instanceof IDataCable) && multipart instanceof IConnectionNode) {
						if (!(multipart instanceof IRemovable) || !((IRemovable) multipart).wasRemoved()) {
							((IConnectionNode) multipart).addConnections(map);
						}
					}
				});
			}
		}
		toRemove.forEach(remove -> connections.getOrDefault(IDataCable.class, new ArrayList<ILogicTile>()).remove(remove));
		this.blockCache = (LinkedHashMap<BlockCoords, EnumFacing>) map.clone();
		ArrayList<Integer> networks = getFinalNetworkList();
		for (Integer id : networks) {
			INetworkCache network = CacheRegistry.getCache(id);
			LinkedHashMap<BlockCoords, EnumFacing> blocks = ((LinkedHashMap<BlockCoords, EnumFacing>) network.getExternalBlocks(false).clone());
			for (Entry<BlockCoords, EnumFacing> set : blocks.entrySet()) {
				if (!map.containsKey(set.getKey())) {
					map.put(set.getKey(), set.getValue());
				}
			}
		}
		this.networkedCache = (LinkedHashMap<BlockCoords, EnumFacing>) map.clone();

		for (Entry<MonitorHandler, LinkedHashMap<ILogicMonitor, MonitoredList<?>>> handlers : monitoredCollections.entrySet()) {
			this.compileCoordsList(handlers.getKey());
		}

		MonitoredList<MonitoredBlockCoords> list = MonitoredList.<MonitoredBlockCoords>newMonitoredList();
		for (Entry<BlockCoords, EnumFacing> entry : networkedCache.entrySet()) {
			list.add(new MonitoredBlockCoords(entry.getKey(), entry.getKey().getBlock().getUnlocalizedName()));
		}
		CacheRegistry.coordMap.put(networkID, list);

	}

	public <T extends ILogicTile> T getFirstConnection(Class<T> type) {
		ArrayList<T> coords = getConnections(type, true);
		return coords.isEmpty() ? null : coords.get(0);
	}

	@Override
	public int getNetworkID() {
		return networkID;
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks) {
		/* for (BlockCoords coord : connections.getOrDefault(CacheTypes.CHANNELLED, new ArrayList<BlockCoords>())) { Object tile = OLDMultipartHelper.checkObject(coord.getTileEntity()); if (tile != null && tile instanceof IChannelProvider) { IChannelProvider provider = (IChannelProvider) tile; INetworkCache network = provider.getNetwork(); int id = network.getNetworkID(); if (id != -1 && !networks.contains(id)) { networks.add(id); } } } */
		return networks;
	}

	public ArrayList<Integer> getFinalNetworkList() {
		ArrayList<Integer> networks = getConnectedNetworks(new ArrayList());
		for (Integer id : (ArrayList<Integer>) networks.clone()) {
			if (!networks.contains(id)) {
				networks.add(id);
			}
			CacheRegistry.getCache(networkID).getConnectedNetworks(networks);
		}
		return networks;
	}

	@Override
	public void updateNetwork(int networkID) {
		if (networkID != this.getNetworkID()) {
			this.refreshCache(networkID, RefreshType.FULL);
		}
		for (Entry<MonitorHandler, LinkedHashMap<ILogicMonitor, MonitoredList<?>>> handlers : monitoredCollections.entrySet()) {
			if (!handlers.getValue().isEmpty()) {
				getMonitoredList(handlers.getKey());
				LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> infoList = monitoredList.get(handlers.getKey());
				for (Entry<ILogicMonitor, MonitoredList<?>> monitors : handlers.getValue().entrySet()) {
					ILogicMonitor monitor = monitors.getKey();
					if (monitor != null) {
						MonitoredList<IMonitorInfo> updateList = updateMonitoredList(monitors.getKey(), infoList).updateList(monitors.getValue());
						monitor.sortMonitoredList(updateList);
						monitor.setMonitoredInfo(updateList);
						List<MonitorViewer> viewers = monitor.getViewers();
						//if (!viewers.isEmpty()) {
							sendPacketsToViewer(monitor, viewers, updateList, monitors.getValue());
						//}
					}
				}
			}
		}
	}

	public <T extends IMonitorInfo> void compileCoordsList(MonitorHandler<T> type) {
		LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> compiledList = new LinkedHashMap();
		for (Entry<BlockCoords, EnumFacing> coords : networkedCache.entrySet()) {
			Pair pair = new Pair(coords.getKey(), coords.getValue());
			if (!compiledList.containsKey(pair))
				compiledList.put(pair, MonitoredList.<T>newMonitoredList());
		}
		monitoredList.put(type, compiledList);
	}

	@Override
	public boolean isFakeNetwork() {
		return false;
	}
}
