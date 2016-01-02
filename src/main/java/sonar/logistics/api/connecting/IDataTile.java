package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
/**
 * Implemented by Tile Entities which can connect to cables.
 */
public interface IDataTile {

	/**checked by cables to see if they can connect, 
	 * this shouldn't be changed according to internal Tile data, 
	 * and should merely be used to show if the tile can connect at all on this side*/
	public boolean canConnect(ForgeDirection dir);
}
