package sonar.logistics.api.display;

import sonar.logistics.api.connecting.INetworkConnectable;

public interface ILargeDisplay extends IInfoDisplay,INetworkConnectable {

	public void setConnectedDisplay(ConnectedDisplayScreen connectedDisplay);
	
	public ConnectedDisplayScreen getDisplayScreen();
	
	public void setShouldRender(boolean shouldRender);
	
	public boolean shouldRender();
}
