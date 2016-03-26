package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;

/** implemented by Tile Entities which can connect to Data Cables */
public interface ILogicTile {

	/** can the Tile connect to cables on the given direction */
	public boolean canConnect(ForgeDirection dir);

	/** the {@link BlockCoords} this Block/FMP Part should be registered as on the Network
	 * @return the {@link BlockCoords} */
	public BlockCoords getCoords();
}
