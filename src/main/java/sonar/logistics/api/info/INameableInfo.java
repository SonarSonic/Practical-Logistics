package sonar.logistics.api.info;

import sonar.logistics.api.info.monitor.IMonitorInfo;

public interface INameableInfo<T extends IMonitorInfo> extends IMonitorInfo<T> {
	public String getClientIdentifier();

	public String getClientObject();

	public String getClientType();
}
