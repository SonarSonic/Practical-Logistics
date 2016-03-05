package sonar.logistics.api.connecting;

import sonar.logistics.api.ExternalCoords;

/**implemented to blocks which can bypass the standard channel system and reference and other channel in the system when connected. used by Data Receiver and Channel Selector**/
public interface IChannelProvider extends IInfoEmitter {

	/**the connected channel, this could be null if one hasn't been selected*/
	public ExternalCoords getChannel();
}
