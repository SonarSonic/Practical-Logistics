package sonar.logistics.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.utils.ExternalCoords;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;
import sonar.logistics.registries.CableRegistry;
import sonar.logistics.registries.CacheRegistry;

public class NetworkCache extends StorageCache implements IRefreshCache {

	public int networkID = -1;

	/** all {@link IEntityNode} */
	private final ArrayList<BlockCoords> entityCache = new ArrayList<BlockCoords>();
	/** all {@link IConnectionNode} */
	private final ArrayList<BlockCoords> nodeCache = new ArrayList<BlockCoords>();
	/** all {@link IInfoEmitter} */
	private final ArrayList<BlockCoords> emitterCache = new ArrayList<BlockCoords>();
	/** all {@link ILogicTile} */
	private final ArrayList<BlockCoords> networkCache = new ArrayList<BlockCoords>();
	/** all {@link IChannelProvider} */
	private final ArrayList<BlockCoords> channelCache = new ArrayList<BlockCoords>();

	private final LinkedHashMap<BlockCoords, ForgeDirection> blockCache = new LinkedHashMap();
	private final LinkedHashMap<BlockCoords, ForgeDirection> networkedCache = new LinkedHashMap();
	private StorageItems cachedItems = StorageItems.EMPTY;
	private StorageFluids cachedFluids = StorageFluids.EMPTY;

	@Override
	public Entry<BlockCoords, ForgeDirection> getExternalBlock(boolean includeChannels) {
		for (BlockCoords coords : nodeCache) {
			try {
				Object tile = FMPHelper.checkObject(coords.getTileEntity());
				if (tile instanceof IConnectionNode) {
					LinkedHashMap<BlockCoords, ForgeDirection> map = new LinkedHashMap();
					((IConnectionNode) tile).addConnections(map);
					for (Entry<BlockCoords, ForgeDirection> set : map.entrySet()) {
						return set;
					}
				}
			} catch (Exception exception) {
			}
		}

		return null;
	}

	@Override
	public ArrayList<BlockCoords> getConnections(CacheTypes type) {
		switch (type) {
		case CHANNELLED:
			return channelCache;
		case EMITTER:
			return emitterCache;
		case ENTITY_NODES:
			return entityCache;
		case NODES:
			return nodeCache;
		case NETWORK:
			return networkCache;
		case CABLE:
		default:
			break;
		}
		return new ArrayList();
	}

	@Override
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks(boolean includeChannels) {
		if (includeChannels) {
			return networkedCache;
		}
		/*
		 * LinkedHashMap map = new LinkedHashMap(); for (BlockCoords coords : nodeCache) { try { Object tile = FMPHelper.checkObject(coords.getTileEntity()); if (tile instanceof IConnectionNode) { LinkedHashMap<BlockCoords, ForgeDirection> connections = new LinkedHashMap(); ((IConnectionNode) tile).addConnections(connections); for (Entry<BlockCoords, ForgeDirection> set : connections.entrySet()) { if (!set.getKey().contains(map)) { map.put(set.getKey(), set.getValue()); } } } } catch (Exception exception) { } } if (includeChannels) { ArrayList<Integer> networks = getFinalNetworkList(); for (Integer id : networks) { INetworkCache network = CacheRegistry.getCache(id); LinkedHashMap<BlockCoords, ForgeDirection> blocks = ((LinkedHashMap<BlockCoords, ForgeDirection>)
		 * network.getExternalBlocks(false).clone()); for (Entry<BlockCoords, ForgeDirection> set : blocks.entrySet()) { if (!set.getKey().contains(map)) { map.put(set.getKey(), set.getValue()); } }
		 * 
		 * } }
		 */
		return blockCache;
	}

	@Override
	public void refreshCache(int networkID) {
		this.networkID = networkID;
		ArrayList<BlockCoords> coords = (ArrayList<BlockCoords>) CableRegistry.getConnections(networkID).clone();
		Iterator<BlockCoords> iterator = coords.iterator();

		LinkedHashMap<BlockCoords, ForgeDirection> blockCache = new LinkedHashMap<BlockCoords, ForgeDirection>();
		ArrayList<BlockCoords> entityCache = new ArrayList<BlockCoords>();
		ArrayList<BlockCoords> nodeCache = new ArrayList<BlockCoords>();
		ArrayList<BlockCoords> emitterCache = new ArrayList<BlockCoords>();
		ArrayList<BlockCoords> networkCache = new ArrayList<BlockCoords>();
		ArrayList<BlockCoords> channelCache = new ArrayList<BlockCoords>();

		while (iterator.hasNext()) {
			BlockCoords coord = iterator.next();
			World world = coord.getWorld();
			Block target = coord.getBlock(coord.getWorld());
			if (target != null && !target.isAir(world, coord.getX(), coord.getY(), coord.getZ())) {
				TileEntity tile = coord.getTileEntity();
				if (tile != null) {
					if (tile instanceof ILogicTile) {
						if (!coord.contains(networkCache)) {
							networkCache.add(coord);
						}
					}
					if (tile instanceof IInfoEmitter) {
						if (!coord.contains(emitterCache)) {
							emitterCache.add(coord);
						}
					}
					if (tile instanceof IEntityNode) {
						if (!coord.contains(entityCache)) {
							entityCache.add(coord);
						}
					}
					if (tile instanceof IConnectionNode) {
						if (!coord.contains(nodeCache)) {
							nodeCache.add(coord);
						}
					} else if (tile instanceof IChannelProvider) {
						if (!coord.contains(channelCache)) {
							channelCache.add(coord);
						}
					}
				}
			}
		}
		this.entityCache.clear();
		this.entityCache.addAll(entityCache);
		this.nodeCache.clear();
		this.nodeCache.addAll(nodeCache);
		this.emitterCache.clear();
		this.emitterCache.addAll(emitterCache);
		this.networkCache.clear();
		this.networkCache.addAll(networkCache);
		this.channelCache.clear();
		this.channelCache.addAll(channelCache);
	}

	@Override
	public BlockCoords getFirstConnection(CacheTypes type) {
		try {
			return this.getConnections(type).get(0);
		} catch (Exception exception) {
			return null;
		}
	}

	@Override
	public Block getFirstBlock(CacheTypes type) {
		try {
			return this.getConnections(type).get(0).getBlock();
		} catch (Exception exception) {
			return null;
		}
	}

	@Override
	public TileEntity getFirstTileEntity(CacheTypes type) {
		try {
			return this.getConnections(type).get(0).getTileEntity();
		} catch (Exception exception) {
			return null;
		}
	}

	@Override
	public int getNetworkID() {
		return networkID;
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks) {
		for (BlockCoords coord : channelCache) {
			try {
				Object tile = FMPHelper.checkObject(coord.getTileEntity());
				if (tile instanceof IChannelProvider) {

					IChannelProvider provider = (IChannelProvider) tile;
					final ExternalCoords coords = provider.getChannel();
					TileEntity channel = coords.blockCoords.getTileEntity();
					int id = LogisticsAPI.getCableHelper().getNetwork(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite()).getNetworkID();
					if (id != -1 && !networks.contains(id)) {
						networks.add(id);
					}
				}
			} catch (Exception exception) {
			}
		}
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
			this.refreshCache(networkID);
		}
		LinkedHashMap map = new LinkedHashMap();
		for (BlockCoords coords : nodeCache) {
			try {
				Object tile = FMPHelper.checkObject(coords.getTileEntity());
				if (tile instanceof IConnectionNode) {
					LinkedHashMap<BlockCoords, ForgeDirection> connections = new LinkedHashMap();
					((IConnectionNode) tile).addConnections(connections);
					for (Entry<BlockCoords, ForgeDirection> set : connections.entrySet()) {
						if (!set.getKey().contains(map)) {
							map.put(set.getKey(), set.getValue());
						}
					}
				}
			} catch (Exception exception) {
			}
		}
		this.blockCache.clear();
		this.blockCache.putAll(map);
		ArrayList<Integer> networks = getFinalNetworkList();
		for (Integer id : networks) {
			INetworkCache network = CacheRegistry.getCache(id);
			LinkedHashMap<BlockCoords, ForgeDirection> blocks = ((LinkedHashMap<BlockCoords, ForgeDirection>) network.getExternalBlocks(false).clone());
			for (Entry<BlockCoords, ForgeDirection> set : blocks.entrySet()) {
				if (!set.getKey().contains(map)) {
					map.put(set.getKey(), set.getValue());
				}
			}

		}
		this.networkedCache.clear();
		this.networkedCache.putAll(map);

		this.cachedItems = this.getCachedItems();
		this.cachedFluids = this.getCachedFluids();
	}

	@Override
	public StorageItems getStoredItems() {
		return cachedItems;
	}

	@Override
	public StorageFluids getStoredFluids() {
		return cachedFluids;
	}
}
