package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

/** implemented by Tile Enties which can connect to Multi Data Cables */
public interface IMultiTile {

	/**this tile entities's coordinates, for convenience and prevent the need to find the FMP Tile**/
	public BlockCoords getCoords();

	/**can the Tile connect to cables on the given direction*/
	public boolean canConnect(ForgeDirection dir);
}
