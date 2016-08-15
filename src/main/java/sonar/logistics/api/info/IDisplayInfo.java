package sonar.logistics.api.info;

import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public interface IDisplayInfo {

	public IMonitorInfo getStoredInfo();
	
	public CustomColour getTextColour();
	
	public CustomColour getBackgroundColour();
	
}
