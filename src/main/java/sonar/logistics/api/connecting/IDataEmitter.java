package sonar.logistics.api.connecting;

import java.util.UUID;

public interface IDataEmitter extends ILogicTile {

	public int getNetworkID();
	
	public boolean canPlayerConnect(UUID uuid);

}
