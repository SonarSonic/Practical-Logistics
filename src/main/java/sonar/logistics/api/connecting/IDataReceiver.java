package sonar.logistics.api.connecting;

import java.util.ArrayList;

public interface IDataReceiver extends ILogicTile {

	public ArrayList<Integer> getConnectedNetworks();
}
