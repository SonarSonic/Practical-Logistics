package sonar.logistics.api.connecting;

import java.util.UUID;

/**implemented on the Data Emitter*/
public interface IDataEmitter extends ILogicTile {
	
	/**can the given player UUID connect to this IDataEmitter*/
	public boolean canPlayerConnect(UUID uuid);

}
