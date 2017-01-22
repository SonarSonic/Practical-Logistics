package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.IRemovable;
import sonar.core.utils.IWorldPosition;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.common.multiparts.DataCablePart;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.CableHelper;

public class DefaultNetwork extends AbstractNetwork implements IRefreshCache {

	public int networkID = -1;
	private ArrayList<Class<?>> cacheTypes = Lists.newArrayList(IDataCable.class, ILogicTile.class, ILogicMonitor.class, IDataReceiver.class, IDataEmitter.class);
	private HashMap<Class<?>, ArrayList<IWorldPosition>> connections = getFreshMap();
	private HashMap<BlockCoords, EnumFacing> blockCache = new HashMap();
	private HashMap<BlockCoords, EnumFacing> networkedCache = new HashMap();
	private ArrayList<Integer> connectedNetworks = new ArrayList();
	private RefreshType lastRefresh = RefreshType.NONE;
	private RefreshType toRefresh = RefreshType.FULL;

	public DefaultNetwork(int networkID) {
		this.networkID = networkID;
	}

	public HashMap<Class<?>, ArrayList<IWorldPosition>> getFreshMap() {
		HashMap<Class<?>, ArrayList<IWorldPosition>> connections = new HashMap();
		cacheTypes.forEach(classType -> connections.put(classType, new ArrayList()));
		return connections;
	}

	public ArrayList<Class<?>> getValidClasses(IWorldPosition tile) {
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
		Iterator<IDataCable> connections = getConnections(IDataCable.class, includeChannels).iterator();
		while (connections.hasNext()) {
			IDataCable part = connections.next();
			if (part instanceof DataCablePart) {
				DataCablePart cable = (DataCablePart) part;
				HashMap<BlockCoords, EnumFacing> map = new HashMap();
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

	public <T extends IWorldPosition> ArrayList<T> getConnections(Class<T> classType, boolean includeChannels) {
		ArrayList<T> list = (ArrayList<T>) connections.getOrDefault(classType, (ArrayList<IWorldPosition>) new ArrayList<T>());
		if (includeChannels) {
			ArrayList<Integer> networks = getFinalNetworkList();
			networks.iterator().forEachRemaining(id -> {
				INetworkCache network = Logistics.getNetworkManager().getNetwork(id);
				ArrayList<T> connections = network.getConnections(classType, false);
				connections.iterator().forEachRemaining(connection -> {
					if (!list.contains(connection)) {
						list.add(connection);
					}
				});
			});
		}
		return list;
	}

	@Override
	public HashMap<BlockCoords, EnumFacing> getExternalBlocks(boolean includeChannels) {
		return includeChannels ? networkedCache : blockCache;
	}

	@Override
	public void refreshCache(int networkID, RefreshType refresh) {
		this.networkID = networkID;
		if (Logistics.getNetworkManager().updateEmitters) {
			for (IDataReceiver receiver : getConnections(IDataReceiver.class, true)) {
				receiver.refreshConnectedNetworks();
			}
		}
		if (refresh.shouldRefreshCables()) {
			refreshCables();
		}
		if (refresh.shouldRefreshConnections()) {
			refreshConnections();
		}
		if (refresh.shouldRefreshNetworks()) {
			refreshNetworks();
			refreshConnections();
		}
		if (refresh.shouldAlertWatchingNetworks()) {
			alertWatchingNetworks();
		}
		lastRefresh = toRefresh;
		toRefresh = RefreshType.NONE;
	}

	public void alertWatchingNetworks() {
		ArrayList<Integer> networks = this.getWatchingNetworkList();
		for (Integer id : networks) {
			Logistics.getNetworkManager().markNetworkDirty(id, RefreshType.ALERT);
		}
	}

	public void refreshCables() {
		localMonitors.clear();
		HashMap<Class<?>, ArrayList<IWorldPosition>> newConnections = getFreshMap();
		ArrayList<IDataCable> cables = Logistics.getCableManager().getConnections(networkID);
		Iterator<IDataCable> iterator = cables.iterator();
		while (iterator.hasNext()) {
			BlockCoords coord = iterator.next().getCoords();
			DataCablePart cablePart = (DataCablePart) LogisticsAPI.getCableHelper().getCableFromCoords(coord);
			if (cablePart == null) {
				continue;
			}
			cablePart.configureConnections(this);
			ArrayList<ILogicTile> tiles = CableHelper.getConnectedTiles(cablePart);
			tiles.forEach(tile -> getValidClasses(tile).forEach(classType -> newConnections.get(classType).add(tile)));
			if (!tiles.isEmpty())
				newConnections.get(IDataCable.class).add(cablePart);
		}
		this.connections = newConnections;
	}

	public void refreshNetworks() {
		ArrayList<Integer> networks = new ArrayList();
		getConnections(IDataReceiver.class, true).iterator().forEachRemaining(receiver -> {
			receiver.getConnectedNetworks().iterator().forEachRemaining(network -> {
				if (!networks.contains(network)) {
					networks.add(network);
				}
			});
		});
		this.connectedNetworks = networks;
	}

	public void refreshConnections() {
		HashMap<BlockCoords, EnumFacing> map = new HashMap<BlockCoords, EnumFacing>();
		ArrayList<IWorldPosition> toRemove = new ArrayList();
		for (IWorldPosition part : connections.get(IDataCable.class)) {
			if (part == null || !(part instanceof DataCablePart)) {
				toRemove.add(part);
				continue;
			}
			ArrayList<IConnectionNode> nodes = CableHelper.getConnectedTiles((DataCablePart) part, IConnectionNode.class);
			nodes.iterator().forEachRemaining(node -> {
				if (!(node instanceof IRemovable) || !((IRemovable) node).wasRemoved()) {
					node.addConnections(map);
				}
			});
		}
		toRemove.forEach(remove -> connections.get(IDataCable.class).remove(remove));
		this.blockCache = (HashMap<BlockCoords, EnumFacing>) map.clone();

		ArrayList<Integer> networks = getFinalNetworkList();
		for (Integer id : networks) {
			INetworkCache network = Logistics.getNetworkManager().getNetwork(id);
			HashMap<BlockCoords, EnumFacing> blocks = (HashMap<BlockCoords, EnumFacing>) network.getExternalBlocks(false).clone();
			for (Entry<BlockCoords, EnumFacing> set : blocks.entrySet()) {
				if (!map.containsKey(set.getKey())) {
					map.put(set.getKey(), set.getValue());
				}
			}
			for (ILogicMonitor monitor : network.getLocalMonitors()) {
				if (!localMonitors.contains(monitor)) {
					localMonitors.add(monitor);
				}
			}
		}
		this.networkedCache = (HashMap<BlockCoords, EnumFacing>) map.clone();

		for (Entry<LogicMonitorHandler, Map<ILogicMonitor, MonitoredList<?>>> handlers : monitorInfo.entrySet()) {
			compileCoordsList(handlers.getKey());
		}

		MonitoredList<MonitoredBlockCoords> list = MonitoredList.<MonitoredBlockCoords>newMonitoredList(getNetworkID());
		for (Entry<BlockCoords, EnumFacing> entry : networkedCache.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			list.add(new MonitoredBlockCoords(entry.getKey(), tile != null && tile.getDisplayName() != null ? tile.getDisplayName().getFormattedText() : entry.getKey().getBlock().getLocalizedName()));
		}
		Logistics.getNetworkManager().getCoordMap().put(networkID, list);

	}

	public <T extends IWorldPosition> T getFirstConnection(Class<T> type) {
		ArrayList<T> coords = getConnections(type, true);
		return coords.isEmpty() ? null : coords.get(0);
	}

	@Override
	public int getNetworkID() {
		return networkID;
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks) {
		for (Integer network : connectedNetworks) {
			if (!networks.contains(network)) {
				networks.add(network);
			}
		}
		return networks;
	}

	public ArrayList<Integer> getWatchingNetworkList() {
		ArrayList<Integer> networks = new ArrayList();
		getConnections(IDataEmitter.class, true).iterator().forEachRemaining(emitter -> {
			emitter.getNetworks().iterator().forEachRemaining(network -> {
				if (network != this.networkID && !networks.contains(network)) {
					networks.add(network);
				}
			});
		});
		return networks;
	}

	public ArrayList<Integer> getFinalNetworkList() {
		ArrayList<Integer> networks = getConnectedNetworks(new ArrayList());
		ArrayList<Integer> fullNetworks = (ArrayList<Integer>) networks.clone();
		networks.iterator().forEachRemaining(id -> Logistics.getNetworkManager().getNetwork(id).getConnectedNetworks(fullNetworks));
		return fullNetworks;
	}

	@Override
	public void updateNetwork(int networkID) {
		resendAllLists = networkID != this.getNetworkID();
		if (resendAllLists || Logistics.getNetworkManager().updateEmitters) {
			refreshCache(networkID, RefreshType.FULL);
		} else if (toRefresh != RefreshType.NONE) {
			refreshCache(networkID, toRefresh);
		} else {
			lastRefresh = RefreshType.NONE;
		}
		
		toRefresh = RefreshType.NONE;
		for (Entry<LogicMonitorHandler, Map<ILogicMonitor, MonitoredList<?>>> monitorMap : monitorInfo.entrySet()) {
			if (!monitorMap.getValue().isEmpty() && monitorMap.getKey() != null) {
				Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> newConnections = getMonitoredList(monitorMap.getKey());
				connectionInfo.replace(monitorMap.getKey(), newConnections);
				for (Entry<ILogicMonitor, MonitoredList<?>> monitors : monitorMap.getValue().entrySet()) {
					ILogicMonitor monitor = monitors.getKey();
					if (monitor != null && monitor.getHandler().id().equals(monitorMap.getKey().id())) {
						MonitoredList<IMonitorInfo> updateList = updateMonitoredList(monitors.getKey(), newConnections).updateList(monitors.getValue());
						monitor.sortMonitoredList(updateList, 0);// TODO
						monitor.setMonitoredInfo(updateList, 0);// TODO
						sendPacketsToViewers(monitor, updateList.copyInfo(), monitors.getValue().copyInfo());
						monitors.setValue(updateList);
					} else if (!monitor.getHandler().id().equals(monitorMap.getKey().id())) {
						Logistics.logger.info("WRONG MONITOR HANDLER FOR MONITOR: " + monitorMap.getKey().id());
					}
				}
			}
		}
		resendAllLists = false;
	}

	public <T extends IMonitorInfo> void compileCoordsList(LogicMonitorHandler<T> type) {
		HashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> compiledList = new HashMap();
		for (Entry<BlockCoords, EnumFacing> coords : networkedCache.entrySet()) {
			Pair pair = new Pair(coords.getKey(), coords.getValue());
			if (!compiledList.containsKey(pair))
				compiledList.put(pair, MonitoredList.<T>newMonitoredList(getNetworkID()));
		}
		connectionInfo.put(type, compiledList);
	}

	@Override
	public boolean isFakeNetwork() {
		return false;
	}

	@Override
	public void addLocalMonitor(ILogicMonitor monitor) {
		localMonitors.add(monitor);
	}

	@Override
	public ILogicMonitor getLocalMonitor() {
		if (!localMonitors.isEmpty()) {
			return localMonitors.get(0);
		}
		return null;
	}

	@Override
	public void markDirty(RefreshType type) {
		if (type.ordinal() < toRefresh.ordinal()) {
			toRefresh = type;
		}
	}

	@Override
	public ArrayList<ILogicMonitor> getLocalMonitors() {
		return localMonitors;
	}

	@Override
	public RefreshType getLastRefresh() {
		return lastRefresh;
	}
}
