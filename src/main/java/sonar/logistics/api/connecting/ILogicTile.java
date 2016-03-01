package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.cache.ICacheable;

/** implemented by Tile Enties which can connect to Data Cables */
public interface ILogicTile extends ICacheable {

	/**can the Tile connect to cables on the given direction*/
	public boolean canConnect(ForgeDirection dir);
}
