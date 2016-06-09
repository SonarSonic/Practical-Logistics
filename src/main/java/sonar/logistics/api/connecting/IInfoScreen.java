package sonar.logistics.api.connecting;

import sonar.logistics.api.info.ILogicInfo;

/** implemented by any Display Screen TileEntity */
public interface IInfoScreen extends ILogicTile {

	/** all the info currently being displayed, entries can be null */
	public ILogicInfo[] getDisplayInfo();

	public ScreenLayout getScreenLayout();

	public static enum ScreenLayout {
		ONE, DUAL, GRID, all;
	}
}
