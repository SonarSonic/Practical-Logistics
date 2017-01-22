package sonar.logistics.api.display;

import net.minecraft.util.EnumFacing;
import sonar.core.network.sync.ISyncableListener;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.monitor.ILogicViewable;

/** implemented by any Display Screen TileEntity */
public interface IInfoDisplay extends ILogicTile, ILogicViewable, ISyncableListener {	
	
	/**the IInfoContainer holding all the current Display Info*/
	public IInfoContainer container();

	/**the current screen layout*/
	public ScreenLayout getLayout();
	
	/**the current Display Type*/
	public DisplayType getDisplayType();
	
	/**the maximum amount of info which can be displayed on the screen at a time*/
	public int maxInfo();
	
	/**which face the Display is facing, used for checking if LargeDisplayScreens can connect*/
	public EnumFacing getFace();
	
}
