package sonar.logistics.api.connecting;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.cache.INetworkCache;

/** implemented by Tile Entities which can connect to Data Cables */
public interface ILogicTile {

	/** can the Tile connect to cables on the given direction */
	public boolean canConnect(EnumFacing dir);

	/** the {@link BlockCoords} this Block/FMP Part should be registered as on the Network
	 * @return the {@link BlockCoords} */
	public BlockCoords getCoords();
}
