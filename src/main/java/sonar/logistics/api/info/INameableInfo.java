package sonar.logistics.api.info;

import sonar.logistics.api.info.monitor.IMonitorInfo;

/**implemented on info which can be rendered in a list in the Info Reader*/
public interface INameableInfo<T extends IMonitorInfo> extends IMonitorInfo<T> {
	
	/**the objects identifier (translated)*/
	public String getClientIdentifier();

	/**the object itself*/
	public String getClientObject();

	/**the object type*/
	public String getClientType();
}
