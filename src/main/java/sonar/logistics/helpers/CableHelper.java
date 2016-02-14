package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.registries.CableRegistry;

public class CableHelper {

	public static void addConnection(TileEntity connection, ForgeDirection side) {
		Object adjacent = FMPHelper.getAdjacentTile(connection, side);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (!cable.isBlocked(side.getOpposite())) {
					CableRegistry.addConnection(cable.registryID(), new BlockCoords(connection));
				}
			}
		}
	}

	public static void removeConnection(TileEntity connection, ForgeDirection side) {
		Object adjacent = FMPHelper.getAdjacentTile(connection, side);
		TileHandler handler = FMPHelper.getHandler(adjacent);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (!cable.isBlocked(side.getOpposite())) {
					CableRegistry.removeConnection(cable.registryID(), new BlockCoords(connection));
				}
			}
		}
	}

	public static void addCable(IDataCable cable) {
		Object cableTile = FMPHelper.getTile(cable.getCoords().getTileEntity());
		if (cableTile != null) {
			List adjacents = new ArrayList();
			List<Integer> ids = new ArrayList();

			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				if (!((IDataCable) cableTile).isBlocked(dir)) {
					Object adjacent = FMPHelper.getTile(BlockCoords.translateCoords(cable.getCoords(), dir).getTileEntity());
					if (adjacent != null && adjacent instanceof ILogicTile) {
						if (adjacent instanceof IInfoEmitter) {
							adjacents.add(adjacent);
						}
						if (adjacent instanceof IDataCable) {
							IDataCable adjTile = (IDataCable) adjacent;
							if (adjTile.unlimitedChannels() == cable.unlimitedChannels() && adjTile.registryID() != -2) {
								if (!adjTile.isBlocked(dir.getOpposite())) {
									adjacents.add(adjacent);
									ids.add(adjTile.registryID());
								} else {
								}
							}
						}
					}
				} else {
					
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
				if (adjacent instanceof IDataCable) {
					IDataCable adjCable = (IDataCable) adjacent;
					if (adjCable.registryID() != cableID) {
						CableRegistry.connectNetworks(cableID, adjCable.registryID());
					}
				}
			}
		}
	}

	public static void removeCable(IDataCable cable) {
		CableRegistry.removeCable(cable.registryID(), cable);
	}

	public static List<BlockCoords> getConnections(TileEntity tile, ForgeDirection dir) {
		ArrayList<BlockCoords> connections = new ArrayList();
		int registryID = -1;
		boolean unlimited = false;
		Object adjacent = FMPHelper.getAdjacentTile(tile, dir);
		if (adjacent != null) {
			if (adjacent instanceof IDataCable) {
				IDataCable cable = ((IDataCable) adjacent);
				if (cable.isBlocked(dir.getOpposite())) {
					return connections;
				}
				registryID = cable.registryID();
				unlimited = cable.unlimitedChannels();
			} else if (adjacent instanceof IChannelProvider) {
				addChannelConnections(connections, (IChannelProvider) adjacent, tile, true);
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
					List<BlockCoords> channels = new ArrayList();
					addChannelConnections(connections, (IChannelProvider) target, tile, unlimited);
					connections.addAll(connections);
				} else if (target instanceof ILogicTile) {
					connections.add(coord);
				}
			}
			if (!unlimited) {
				return connections;
			}
		}
		return connections;
	}

	public static void addChannelConnections(ArrayList<BlockCoords> connections, IChannelProvider receiver, TileEntity tile, boolean unlimited) {
		TileEntity emitter = null;
		if (receiver.getChannel() != null && receiver.getChannel().blockCoords != null) {
			TileEntity target = receiver.getChannel().blockCoords.getTileEntity();
			if (target != null && target instanceof ILogicTile) {
				ILogicTile dataemitter = (ILogicTile) target;
				if (tile.xCoord != dataemitter.getCoords().getX() || tile.yCoord != dataemitter.getCoords().getY() || tile.zCoord != dataemitter.getCoords().getZ()) {
					emitter = target;
				}
			}
		}
		if (emitter != null) {
			if (!(emitter instanceof TileEntityDataEmitter)) {
				connections.add(new BlockCoords(emitter));
			} else {
				List<BlockCoords> emitterConnections = getConnections(emitter, ForgeDirection.getOrientation(emitter.getBlockMetadata()).getOpposite());
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
				if (unlimited) {
					connections.addAll(toAdd);
				} else if (!toAdd.isEmpty() && toAdd.get(0) != null) {
					connections.add(toAdd.get(0));
				}
			}
		}
	}

	public static int canRenderConnection(TileEntity te, ForgeDirection dir) {
		Object target = FMPHelper.getTile(te);
		Object tile = SonarHelper.getAdjacentTileEntity(te, dir);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (target instanceof IDataCable) {
					if (cable.unlimitedChannels() != ((IDataCable) target).unlimitedChannels()) {
						return 0;
					}
				}
				if (!cable.isBlocked(dir.getOpposite())) {
					return cable.unlimitedChannels() ? 2 : 1;
				}

			} else if (tile instanceof ILogicTile) {
				int connect = (((ILogicTile) tile).canConnect(dir.getOpposite())) ? 1 : 0;
				if (connect != 0 && target instanceof IDataCable) {
					return ((IDataCable) target).unlimitedChannels() ? 2 : 1;
				}
				return connect;
			}
		}
		return 0;
	}
}
