package sonar.logistics.api.connecting;

import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.IDisplayInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class DisplayInfo implements IDisplayInfo {

	public IMonitorInfo info;
	public CustomColour textColour, backgroundColour;

	public DisplayInfo(IMonitorInfo info, CustomColour textColour, CustomColour backgroundColour) {
		this.info = info;
		this.textColour = textColour;
		this.backgroundColour = backgroundColour;
	}

	@Override
	public IMonitorInfo getStoredInfo() {
		return info;
	}

	@Override
	public CustomColour getTextColour() {
		return textColour;
	}

	@Override
	public CustomColour getBackgroundColour() {
		return backgroundColour;
	}

}
