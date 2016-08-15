package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.IRemovable;
import sonar.core.utils.Pair;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class NetworkCache extends MonitorCache implements IRefreshCache {

	public int networkID = -1;
	private LinkedHashMap<CacheTypes, ArrayList<BlockCoords>> connections = new LinkedHashMap();
	private LinkedHashMap<BlockCoords, EnumFacing> blockCache = new LinkedHashMap();
	private LinkedHashMap<BlockCoords, EnumFacing> networkedCache = new LinkedHashMap();
	
	
	@Override
	@Deprecated
	public Entry<BlockCoords, EnumFacing> getExternalBlock(boolean includeChannels) {
		// FIXME - cache this
		ArrayList<BlockCoords> toRemove = new ArrayList();
		for (BlockCoords coords : connections.getOrDefault(CacheTypes.CONNECTED_CABLE, new ArrayList<BlockCoords>())) {
			IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
			ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
			if (part == null || !(part instanceof IDataCable) || !((IDataCable) part).hasConnections()) {
				toRemove.add(coords);
				continue;
			} else {
				LinkedHashMap<BlockCoords, EnumFacing> map = new LinkedHashMap();
				container.getParts().forEach(multipart -> {
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

	@Override
	public ArrayList<BlockCoords> getConnections(CacheTypes type, boolean includeChannels) {
		ArrayList<BlockCoords> list = connections.getOrDefault(type, new ArrayList<BlockCoords>());
		if (includeChannels) {
			ArrayList<Integer> networks = getFinalNetworkList();
			for (Integer id : networks) {
				INetworkCache network = CacheRegistry.getCache(id);
				ArrayList<BlockCoords> blocks = ((ArrayList<BlockCoords>) network.getConnections(type, false));
				for (BlockCoords coord : blocks) {
					if (!coord.contains(list)) {
						list.add(coord);
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
	public void refreshCache(int networkID, boolean fullRefresh) {
		this.networkID = networkID;
		if (fullRefresh) {
			LinkedHashMap<CacheTypes, ArrayList<BlockCoords>> connections = new LinkedHashMap();
			CableRegistry.getCables(networkID).forEach(coord -> {
				Object tile = LogisticsAPI.getCableHelper().getCableFromCoords(coord);
				if (tile == null) {
					tile = coord.getTileEntity();
				}
				if (tile != null) {
					if (tile instanceof IDataCable) {
						IDataCable cable = (IDataCable) tile;
						cable.configureConnections(this);
					}
					CacheTypes.getTypesForTile(tile).forEach(type -> {
						connections.putIfAbsent(type, new ArrayList());
						connections.get(type).add(coord);
					});
				}
			});
			this.connections = connections;
		}
		LinkedHashMap<BlockCoords, EnumFacing> map = new LinkedHashMap<BlockCoords, EnumFacing>();
		ArrayList<BlockCoords> toRemove = new ArrayList();
		for (BlockCoords coords : connections.getOrDefault(CacheTypes.CONNECTED_CABLE, new ArrayList<BlockCoords>())) {
			IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
			ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
			if (part == null || !(part instanceof IDataCable)) {
				toRemove.add(coords);
				continue;
			} else {
				container.getParts().forEach(multipart -> {
					if (!(multipart instanceof IDataCable) && multipart instanceof IConnectionNode) {
						if (!(multipart instanceof IRemovable) || !((IRemovable) multipart).wasRemoved()) {
							((IConnectionNode) multipart).addConnections(map);
						}
					}
				});
			}
		}
		toRemove.forEach(remove -> connections.getOrDefault(CacheTypes.CONNECTED_CABLE, new ArrayList<BlockCoords>()).remove(remove));
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
			list.info.add(new MonitoredBlockCoords(entry.getKey(), entry.getKey().getBlock().getUnlocalizedName()));
		}		
		CacheRegistry.coordMap.put(networkID, list);
		
	}

	@Override
	public BlockCoords getFirstConnection(CacheTypes type) {
		ArrayList<BlockCoords> coords = this.getConnections(type, true);
		return coords.isEmpty() ? null : coords.get(0);
	}

	@Override
	public Block getFirstBlock(CacheTypes type) {
		BlockCoords connection = this.getFirstConnection(type);
		return connection == null ? null : connection.getBlock();
	}

	@Override
	public TileEntity getFirstTileEntity(CacheTypes type) {
		BlockCoords connection = this.getFirstConnection(type);
		return connection == null ? null : connection.getTileEntity();
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
			this.refreshCache(networkID, true);
		}
		for (Entry<MonitorHandler, LinkedHashMap<ILogicMonitor, MonitoredList<?>>> handlers : monitoredCollections.entrySet()) {
			getMonitoredList(handlers.getKey());
			for (Entry<ILogicMonitor, MonitoredList<?>> monitors : handlers.getValue().entrySet()) {
				getAndSendFullMonitoredList(monitors.getKey(), monitors.getValue());
			}
		}
	}

	public <T extends IMonitorInfo> void compileCoordsList(MonitorHandler<T> type){
		//LinkedHashMap<ILogicMonitor, MonitoredList<?>> logicMonitors = monitoredCollections.get(type);
		//check if any of them what to monitor everything... otherwise for now we assume they all do cause reasons.
		LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> compiledList = new LinkedHashMap();
		for(Entry<BlockCoords, EnumFacing> coords :networkedCache.entrySet()){
			compiledList.put(new Pair(coords.getKey(), coords.getValue()), MonitoredList.<T>newMonitoredList());
		}
		monitoredList.put(type, compiledList);
	}

	@Override
	public boolean isFakeNetwork() {
		return false;
	}
}
