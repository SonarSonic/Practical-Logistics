package sonar.logistics.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IStorageCache;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.utils.ExternalCoords;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;

public class LocalNetworkCache extends StorageCache {
	public final ILogicTile tile;

	private StorageItems cachedItems = StorageItems.EMPTY;
	private StorageFluids cachedFluids = StorageFluids.EMPTY;
	
	public LocalNetworkCache(ILogicTile tile) {
		this.tile = tile;
	}

	@Override
	public Entry<BlockCoords, ForgeDirection> getExternalBlock(boolean includeChannels) {
		if (tile instanceof IConnectionNode) {
			LinkedHashMap<BlockCoords, ForgeDirection> map = new LinkedHashMap();
			((IConnectionNode) tile).addConnections(map);
			for (Entry<BlockCoords, ForgeDirection> entry : map.entrySet()) {
				if (entry.getKey().getBlock(entry.getKey().getWorld()) != null) {
					return entry;
				}
			}
		}
		if (tile instanceof IChannelProvider) {
			IChannelProvider provider = (IChannelProvider) tile;
			return provider.getNetwork().getExternalBlock(includeChannels);
		}
		return null;
	}

	@Override
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks(boolean includeChannels) {
		LinkedHashMap map = new LinkedHashMap();
		if (tile instanceof IConnectionNode) {
			LinkedHashMap<BlockCoords, ForgeDirection> connections = new LinkedHashMap();
			((IConnectionNode) tile).addConnections(connections);
			for (Entry<BlockCoords, ForgeDirection> set : connections.entrySet()) {
				if (!map.containsKey(set.getKey())) {
					map.put(set.getKey(), set.getValue());
				}
			}
		}
		if (tile instanceof IChannelProvider) {
			IChannelProvider provider = (IChannelProvider) tile;
			return provider.getNetwork().getExternalBlocks(includeChannels);
		}
		return map;
	}

	@Override
	public ArrayList<BlockCoords> getConnections(CacheTypes type, boolean includeChannels) {
		ArrayList array = new ArrayList();
		if (tile instanceof IChannelProvider) {
			IChannelProvider provider = (IChannelProvider) tile;
			return provider.getNetwork().getConnections(type, includeChannels);
		} else {
			switch (type) {
			case ENTITY_NODES:
				if (tile instanceof IEntityNode) {
					array.add(tile.getCoords());
				}
				break;
			case NODES:
				if (tile instanceof IConnectionNode) {
					array.add(tile.getCoords());
				}
				break;
			case EMITTER:
				if (tile instanceof IInfoEmitter) {
					array.add(tile.getCoords());
				}
				break;
			case NETWORK:
				if (tile instanceof ILogicTile) {
					array.add(tile.getCoords());
				}
				break;
			default:
				break;
			}
		}
		return array;
	}

	@Override
	public BlockCoords getFirstConnection(CacheTypes type) {
		ArrayList<BlockCoords> coords = this.getConnections(type, true);
		if (coords.isEmpty()) {
			return null;
		}
		return coords.get(0);
	}

	@Override
	public Block getFirstBlock(CacheTypes type) {
		BlockCoords connection = this.getFirstConnection(type);
		if (connection == null) {
			return null;
		}
		return connection.getBlock();
	}

	@Override
	public TileEntity getFirstTileEntity(CacheTypes type) {
		BlockCoords connection = this.getFirstConnection(type);
		if (connection == null) {
			return null;
		}
		return connection.getTileEntity();
	}

	@Override
	public int getNetworkID() {
		if (tile instanceof IChannelProvider) {
			IChannelProvider provider = (IChannelProvider) tile;
			return provider.getNetwork().getNetworkID();
		}
		return -1;
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks) {
		return networks;
	}
	//LOCAL NETWORKS UPDATE ITEMS/FLUIDS EVERY SINGLE TICK!!!!!!! THIS NEEDS SOME ATTENTION, SOME CONFIGURATIONS MAY LAG LIKE CRAZY 
	@Override
	public StorageItems getStoredItems() {
		if (tile instanceof IChannelProvider) {
			IChannelProvider provider = (IChannelProvider) tile;
			INetworkCache cache= provider.getNetwork();
			if(cache!=null && cache instanceof IStorageCache){
				return ((IStorageCache) cache).getStoredItems();
			}
		}
		return this.getCachedItems(cachedItems.items);
	}

	@Override
	public StorageFluids getStoredFluids() {
		if (tile instanceof IChannelProvider) {
			IChannelProvider provider = (IChannelProvider) tile;
			INetworkCache cache= provider.getNetwork();
			if(cache!=null && cache instanceof IStorageCache){
				return ((IStorageCache) cache).getStoredFluids();
			}
		}
		return this.getCachedFluids(cachedFluids.fluids);
	}
}
