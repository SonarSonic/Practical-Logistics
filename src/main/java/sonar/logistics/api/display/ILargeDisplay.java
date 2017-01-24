package sonar.logistics.api.display;

import sonar.logistics.api.connecting.INetworkConnectable;

/**implemented on Large Display Screen*/
public interface ILargeDisplay extends IInfoDisplay,INetworkConnectable {

	/**sets the {@link ConnectedDisplayScreen} this {@link ILargeDisplay} is connected to*/
	public void setConnectedDisplay(ConnectedDisplayScreen connectedDisplay);
	
	/**gets the {@link ConnectedDisplayScreen} this {@link ILargeDisplay} is connected to*/
	public ConnectedDisplayScreen getDisplayScreen();
	
	/**sets if this {@link ILargeDisplay} should be responsible for rendering the data from the {@link ConnectedDisplayScreen}*/
	public void setShouldRender(boolean shouldRender);
	
	/**if this {@link ILargeDisplay} should render the info from the {@link ConnectedDisplayScreen}*/
	public boolean shouldRender();
}
