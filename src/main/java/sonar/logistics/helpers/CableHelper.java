package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionArray;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.cache.LocalNetworkCache;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.registries.CableRegistry;
import sonar.logistics.registries.CacheRegistry;

public class CableHelper extends CablingWrapper {

	public void addConnection(TileEntity connection, ForgeDirection side) {
		Object adjacent = FMPHelper.getAdjacentTile(connection, side);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (!cable.isBlocked(side.getOpposite())) {
					addConnection(cable.registryID(), new BlockCoords(connection));
				}
			}
		}
	}

	public void addConnection(int registryID, BlockCoords coords) {
		CableRegistry.addConnection(registryID, coords);
	}

	public void removeConnection(TileEntity connection, ForgeDirection side) {
		Object adjacent = FMPHelper.getAdjacentTile(connection, side);
		TileHandler handler = FMPHelper.getHandler(adjacent);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (!cable.isBlocked(side.getOpposite())) {
					removeConnection(cable.registryID(), new BlockCoords(connection));
				}
			}
		}
	}

	public void removeConnection(int registryID, BlockCoords coords) {
		CableRegistry.removeConnection(registryID, coords);
	}

	public void addCable(IDataCable cable) {
		Object cableTile = FMPHelper.getTile(cable.getCoords().getTileEntity());
		if (cableTile != null) {
			List adjacents = new ArrayList();
			List<Integer> ids = new ArrayList();

			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				if (!((IDataCable) cableTile).isBlocked(dir)) {
					Object adjacent = FMPHelper.getTile(BlockCoords.translateCoords(cable.getCoords(), dir).getTileEntity());
					if (adjacent != null && adjacent instanceof ILogicTile) {
						if (adjacent instanceof IDataCable) {
							IDataCable adjTile = (IDataCable) adjacent;
							if (adjTile.getCableType().canConnect(cable.getCableType()) && adjTile.registryID() != -2 && !adjTile.isBlocked(dir.getOpposite())) {
								adjacents.add(adjacent);
								ids.add(adjTile.registryID());
							}
						} else if (adjacent instanceof IInfoEmitter) {
							adjacents.add(adjacent);
						}

					}
				}
			}
			int cableID = -1;
			int lastSize = -1;
			for (Integer id : ids) {
				if (id != -1) {
					List<BlockCoords> cables = CableRegistry.getCables(id);
					if (cables.size() > lastSize) {
						cableID = id;
						lastSize = cables.size();
					}
				}
			}
			if (cableID == -1) {
				cableID = CableRegistry.getNextAvailableID();
			}
			CableRegistry.addCable(cableID, cable.getCoords());

			List<BlockCoords> coords = new ArrayList();
			for (Object adjacent : adjacents) {
				if (adjacent instanceof IDataCable) {
					IDataCable adjCable = (IDataCable) adjacent;
					if (adjCable.registryID() != cableID) {
						CableRegistry.connectNetworks(cableID, adjCable.registryID());
					}
				}
				if ((adjacent instanceof IInfoEmitter)) {
					IInfoEmitter adjTile = (IInfoEmitter) adjacent;
					adjTile.addConnections();
				}
			}
		}
	}

	public void removeCable(IDataCable cable) {
		CableRegistry.removeCable(cable.registryID(), cable);
	}

	public INetworkCache getNetwork(TileEntity tile, ForgeDirection dir) {
		//ArrayList<BlockCoords> connections = new ArrayList();
		int registryID = -1;
		CableType cableType = CableType.NONE;
		Object adjacent = FMPHelper.getAdjacentTile(tile, dir);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (cable.isBlocked(dir.getOpposite())) {
					return CacheRegistry.EMPTY_CACHE;
				}
				registryID = cable.registryID();
				cableType = cable.getCableType();
			} else if (adjacent instanceof IChannelProvider) {
				// help needed probably
				// addChannelConnections(connections, (IChannelProvider) adjacent, tile, cableType);
			} else if (adjacent instanceof ILogicTile) {
				
				//YOU NEED TO REWRITE THE WHOLE MOD, GIVING EVERY SINGLE BLOCK IT'S OWN ID :O, OR YOU COULD MAKE A PRIVATE CACHE SYSTEM, RETRIEVING THE CACHE FROM A TILE
				
				return new LocalNetworkCache((ILogicTile) adjacent);
			}
		}
		if (registryID != -1) {
			try {
				return CacheRegistry.getCache(registryID);
				/*
				ArrayList<BlockCoords> cacheList = CacheRegistry.getCacheList(cacheType, registryID);
				if (!cacheList.isEmpty()) {
					if (cableType.hasUnlimitedConnections()) {
						connections.addAll(cacheList);
					} else {
						connections.add(cacheList.get(0));
					}
				}
				*/
			} catch (Exception exception) {
				Logistics.logger.error("CableHelper: " + exception.getLocalizedMessage());
			}
		}
		/*
		 * for (BlockCoords coord : (ArrayList<BlockCoords>) CableRegistry.getConnections(registryID).clone()) { TileEntity target = coord.getTileEntity(); if (target != null) { if (target instanceof TileEntityDataEmitter) { } else if (target instanceof IChannelProvider) { addChannelConnections(connections, (IChannelProvider) target, tile, cableType); }else if (target instanceof ILogicTile) { connections.add(coord); } } if (!cableType.hasUnlimitedConnections()) { return connections; } }
		 */
		return CacheRegistry.EMPTY_CACHE ;
	}

	/*
	 * private static void addChannelConnections(ArrayList<BlockCoords> connections, IChannelProvider receiver, TileEntity tile, CableType cableType) { if (receiver.getChannel() != null && receiver.getChannel().blockCoords != null) { BlockCoords target = receiver.getChannel().blockCoords; if (!target.equals(new BlockCoords(tile))) { TileEntity channel = target.getTileEntity(); if (channel == null || !(channel instanceof TileEntityDataEmitter)) { Block block = target.getBlock(target.getWorld()); if (block != null) connections.add(target); } else { List<BlockCoords> emitterConnections = LogisticsAPI.getCableHelper().getConnections(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite()); List<BlockCoords> toAdd = new ArrayList(); for (BlockCoords coords :
	 * emitterConnections) { boolean hasCoords = false; for (BlockCoords currentCoords : (ArrayList<BlockCoords>) connections.clone()) { if (BlockCoords.equalCoords(currentCoords, coords)) { hasCoords = true; } } if (!hasCoords) { toAdd.add(coords); } } if (cableType.hasUnlimitedConnections()) { connections.addAll(toAdd); } else if (!toAdd.isEmpty() && toAdd.get(0) != null) { connections.add(toAdd.get(0)); } } }
	 * 
	 * } }
	 */
	public Map<BlockCoords, ForgeDirection> getTileConnections(List<BlockCoords> network) {
		if (network == null) {
			return Collections.EMPTY_MAP;
		}
		Map<BlockCoords, ForgeDirection> connections = new LinkedHashMap();
		for (BlockCoords connect : network) {
			TileEntity node = connect.getTileEntity();
			if (node != null && node instanceof IConnectionNode) {
				connections.putAll(((IConnectionNode) node).getConnections());
			}
		}
		return connections;

	}

	public Map<BlockCoords, ForgeDirection> getTileConnections(TileEntity tile, ForgeDirection dir) {
		LinkedHashMap<BlockCoords, ForgeDirection> connections = new LinkedHashMap();
		int registryID = -1;
		CableType cableType = CableType.NONE;
		Object adjacent = FMPHelper.getAdjacentTile(tile, dir);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (cable.isBlocked(dir.getOpposite())) {
					return connections;
				}
				registryID = cable.registryID();
				cableType = cable.getCableType();
			} else if (adjacent instanceof IConnectionNode) {
				IConnectionNode node = (IConnectionNode) adjacent;
				connections.putAll(node.getConnections());
				// help needed probably
				// addChannelConnections(connections, (IChannelProvider) adjacent, tile, cableType);
			}
		}
		if (registryID != -1) {
			try {
				LinkedHashMap<BlockCoords, ForgeDirection> cacheList = CacheRegistry.getChannelArray(registryID);
				if (!cacheList.isEmpty()) {
					if (cableType.hasUnlimitedConnections()) {
						connections.putAll(cacheList);
					} else {
						for (Entry<BlockCoords, ForgeDirection> entry : cacheList.entrySet()) {
							if (entry.getKey().getBlock(entry.getKey().getWorld()) != null) {
								connections.put(entry.getKey(), entry.getValue());
							}
						}
					}
				}
			} catch (Exception exception) {
				Logistics.logger.error("CableHelper: " + exception.getLocalizedMessage());
			}
		}
		return connections;
	}

	public CableType canRenderConnection(TileEntity te, ForgeDirection dir, CableType cableType) {
		Object target = FMPHelper.getTile(te);
		Object tile = SonarHelper.getAdjacentTileEntity(te, dir);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (target instanceof IDataCable) {
					if (!cable.getCableType().canConnect(((IDataCable) target).getCableType())) {
						return CableType.NONE;
					}
				}
				if (!cable.isBlocked(dir.getOpposite())) {
					if (cableType.canConnect(cable.getCableType())) {
						return cable.getCableType();
					}
				}

			} else if (tile instanceof ILogicTile) {
				boolean canConnect = (((ILogicTile) tile).canConnect(dir.getOpposite()));
				if (canConnect && target instanceof IDataCable) {
					if (cableType.canConnect(((IDataCable) target).getCableType())) {
						return ((IDataCable) target).getCableType();
					}
				}
				return canConnect ? CableType.DATA_CABLE : CableType.NONE;
			}
		}
		return CableType.NONE;
	}
}
