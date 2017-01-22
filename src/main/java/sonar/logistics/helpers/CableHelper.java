package sonar.logistics.helpers;

import java.util.ArrayList;

import mcmultipart.multipart.IMultipart;
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
import sonar.core.utils.SonarValidation;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ConnectableType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.connecting.INetworkConnectable;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ILargeDisplay;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.common.multiparts.DataCablePart;

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

	public ILogicTile getMultipart(BlockCoords coords, EnumFacing face) {
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(face));
			if (part instanceof ILogicTile) {
				return (ILogicTile) part;
			}
		}
		return null;
	}

	public IInfoDisplay getDisplayScreen(BlockCoords coords, EnumFacing face) {		
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			return getDisplayScreen(container, face);
		}
		return null;
	}

	public ILargeDisplay getDisplayScreen(BlockCoords coords, int registryID) {
		IMultipartContainer container = MultipartHelper.getPartContainer(coords.getWorld(), coords.getBlockPos());
		if (container != null) {
			return getDisplayScreen(container, registryID);
		}
		return null;
	}

	public IInfoDisplay getDisplayScreen(IMultipartContainer container, EnumFacing face) {
		for (IMultipart part : container.getParts()) {
			if (part != null && part instanceof IInfoDisplay) {
				IInfoDisplay display = (IInfoDisplay) part;
				if (display.getFace() == face) {
					return display;
				}
			}
		}
		return null;
	}

	public ILargeDisplay getDisplayScreen(IMultipartContainer container, int registryID) {
		for (IMultipart part : container.getParts()) {
			if (part != null && part instanceof ILargeDisplay) {
				ILargeDisplay display = (ILargeDisplay) part;
				if (display.getRegistryID() == registryID) {
					return display;
				}
			}
		}
		return null;
	}

	public static ArrayList<ILogicTile> getConnectedTiles(DataCablePart cable) {
		return getConnectedTiles(cable, new SonarValidation.CLASS(ILogicTile.class));
	}

	public static <T> ArrayList<T> getConnectedTiles(DataCablePart cable, Class<T> type) {
		return getConnectedTiles(cable, new SonarValidation.CLASS(type));
	}

	public static <T> ArrayList<T> getConnectedTiles(DataCablePart cable, SonarValidation validate) {
		ArrayList<T> logicTiles = new ArrayList();
		for (IMultipart part : cable.getContainer().getParts()) {
			if (validate.isValid(part)) {
				logicTiles.add((T) part);
			}
		}
		for (EnumFacing face : EnumFacing.values()) {
			BlockCoords offset = BlockCoords.translateCoords(cable.getCoords(), face.getOpposite());
			ILogicTile tile = LogisticsAPI.getCableHelper().getMultipart(offset, face);
			if (validate.isValid(tile) && tile.canConnect(face).canConnect()) {
				logicTiles.add((T) tile);
			}
		}
		return logicTiles;
	}

	public static ArrayList<ILogicMonitor> getLocalMonitors(DataCablePart cable) {
		ArrayList<ILogicMonitor> logicTiles = new ArrayList();
		for (EnumFacing face : EnumFacing.values()) {
			BlockCoords offset = BlockCoords.translateCoords(cable.getCoords(), face.getOpposite());
			ILogicTile tile = LogisticsAPI.getCableHelper().getMultipart(offset, face);
			if (tile instanceof ILogicMonitor) {
				logicTiles.add((ILogicMonitor) tile);
			}
		}
		return logicTiles;
	}

	public INetworkCache getNetwork(TileEntity tile, EnumFacing dir) {
		// watch out for this null :P
		Pair<ConnectableType, Integer> connection = Logistics.getDisplayManager().getConnectionType(null, tile.getWorld(), tile.getPos(), dir, ConnectableType.CONNECTION);
		if (connection.a != ConnectableType.NONE && connection.b != -1) {
			INetworkCache cache = Logistics.instance.networkManager.getNetwork(connection.b);
			if (cache != null) {
				return cache;
			}
		}
		return EmptyNetworkCache.INSTANCE;
	}

	public INetworkCache getNetwork(int registryID) {
		return Logistics.instance.networkManager.getNetwork(registryID);
	}

	public static ILogicMonitor getMonitorFromHashCode(int hashCode, boolean isRemote) {
		for (ILogicMonitor monitor : Logistics.getInfoManager(isRemote).getMonitors().values()) {
			if (monitor.getIdentity().hashCode() == hashCode) {
				return monitor;
			}
		}
		return null;
	}
	

	public static  <T extends INetworkConnectable> Pair<ConnectableType, Integer> getConnectionType(T source, World world, BlockPos pos, EnumFacing dir, ConnectableType cableType) {
		BlockPos offset = pos.offset(dir);
		IMultipartContainer container = MultipartHelper.getPartContainer(world, offset);
		if (container != null) {
			return getConnectionType(source, container, dir, cableType);
		} else {
			TileEntity tile = world.getTileEntity(offset);
			if (tile != null) {
				return getConnectionTypeFromObject(source, tile, dir, cableType);
			}
		}
		return new Pair(ConnectableType.NONE, -1);
	}

	/** checks what cable type can be connected via a certain direction, assumes the other block can connect from this side */
	public static  <T extends INetworkConnectable> Pair<ConnectableType, Integer> getConnectionType(T source, IMultipartContainer container, EnumFacing dir, ConnectableType cableType) {
		ISlottedPart part = container.getPartInSlot(PartSlot.getFaceSlot(dir.getOpposite()));
		if (part != null) {
			return getConnectionTypeFromObject(source, part, dir, cableType);
		} else {
			ISlottedPart centre = container.getPartInSlot(PartSlot.CENTER);
			if (centre != null && centre instanceof IDataCable) {
				return getConnectionTypeFromObject(source, centre, dir, cableType);
			}
		}
		return new Pair(ConnectableType.NONE, -1);
	}

	public static <T extends INetworkConnectable> Pair<ConnectableType, Integer> getConnectionTypeFromObject(T source, Object connection, EnumFacing dir, ConnectableType cableType) {
		if (connection instanceof IDataCable) {
			IDataCable cable = (IDataCable) connection;
			if (cable.getCableType().canConnect(cableType)) {
				return cable.canConnectOnSide(dir.getOpposite()) ? new Pair(cable.getCableType(), cable.getRegistryID()) : new Pair(ConnectableType.NONE, -1);
			}
		} else if (connection instanceof ILogicTile) {
			return ((ILogicTile) connection).canConnect(dir.getOpposite()).canShowConnection() ? new Pair(ConnectableType.BLOCK_CONNECTION, -1) : new Pair(ConnectableType.NONE, -1);
		}
		return new Pair(ConnectableType.NONE, -1);
	}
}
