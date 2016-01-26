package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.SonarAPI;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.api.connecting.IDataTile;
import sonar.logistics.api.connecting.IMultiDataCable;
import sonar.logistics.api.connecting.IMultiTile;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import codechicken.multipart.TileMultipart;

public class MultiCableHelper {

	public static TileEntity getAdjacentTile(TileEntity tile, ForgeDirection side) {
		BlockCoords origin = new BlockCoords(tile);
		BlockCoords target = BlockCoords.translateCoords(origin, side);
		return target.getTileEntity(tile.getWorldObj());
	}

	public static void addConnection(TileEntity connections, ForgeDirection side) {
		TileEntity adjacent = getAdjacentTile(connections, side);
		if (adjacent != null) {
			if (adjacent instanceof IMultiDataCable) {
				IMultiDataCable cable = ((IMultiDataCable) adjacent);
				cable.addCoords(new BlockCoords(connections));
			}
		}
	}

	public static void removeConnection(TileEntity connection, ForgeDirection side) {
		TileEntity adjacent = getAdjacentTile(connection, side);
		if (adjacent != null) {
			if (adjacent instanceof IMultiDataCable) {
				IMultiDataCable cable = ((IMultiDataCable) adjacent);
				cable.addCoords(new BlockCoords(connection));
			}
		}
	}

	public static void addCable(TileEntity cable) {
		List adjacents = new ArrayList();
		for (int i = 0; i < 6; i++) {
			Object adjacent = FMPHelper.checkObject(getAdjacentTile(cable, ForgeDirection.getOrientation(i)));
			if (adjacent != null && adjacent instanceof IMultiTile) {
				adjacents.add(adjacent);
				if (adjacent instanceof IMultiDataCable) {
					IMultiDataCable adjTile = (IMultiDataCable) adjacent;
					adjTile.updateConnections();
				}
			}
		}
		for (Object adjacent : adjacents) {
			if (!(adjacent instanceof IMultiDataCable)) {
				IMultiTile adjTile = (IMultiTile) adjacent;
				adjTile.updateConnections();
			}
		}
	}

	public static void removeCable(BlockCoords cable) {
		for (int i = 0; i < 6; i++) {
			Object adjacent = FMPHelper.checkObject(BlockCoords.translateCoords(cable, ForgeDirection.getOrientation(i)));
			if (adjacent instanceof IMultiTile) {
				IMultiTile adjTile = (IMultiTile) adjacent;
				adjTile.updateConnections();
			}
		}
	}

	public static List<BlockCoords> getConnections(TileEntity tile, ForgeDirection dir) {
		List<BlockCoords> coords = new ArrayList();
		TileEntity adjacent = getAdjacentTile(tile, dir);
		if (adjacent != null) {
			if (adjacent instanceof IMultiDataCable) {
				IMultiDataCable cable = ((IMultiDataCable) adjacent);
				coords = cable.getCoords();
			}
		}
		List<BlockCoords> connections = new ArrayList();
		for (BlockCoords coord : coords) {
			TileEntity target = coord.getTileEntity();
			if (target != null) {
				if (target instanceof TileEntityDataEmitter) {
					// don't connect
				} else if (target instanceof IDataReceiver) {
					TileEntityDataEmitter emitter = getConnectedEmitter((IDataReceiver) target, tile);
					if (emitter != null) {
						connections.addAll(getConnections(emitter, ForgeDirection.getOrientation(emitter.getBlockMetadata())));
					}
				} else if (target instanceof IMultiTile) {
					connections.add(coord);
				}
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

	/** checks connection on direction */
	public static boolean canRenderConnection(TileEntity te, ForgeDirection dir) {
		Object tile = SonarHelper.getAdjacentTileEntity(te, dir);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataTile) {
				return (((IDataTile) tile).canConnect(dir.getOpposite()));
			} else if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (!cable.isBlocked(dir.getOpposite())) {
					return true;
				}
			}
		}
		return false;
	}
}
