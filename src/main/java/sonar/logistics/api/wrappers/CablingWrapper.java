package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;

public class CablingWrapper {

	/** should be called on validate() method in your TileEntity class: for adding a the TileEntity to a Network
	 * @param connection TileEntity to connect
	 * @param side {@link ForgeDirection} it should connect via (cable side) */
	public void addConnection(TileEntity connection, ForgeDirection side) {}

	/** adds a connection to the given RegistryID
	 * @param registryID the registryID to connect to
	 * @param coords the coordinates of the connection */
	public void addConnection(int registryID, BlockCoords coords) {}

	/** should be called on invalidate() method in your TileEntity class: for removing a TileEntity from a Network
	 * @param connection TileEntity to remove
	 * @param side {@link ForgeDirection} it was connected via (cable side) */
	public void removeConnection(TileEntity connection, ForgeDirection side) {}

	/** removes a connection to the given RegistryID
	 * @param registryID the registryID to remove to
	 * @param coords the coordinates of the connection */
	public void removeConnection(int registryID, BlockCoords coords) {}

	/** should be called on the {@code validate()} method in your TileEntity class: for adding a {@link IDataCable} to a network
	 * @param cable {@link IDataCable} to be added */
	public void addCable(IDataCable cable) {}

	/** should be called on the {@code invalidate()} method in your TileEntity class: for removing a {@link IDataCable} from a network
	 * @param cable {@link IDataCable} to be removed */
	public void removeCable(IDataCable cable) {}
	
	/**checks all the adjacent IInfoEmitters and refreshes their connections
	 * @param cable the {@link IDataCable} */
	public void refreshConnections(IDataCable cable) {}		
	
	/** returns the {@link INetworkCache} on a given side of a tile
	 * @param tile TileEntity to check from
	 * @param dir {@link ForgeDirection} to check in
	 * @return list of {@link BlockCoords} */
	public INetworkCache getNetwork(TileEntity tile, ForgeDirection dir) {
		return new EmptyNetworkCache();
	}

	/** checks the given TileEntity for cable connections in a given direction
	 * @param te TileEntity to check from
	 * @param dir {@link ForgeDirection} to check in
	 * @param cableType the default CableType
	 * @return 0 = no connection, 1 = Data Cable connection, 2 = Channelled Cable connection */
	public CableType canRenderConnection(TileEntity te, ForgeDirection dir, CableType cableType) {
		return CableType.NONE;
	}
}
