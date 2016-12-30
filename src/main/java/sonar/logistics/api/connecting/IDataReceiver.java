package sonar.logistics.api.connecting;

import java.util.ArrayList;

/**implemented on the Data Receiver*/
public interface IDataReceiver extends ILogicTile {

	/**gets the network ID of all currently connected networks*/
	public ArrayList<Integer> getConnectedNetworks();
	
	public void refreshConnectedNetworks();
}
