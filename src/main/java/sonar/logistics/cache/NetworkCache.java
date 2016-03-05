package sonar.logistics.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.registries.CableRegistry;

public class NetworkCache implements IRefreshCache {

	/** all {@link IEntityNode} */
	private final ArrayList<BlockCoords> entityCache = new ArrayList<BlockCoords>();
	/** all {@link IInfoEmitter} */
	private final ArrayList<BlockCoords> emitterCache = new ArrayList<BlockCoords>();
	/** all {@link ILogicTile} */
	private final ArrayList<BlockCoords> networkCache = new ArrayList<BlockCoords>();
	/** all {@link IChannelProvider} */
	private final ArrayList<BlockCoords> channelCache = new ArrayList<BlockCoords>();
	/** all {@link Block} */
	private final LinkedHashMap<BlockCoords, ForgeDirection> blockCache = new LinkedHashMap<BlockCoords, ForgeDirection>();

	@Override
	public Entry<BlockCoords, ForgeDirection> getExternalBlock() {
		for (Entry<BlockCoords, ForgeDirection> entry : blockCache.entrySet()) {
			if (entry.getKey().getBlock(entry.getKey().getWorld()) != null) {
				return entry;
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
		case NETWORK:
			return networkCache;
		case CABLE:
		default:
			break;
		}
		return new ArrayList();
	}

	public ArrayList<BlockCoords> getChannelledConnections() {
		ArrayList<BlockCoords> coords = new ArrayList();
		for (BlockCoords coord : channelCache) {
			TileEntity tile = coord.getTileEntity();
			if (tile instanceof IChannelProvider) {
				BlockCoords channel = ((IChannelProvider) tile).getChannel().blockCoords;
				channel.getTileEntity();
				// add connections for Data Receivers
			}
		}
		return coords;
	}

	@Override
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks() {
		return blockCache;
	}

	@Override
	public void refreshCache(int networkID) {
		ArrayList<BlockCoords> coords = (ArrayList<BlockCoords>) CableRegistry.getConnections(networkID).clone();
		Iterator<BlockCoords> iterator = coords.iterator();

		LinkedHashMap<BlockCoords, ForgeDirection> blockCache = new LinkedHashMap<BlockCoords, ForgeDirection>();
		ArrayList<BlockCoords> entityCache = new ArrayList<BlockCoords>();
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
						if (!networkCache.contains(coord)) {
							networkCache.add(coord);
						}
					}
					if (tile instanceof IInfoEmitter) {
						if (!emitterCache.contains(coord)) {
							emitterCache.add(coord);
						}
					}
					if (tile instanceof IEntityNode) {
						if (!entityCache.contains(coord)) {
							entityCache.add(coord);
						}
					}
					if (tile instanceof IConnectionNode) {
						IConnectionNode node = (IConnectionNode) tile;				
						for (Entry<BlockCoords, ForgeDirection> set : node.getConnections().entrySet()) {
							if (!blockCache.containsKey(set.getKey())) {
								blockCache.put(set.getKey(), set.getValue());
							}
						}

					} else if (tile instanceof IChannelProvider) {
						if (!channelCache.contains(coord)) {
							channelCache.add(coord);
						}
						/*
						 * BlockCoords provided = channelProvider.getCoords(); if (!provided.equals(new BlockCoords(tile))) { TileEntity channel = provided.getTileEntity(); if (channel == null || !(channel instanceof TileEntityDataEmitter)) { Block block = provided.getBlock(provided.getWorld()); if (block != null) machinesCache.add(provided); } else { List<BlockCoords> emitterConnections = LogisticsAPI.getCableHelper().getConnections(channel, ForgeDirection .getOrientation(channel.getBlockMetadata ()).getOpposite()); List<BlockCoords> toAdd = new ArrayList(); for (BlockCoords coords : emitterConnections) { boolean hasCoords = false; for (BlockCoords currentCoords : (ArrayList<BlockCoords>) connections.clone()) { if (BlockCoords.equalCoords(currentCoords, coords)) { hasCoords = true; } } if
						 * (!hasCoords) { toAdd.add(coords); } } if (cableType.hasUnlimitedConnections()) { connections.addAll(toAdd); } else if (!toAdd.isEmpty() && toAdd.get(0) != null) { connections.add(toAdd.get(0)); } } }
						 */
						// channelCache.add(channel.getCoords());
					}
				}
			}
		}
		this.blockCache.clear();
		this.blockCache.putAll(blockCache);
		this.entityCache.clear();
		this.entityCache.addAll(entityCache);
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
}
