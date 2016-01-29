package sonar.logistics.api.connecting;

import java.util.List;

import sonar.core.utils.BlockCoords;

/**current Interface for large display screens, will be changed in the very near future*/
public interface ILargeDisplay {

	public boolean isHandler();

	public List<BlockCoords> getConnectedScreens();

	public void setConnectedScreens(List<BlockCoords> list);

	public void addDisplay(BlockCoords coords);

	public void removeDisplay(BlockCoords coords);

	public BlockCoords getHandlerCoords();

	public void setHandlerCoords(BlockCoords coords);

	public void setHandler(boolean isHandler);
	
	public void resetSizing();

}
