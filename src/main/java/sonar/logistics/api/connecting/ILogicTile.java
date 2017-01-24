package sonar.logistics.api.connecting;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.IWorldPosition;
import sonar.logistics.api.cache.INetworkCache;

/** implemented by Tile Entities which can connect to Data Cables */
public interface ILogicTile extends IWorldPosition {

	public static enum ConnectionType{
		VISUAL,
		NETWORK,
		NONE;
		
		public boolean canConnect(){
			return this==NETWORK;
		}
		
		public boolean canShowConnection(){
			return this==VISUAL || canConnect();
		}
	}
	
	/** can the Tile connect to cables on the given direction */
	public ConnectionType canConnect(EnumFacing dir);

	/** the {@link BlockCoords} this Block/FMP Part should be registered as on the Network
	 * @return the {@link BlockCoords} */
	public BlockCoords getCoords();
	
	/**gets the network cache's ID*/
	public int getNetworkID();
	
	/**sets the network this tile is connected to*/
	public void setLocalNetworkCache(INetworkCache network);
}
