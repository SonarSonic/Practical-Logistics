package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.IMultiDataCable;
import sonar.logistics.api.connecting.IMultiTile;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.registries.CableRegistry;

public class CableHelper {

	public static Object getAdjacentTile(TileEntity tile, ForgeDirection side) {
		if (tile == null || side == null) {
			return null;
		}
		BlockCoords origin = new BlockCoords(tile);
		BlockCoords target = BlockCoords.translateCoords(origin, side);
		return getTile(target.getTileEntity(tile.getWorldObj()));
	}

	public static Object getTile(Object tile) {
		tile = FMPHelper.checkObject(tile);
		return tile;
	}

	public static void addConnection(TileEntity connection, ForgeDirection side) {
		Object adjacent = getAdjacentTile(connection, side);
		if (adjacent != null) {
			if (adjacent instanceof IMultiDataCable) {
				IMultiDataCable cable = ((IMultiDataCable) adjacent);
				CableRegistry.addConnection(cable.registryID(), new BlockCoords(connection));
			}
		}
	}

	public static void removeConnection(TileEntity connection, ForgeDirection side) {
		Object adjacent = getAdjacentTile(connection, side);
		TileHandler handler = FMPHelper.getHandler(adjacent);
		if (adjacent != null) {
			if (adjacent instanceof IMultiDataCable) {
				IMultiDataCable cable = ((IMultiDataCable) adjacent);
				CableRegistry.removeConnection(cable.registryID(), new BlockCoords(connection));
			}
		}
	}

	public static void addCable(IMultiDataCable cable) {
		Object cableTile = getTile(cable.getCoords().getTileEntity());
		if (cableTile != null) {
			List adjacents = new ArrayList();
			List<Integer> ids = new ArrayList();

			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				if (!cable.isBlocked(dir)) {
					Object adjacent = getTile(BlockCoords.translateCoords(cable.getCoords(), dir).getTileEntity());
					if (adjacent != null && adjacent instanceof IMultiTile) {
						if (adjacent instanceof IInfoEmitter) {
							adjacents.add(adjacent);
						}
						if (adjacent instanceof IMultiDataCable) {
							IMultiDataCable adjTile = (IMultiDataCable) adjacent;
							if (adjTile.unlimitedChannels() == cable.unlimitedChannels()) {
								if (!adjTile.isBlocked(dir.getOpposite())) {
									adjacents.add(adjacent);
									ids.add(adjTile.registryID());
								} else {
								}
							}
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
				if ((adjacent instanceof IInfoEmitter)) {
					IInfoEmitter adjTile = (IInfoEmitter) adjacent;
					adjTile.addConnections();
				}
				if (adjacent instanceof IMultiDataCable) {
					IMultiDataCable adjCable = (IMultiDataCable) adjacent;
					if (adjCable.registryID() != cableID) {
						CableRegistry.connectNetworks(cableID, adjCable.registryID());
					}
				}
			}
		}
	}

	public static void removeCable(IMultiDataCable cable) {
		CableRegistry.removeCable(cable.registryID(), cable);
	}

	public static List<BlockCoords> getConnections(TileEntity tile, ForgeDirection dir) {
		List<BlockCoords> connections = new ArrayList();
		int registryID = -1;
		boolean unlimited = false;
		Object adjacent = getAdjacentTile(tile, dir);
		if (adjacent != null) {
			if (adjacent instanceof IMultiDataCable) {
				IMultiDataCable cable = ((IMultiDataCable) adjacent);
				if (cable.isBlocked(dir.getOpposite())) {
					return connections;
				}
				registryID = cable.registryID();
				unlimited = cable.unlimitedChannels();
			} else if (adjacent instanceof IInfoEmitter) {
				IInfoEmitter connect = ((IInfoEmitter) adjacent);
				connections.add(connect.getCoords());
			}
		}
		for (BlockCoords coord : CableRegistry.getConnections(registryID)) {
			TileEntity target = coord.getTileEntity();
			if (target != null) {
				if (target instanceof TileEntityDataEmitter) {
					// don't connect
				} else if (target instanceof IDataReceiver) {
					TileEntityDataEmitter emitter = getConnectedEmitter((IDataReceiver) target, tile);
					if (emitter != null) {
						List<BlockCoords> emitterConnections = getConnections(emitter, ForgeDirection.getOrientation(emitter.getBlockMetadata()).getOpposite());
						List<BlockCoords> toAdd = new ArrayList();
						for (BlockCoords coords : emitterConnections) {
							boolean hasCoords = false;
							for (BlockCoords currentCoords : connections) {
								if (BlockCoords.equalCoords(currentCoords, coords)) {
									hasCoords = true;
								}
							}
							if (!hasCoords) {
								toAdd.add(coords);
							}
						}
						if (unlimited) {
							connections.addAll(toAdd);
						} else if (!toAdd.isEmpty() && toAdd.get(0) != null) {
							connections.add(toAdd.get(0));
						}
					}
				} else if (target instanceof IMultiTile) {
					connections.add(coord);
				}
			}
			if (!unlimited) {
				return connections;
			}
		}
		return connections;
	}

	public static TileEntityDataEmitter getConnectedEmitter(IDataReceiver receiver, TileEntity tile) {
		if (receiver.getEmitter() != null) {
			TileEntity emitter = receiver.getEmitter().coords.getTileEntity();
			if (emitter instanceof TileEntityDataEmitter) {
				TileEntityDataEmitter dataemitter = (TileEntityDataEmitter) emitter;
				if (tile.xCoord != dataemitter.xCoord || tile.yCoord != dataemitter.yCoord || tile.zCoord != dataemitter.zCoord) {
					return dataemitter;
				}
			}
		}
		return null;
	}

	public static int canRenderConnection(TileEntity te, ForgeDirection dir) {
		Object target = getTile(te);
		Object tile = SonarHelper.getAdjacentTileEntity(te, dir);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IMultiDataCable) {
				IMultiDataCable cable = (IMultiDataCable) tile;
				if (target instanceof IMultiDataCable) {
					if (cable.unlimitedChannels() != ((IMultiDataCable) target).unlimitedChannels()) {
						return 0;
					}
				}
				if (!cable.isBlocked(dir.getOpposite())) {
					return cable.unlimitedChannels() ? 2 : 1;
				}

			} else if (tile instanceof IMultiTile) {
				int connect = (((IMultiTile) tile).canConnect(dir.getOpposite())) ? 1 : 0;
				if (connect != 0 && target instanceof IMultiDataCable) {
					return ((IMultiDataCable) target).unlimitedChannels() ? 2 : 1;
				}
				return connect;
			}
		}
		return 0;
	}
}
