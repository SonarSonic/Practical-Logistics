package sonar.logistics.api.info;

import io.netty.buffer.ByteBuf;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public interface IDisplayInfo {

	public IMonitorInfo getCachedInfo();

	public CustomColour getTextColour();

	public CustomColour getBackgroundColour();

	public InfoUUID getInfoUUID();
	
	public void updateInfo(ByteBuf buf);

}
