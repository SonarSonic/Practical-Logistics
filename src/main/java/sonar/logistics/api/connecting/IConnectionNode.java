package sonar.logistics.api.connecting;

import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

/**implemented by Nodes, provides a list of all the blocks they are connected to, normally just one, but can be more*/
public interface IConnectionNode extends ILogicTile {

	public void addConnections(Map<BlockCoords, ForgeDirection> connections);
}
