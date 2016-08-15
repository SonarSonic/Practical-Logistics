package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import mcmultipart.multipart.IMultipartContainer;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.MultipartHelper;
import mcmultipart.multipart.PartSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.connections.CableRegistry;
import sonar.logistics.connections.CacheRegistry;

public class CableHelper extends CablingWrapper {

	public IDataCable getCableFromCoords(BlockCoords coords) {
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
			if (part != null && part instanceof IDataCable) {
				return (IDataCable) part;
			}
		}
		return null;
	}
	public INetworkCache addCable(IDataCable cable) {
		ArrayList<Pair<CableType, Integer>> connections = new ArrayList();
		int cableID = -1;
		int lastSize = -1;
		BlockCoords coords = cable.getCoords();
		for (EnumFacing dir : EnumFacing.values()) {
			if (cable.canConnect(dir)) {
				Pair<CableType, Integer> connection = getConnectionType(coords.getWorld(), coords.getBlockPos(), dir, cable.getCableType());
				if (connection.a != CableType.NONE) {
					if (connection.b != -1) {
						List<BlockCoords> cables = CableRegistry.getCables(connection.b);
						if (cables.size() > lastSize) {
							cableID = connection.b;
							lastSize = cables.size();
						}
						connections.add(connection);
					}
				}
			}
		}
		CableRegistry.addCable(cableID == -1 ? cableID = CableRegistry.getNextAvailableID() : cableID, cable, true);
		for (Pair<CableType, Integer> connection : connections) {
			if (connection.b != cableID) {
				CableRegistry.connectNetworks(cableID, connection.b);
			}
		}
		return CacheRegistry.getCache(cableID);
	}

	public void removeCable(IDataCable cable) {
		CableRegistry.removeCable(cable.registryID(), cable);
	}

	public void refreshConnections(IDataCable cable) {
		BlockCoords coords = cable.getCoords();
		for (EnumFacing dir : EnumFacing.values()) {
			Pair<CableType, Integer> connection = LogisticsAPI.getCableHelper().getConnectionType(coords.getWorld(), coords.getBlockPos(), dir, cable.getCableType());
			boolean canConnect = cable.canConnect(dir);
			if ((!canConnect && connection.a.canConnect(cable.getCableType()))) {
				cable.removeCable();
				cable.addCable();
			} else if ((canConnect && connection.a.canConnect(cable.getCableType()) && connection.b != cable.registryID())) {
				CableRegistry.connectNetworks(cable.registryID(), connection.b);
			}
		}
	}

	public INetworkCache getNetwork(TileEntity tile, EnumFacing dir) {
		Pair<CableType, Integer> connection = getConnectionType(tile.getWorld(), tile.getPos(), dir, CableType.DATA_CABLE);
		if (connection.a != CableType.NONE && connection.b != -1) {
			INetworkCache cache = CacheRegistry.getCache(connection.b);
			if (cache != null) {
				return cache;
			}
		}
		return EmptyNetworkCache.INSTANCE;
	}

	public INetworkCache getNetwork(int registryID) {
		INetworkCache cache = CacheRegistry.getCache(registryID);
		return cache != null ? cache : EmptyNetworkCache.INSTANCE;
	}

	/*
	public Map<BlockCoords, EnumFacing> getTileConnections(List<BlockCoords> network) {
		if (network == null) {
			return Collections.EMPTY_MAP;
		}
		Map<BlockCoords, EnumFacing> connections = new LinkedHashMap();
		for (BlockCoords connect : network) {
			TileEntity tile = connect.getTileEntity();
			if (tile != null && tile instanceof IConnectionNode) {
				((IConnectionNode) tile).addConnections(connections);
			} else {
				IMultipartContainer container = MultipartHelper.getPartContainer(connect.getWorld(), connect.getBlockPos());
				if (container != null) {
					ISlottedPart part = container.getPartInSlot(PartSlot.CENTER);
					if (part == null) {
						continue;
					} else if (part instanceof IDataCable && ((IDataCable) part).hasConnections()) {
						container.getParts().forEach(multipart -> {
							if (multipart instanceof IConnectionNode) {
								((IConnectionNode) multipart).addConnections(connections);
							}
						});
					}
				}
			}
		}
		return connections;

	}
	public Map<BlockCoords, EnumFacing> getTileConnections(TileEntity tile, EnumFacing dir) {
		LinkedHashMap<BlockCoords, EnumFacing> connections = new LinkedHashMap();
		int registryID = -1;
		CableType cableType = CableType.NONE;
		Object adjacent = OLDMultipartHelper.getAdjacentTile(tile, dir);
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
				((IConnectionNode) node).addConnections(connections);
			}
		}
		if (registryID != -1) {
			try {
				LinkedHashMap<BlockCoords, EnumFacing> cacheList = CacheRegistry.getChannelArray(registryID);
				if (!cacheList.isEmpty()) {
					if (cableType.hasUnlimitedConnections()) {
						connections.putAll(cacheList);
					} else {
						for (Entry<BlockCoords, EnumFacing> entry : cacheList.entrySet()) {
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
	*/

	public Pair<CableType, Integer> getConnectionType(World world, BlockPos pos, EnumFacing dir, CableType cableType) {
		BlockPos offset = pos.offset(dir);
		IMultipartContainer container = MultipartHelper.getPartContainer(world, offset);
		if (container != null) {
			return getConnectionType(container, dir, cableType);
		} else {
			TileEntity tile = world.getTileEntity(offset);
			if (tile != null) {
				return getConnectionTypeFromObject(tile, dir, cableType);
			}
		}
		return new Pair(CableType.NONE, -1);
	}

	/** checks what cable type can be connected via a certain direction, assumes the other block can connect from this side */
	public Pair<CableType, Integer> getConnectionType(IMultipartContainer container, EnumFacing dir, CableType cableType) {
		ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(dir.getOpposite()));
		if (part != null) {
			return getConnectionTypeFromObject(part, dir, cableType);
		} else {
			ISlottedPart centre = container.getPartInSlot(PartSlot.CENTER);
			if (centre != null && centre instanceof IDataCable) {
				return getConnectionTypeFromObject(centre, dir, cableType);
			}
		}
		return new Pair(CableType.NONE, -1);
	}

	public Pair<CableType, Integer> getConnectionTypeFromObject(Object connection, EnumFacing dir, CableType cableType) {
		if (connection instanceof IDataCable) {
			IDataCable cable = (IDataCable) connection;
			if (cable.getCableType().canConnect(cableType)) {
				return cable.canConnect(dir.getOpposite()) ? new Pair(cable.getCableType(), cable.registryID()) : new Pair(CableType.NONE, -1);
			}
		} else if (connection instanceof ILogicTile) {
			return ((ILogicTile) connection).canConnect(dir.getOpposite()) ? new Pair(CableType.BLOCK_CONNECTION, -1) : new Pair(CableType.NONE, -1);
		}
		return new Pair(CableType.NONE, -1);
	}
}
