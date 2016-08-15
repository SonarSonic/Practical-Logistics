package sonar.logistics.api.connecting;

import sonar.logistics.api.info.IInfoContainer;

/** implemented by any Display Screen TileEntity */
public interface IInfoScreen extends ILogicTile {

	public ScreenLayout getScreenLayout();
	
	public IInfoContainer getContainer();

	public static enum ScreenLayout {
		ONE, DUAL, GRID, all;
	}
}
