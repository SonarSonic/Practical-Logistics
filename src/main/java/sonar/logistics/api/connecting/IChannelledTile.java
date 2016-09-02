package sonar.logistics.api.connecting;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.ILogicViewable;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.monitoring.MonitoredBlockCoords;

/**a tile which has channels which can be configured by the operator*/
public interface IChannelledTile extends ILogicViewable {

	/**the currently selected channels*/
	public IdentifiedCoordsList getChannels();
	
	/**call this client side only, sends the selected coords to the server*/
	public void modifyCoords(MonitoredBlockCoords coords);
}
