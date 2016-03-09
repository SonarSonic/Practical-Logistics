package sonar.logistics.api.connecting;

import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

/** implemented by Nodes, provides a list of all the blocks they are connected to, normally just one, but can be more */
public interface IConnectionNode extends ILogicTile {

	/** adds any available connections to the current Map
	 * @param connections the current list of Entries */
	public void addConnections(Map<BlockCoords, ForgeDirection> connections);
}
