package sonar.logistics.api.wrappers;

import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;

public class CablingWrapper {

	public IDataCable getCableFromCoords(BlockCoords coords) {
		return null;
	}
	
	/** should be called on the {@code validate()} method in your TileEntity class: for adding a {@link IDataCable} to a network
	 * @param cable {@link IDataCable} to be added 
	 * @return */
	public INetworkCache addCable(IDataCable cable) {
		return EmptyNetworkCache.INSTANCE;
	}

	/** should be called on the {@code invalidate()} method in your TileEntity class: for removing a {@link IDataCable} from a network
	 * @param cable {@link IDataCable} to be removed */
	public void removeCable(IDataCable cable) {}
	
	/**checks all the adjacent IInfoEmitters and refreshes their connections
	 * @param cable the {@link IDataCable} */
	public void refreshConnections(IDataCable cable) {}		
	
	/** returns the {@link INetworkCache} on a given side of a tile
	 * @param tile TileEntity to check from
	 * @param dir {@link EnumFacing} to check in
	 * @return list of {@link BlockCoords} */
	public INetworkCache getNetwork(TileEntity tile, EnumFacing dir) {
		return EmptyNetworkCache.INSTANCE;
	}

	public INetworkCache getNetwork(int registryID) {
		return EmptyNetworkCache.INSTANCE;
	}
	
	public Pair<CableType, Integer> getConnectionType(TileEntity tile, EnumFacing dir, CableType cableType) {
		return this.getConnectionType(tile.getWorld(), tile.getPos(), dir, cableType);
	}
	
	public Pair<CableType, Integer> getConnectionType(BlockCoords coords, EnumFacing dir, CableType cableType) {
		return this.getConnectionType(coords.getWorld(), coords.getBlockPos(), dir, cableType);
	}
	
	/** checks the given IMultipartContainer for cable connections in a given direction
	 * @param world the world
	 * @param pos the position of the block you are checking from (NOT THE BLOCK YOU'RE CHECKING)
	 * @param dir {@link EnumFacing} to check in
	 * @param cableType the default CableType
	 * @return the CableType */
	public Pair<CableType, Integer> getConnectionType(World world, BlockPos pos, EnumFacing dir, CableType cableType) {
		return new Pair(CableType.NONE, -1);
	}

	/** checks the given IMultipartContainer for cable connections in a given direction
	 * @param te Object to check from
	 * @param dir {@link EnumFacing} to check in
	 * @param cableType the default CableType
	 * @return the CableType */
	public Pair<CableType, Integer> getConnectionType(IMultipartContainer container, EnumFacing dir, CableType cableType) {
		return new Pair(CableType.NONE, -1);
	}
	
	/** checks the given TileEntity for cable connections in a given direction
	 * @param te Object to check from
	 * @param dir {@link EnumFacing} to check in
	 * @param cableType the default CableType
	 * @return the CableType */
	public Pair<CableType, Integer> getConnectionTypeFromObject(Object connection, EnumFacing dir, CableType cableType) {
		return new Pair(CableType.NONE, -1);
	}
}
