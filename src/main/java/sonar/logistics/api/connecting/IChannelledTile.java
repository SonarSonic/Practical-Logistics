package sonar.logistics.api.connecting;

import sonar.logistics.api.info.monitor.ILogicViewable;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;

/**a tile which has channels which can be configured by the operator*/
public interface IChannelledTile extends ILogicViewable {

	/**the currently selected channels*/
	public IdentifiedCoordsList getChannels(int channelID);
	
	/**call this client side only, sends the selected coords to the server
	 * @param channelID the id to modify the coords on*/
	public void modifyCoords(MonitoredBlockCoords coords, int channelID);
}
