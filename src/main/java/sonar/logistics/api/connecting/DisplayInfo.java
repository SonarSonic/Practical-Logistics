package sonar.logistics.api.connecting;

import io.netty.buffer.ByteBuf;
import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.IDisplayInfo;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class DisplayInfo implements IDisplayInfo {

	public IMonitorInfo info;
	public CustomColour textColour, backgroundColour;
	public InfoUUID id = new InfoUUID(-1, -1);

	public DisplayInfo(CustomColour textColour, CustomColour backgroundColour) {
		this.textColour = textColour;
		this.backgroundColour = backgroundColour;
	}

	public void setInfo(IMonitorInfo info, InfoUUID id) {
		this.info = info;
		this.id = id;
	}

	@Override
	public IMonitorInfo getCachedInfo() {
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

	@Override
	public InfoUUID getInfoUUID() {
		return null;
	}

	@Override
	public void updateInfo(ByteBuf buf) {

	}

}
