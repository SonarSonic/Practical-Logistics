package sonar.logistics.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
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

	public LocalNetworkCache(ILogicTile tile) {
		this.tile = tile;
	}

	@Override
	public Entry<BlockCoords, ForgeDirection> getExternalBlock(boolean includeChannels) {
		try {
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
				final ExternalCoords coords = provider.getChannel();
				TileEntity channel = coords.blockCoords.getTileEntity();
				return LogisticsAPI.getCableHelper().getNetwork(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite()).getExternalBlock(true);
			}
		} catch (Exception exception) {
		}
		return null;
	}

	@Override
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks(boolean includeChannels) {
		LinkedHashMap map = new LinkedHashMap();
		try {
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
				final ExternalCoords coords = provider.getChannel();
				TileEntity channel = coords.blockCoords.getTileEntity();
				return LogisticsAPI.getCableHelper().getNetwork(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite()).getExternalBlocks(true);
			}
		} catch (Exception exception) {
			return new LinkedHashMap();
		}
		return map;
	}

	@Override
	public ArrayList<BlockCoords> getConnections(CacheTypes type, boolean includeChannels) {
		ArrayList array = new ArrayList();
		if (tile instanceof IChannelProvider) {
			try {
				IChannelProvider provider = (IChannelProvider) tile;
				final ExternalCoords coords = provider.getChannel();
				TileEntity channel = coords.blockCoords.getTileEntity();
				return LogisticsAPI.getCableHelper().getNetwork(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite()).getConnections(type, includeChannels);
			} catch (Exception exception) {
			}
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
		try {
			return this.getConnections(type, true).get(0);
		} catch (Exception exception) {
			return null;
		}
	}

	@Override
	public Block getFirstBlock(CacheTypes type) {
		try {
			return this.getConnections(type, true).get(0).getBlock();
		} catch (Exception exception) {
			return null;
		}
	}

	@Override
	public TileEntity getFirstTileEntity(CacheTypes type) {
		try {
			return this.getConnections(type, true).get(0).getTileEntity();
		} catch (Exception exception) {
			return null;
		}
	}

	@Override
	public int getNetworkID() {
		if (tile instanceof IChannelProvider) {
			try {
				IChannelProvider provider = (IChannelProvider) tile;
				final ExternalCoords coords = provider.getChannel();
				TileEntity channel = coords.blockCoords.getTileEntity();
				return LogisticsAPI.getCableHelper().getNetwork(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite()).getNetworkID();
			} catch (Exception exception) {
			}

		}
		return -1;
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks) {
		return networks;
	}

	@Override
	public StorageItems getStoredItems() {
		return this.getCachedItems();
	}

	@Override
	public StorageFluids getStoredFluids() {
		return this.getCachedFluids();
	}
}
