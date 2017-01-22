package sonar.logistics.api.wrappers;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ILargeDisplay;

public class CablingWrapper {

	public IDataCable getCableFromCoords(BlockCoords coords) {
		return null;
	}

	public ILogicTile getMultipart(BlockCoords coords, EnumFacing face) {
		return null;
	}

	public IInfoDisplay getDisplayScreen(BlockCoords coords, EnumFacing face) {
		return null;
	}
	
	public ILargeDisplay getDisplayScreen(BlockCoords coords, int registryID) {
		return null;
	}

	/** should be called on the {@code validate()} method in your TileEntity class: for adding a {@link IDataCable} to a network
	 * 
	 * @param cable {@link IDataCable} to be added
	 * @return */
	//public INetworkCache addCable(IDataCable cable) {
	///	return EmptyNetworkCache.INSTANCE;
	//}

	/** should be called on the {@code invalidate()} method in your TileEntity class: for removing a {@link IDataCable} from a network
	 * 
	 * @param cable {@link IDataCable} to be removed */
	//public void removeCable(IDataCable cable) {
	//}

	/** checks all the adjacent IInfoEmitters and refreshes their connections
	 * 
	 * @param cable the {@link IDataCable} */
	//public void refreshConnections(IDataCable cable) {
	//}

	/** returns the {@link INetworkCache} on a given side of a tile
	 * 
	 * @param tile TileEntity to check from
	 * @param dir {@link EnumFacing} to check in
	 * @return list of {@link BlockCoords} */
	public INetworkCache getNetwork(TileEntity tile, EnumFacing dir) {
		return EmptyNetworkCache.INSTANCE;
	}

	public INetworkCache getNetwork(int registryID) {
		return EmptyNetworkCache.INSTANCE;
	}
	/*
	public Pair<ConnectableType, Integer> getConnectionType(TileEntity tile, EnumFacing dir, ConnectableType cableType) {
		return this.getConnectionType(tile.getWorld(), tile.getPos(), dir, cableType);
	}

	public Pair<ConnectableType, Integer> getConnectionType(BlockCoords coords, EnumFacing dir, ConnectableType cableType) {
		return this.getConnectionType(coords.getWorld(), coords.getBlockPos(), dir, cableType);
	}

	/** checks the given IMultipartContainer for cable connections in a given direction
	 * 
	 * @param world the world
	 * @param pos the position of the block you are checking from (NOT THE BLOCK YOU'RE CHECKING)
	 * @param dir {@link EnumFacing} to check in
	 * @param cableType the default CableType
	 * @return the CableType */
	/*
	public Pair<ConnectableType, Integer> getConnectionType(World world, BlockPos pos, EnumFacing dir, ConnectableType cableType) {
		return new Pair(ConnectableType.NONE, -1);
	}

	/** checks the given IMultipartContainer for cable connections in a given direction
	 * 
	 * @param te Object to check from
	 * @param dir {@link EnumFacing} to check in
	 * @param cableType the default CableType
	 * @return the CableType */
	/*
	public Pair<ConnectableType, Integer> getConnectionType(IMultipartContainer container, EnumFacing dir, ConnectableType cableType) {
		return new Pair(ConnectableType.NONE, -1);
	}

	/** checks the given TileEntity for cable connections in a given direction
	 * 
	 * @param te Object to check from
	 * @param dir {@link EnumFacing} to check in
	 * @param cableType the default CableType
	 * @return the CableType */
	/*
	public Pair<ConnectableType, Integer> getConnectionTypeFromObject(Object connection, EnumFacing dir, ConnectableType cableType) {
		return new Pair(ConnectableType.NONE, -1);
	}
	*/
}
