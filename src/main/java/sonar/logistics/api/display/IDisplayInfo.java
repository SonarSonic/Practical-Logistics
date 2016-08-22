package sonar.logistics.api.display;

import javax.annotation.Nullable;

import sonar.core.api.nbt.INBTSyncable;
import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.IMonitorInfo;

/** used within a IInfoContainer */
public interface IDisplayInfo extends INBTSyncable {

	public void setUUID(InfoUUID infoUUID);

	@Nullable
	public IMonitorInfo getCachedInfo();

	@Nullable
	public InfoUUID getInfoUUID();

	public CustomColour getTextColour();

	public CustomColour getBackgroundColour();

}
