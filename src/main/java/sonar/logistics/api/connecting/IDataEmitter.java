package sonar.logistics.api.connecting;

import java.util.ArrayList;
import java.util.UUID;

import sonar.core.utils.IUUIDIdentity;

/**implemented on the Data Emitter*/
public interface IDataEmitter extends ILogicTile, IUUIDIdentity {
	
	/**can the given player UUID connect to this IDataEmitter*/
	public boolean canPlayerConnect(UUID uuid);
	
	/**the emitters name, as chosen by the user*/
	public String getEmitterName();
	
	/**called when this Emitter is connected to a DataReceiver*/
	public void connect(IDataReceiver receiver);

	/**called when this Emitter is disconnected to a DataReceiver*/
	public void disconnect(IDataReceiver receiver);
	
	/**a list of network IDs from all the connected networks.*/
	public ArrayList<Integer> getNetworks();

}
