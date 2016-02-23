package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.registries.CableRegistry;

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

	public List<BlockCoords> getConnections(TileEntity tile, ForgeDirection dir) {
		ArrayList<BlockCoords> connections = new ArrayList();
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
			} else if (adjacent instanceof IChannelProvider) {
				addChannelConnections(connections, (IChannelProvider) adjacent, tile, cableType);
			} else if (adjacent instanceof IInfoEmitter) {
				IInfoEmitter connect = ((IInfoEmitter) adjacent);
				connections.add(connect.getCoords());
			}
		}
		for (BlockCoords coord : (ArrayList<BlockCoords>) CableRegistry.getConnections(registryID).clone()) {
			TileEntity target = coord.getTileEntity();
			if (target != null) {
				if (target instanceof TileEntityDataEmitter) {
				} else if (target instanceof IChannelProvider) {
					addChannelConnections(connections, (IChannelProvider) target, tile, cableType);
				} else if (target instanceof ILogicTile) {
					connections.add(coord);
				}
			}
			if (!cableType.hasUnlimitedConnections()) {
				return connections;
			}
		}
		return connections;
	}

	private static void addChannelConnections(ArrayList<BlockCoords> connections, IChannelProvider receiver, TileEntity tile, CableType cableType) {
		TileEntity channel = null;
		if (receiver.getChannel() != null && receiver.getChannel().blockCoords != null) {
			TileEntity target = receiver.getChannel().blockCoords.getTileEntity();
			if (target != null && target instanceof ILogicTile) {
				ILogicTile dataemitter = (ILogicTile) target;
				if (tile.xCoord != dataemitter.getCoords().getX() || tile.yCoord != dataemitter.getCoords().getY() || tile.zCoord != dataemitter.getCoords().getZ()) {
					channel = target;
				}
			}
		}
		if (channel != null) {
			if (!(channel instanceof TileEntityDataEmitter)) {
				connections.add(new BlockCoords(channel));
			} else {
				List<BlockCoords> emitterConnections = LogisticsAPI.getCableHelper().getConnections(channel, ForgeDirection.getOrientation(channel.getBlockMetadata()).getOpposite());
				List<BlockCoords> toAdd = new ArrayList();
				for (BlockCoords coords : emitterConnections) {
					boolean hasCoords = false;
					for (BlockCoords currentCoords : (ArrayList<BlockCoords>) connections.clone()) {
						if (BlockCoords.equalCoords(currentCoords, coords)) {
							hasCoords = true;
						}
					}
					if (!hasCoords) {
						toAdd.add(coords);
					}
				}
				if (cableType.hasUnlimitedConnections()) {
					connections.addAll(toAdd);
				} else if (!toAdd.isEmpty() && toAdd.get(0) != null) {
					connections.add(toAdd.get(0));
				}
			}
		}
	}

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
				return canConnect? CableType.DATA_CABLE : CableType.NONE;
			}
		}
		return CableType.NONE;
	}
}
