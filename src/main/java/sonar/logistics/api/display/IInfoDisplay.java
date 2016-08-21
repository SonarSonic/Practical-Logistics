package sonar.logistics.api.display;

import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoUUID;

/** implemented by any Display Screen TileEntity */
public interface IInfoDisplay extends ILogicTile {	
	
	public IInfoContainer container();

	public ScreenLayout getLayout();
	
	public int maxInfo();

	public boolean monitorsUUID(InfoUUID id);
	
}
