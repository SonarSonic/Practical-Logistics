package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;

/**implemented by Tiles which can provide info to other tiles, normally connected via cables*/
public interface IDataConnection extends IDataTile {		
		
	/**the current data for use with Screens, this may be changed to a universal piece of data at some point*/
	public Info currentInfo();
}
