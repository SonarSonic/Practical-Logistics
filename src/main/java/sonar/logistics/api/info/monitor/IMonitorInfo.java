package sonar.logistics.api.info.monitor;

import sonar.core.api.nbt.INBTSyncable;

public interface IMonitorInfo<T extends IMonitorInfo> extends INBTSyncable{
	
	public String getID();
	
	/** if they are identical **/
	public boolean isIdenticalInfo(T info);

	/** if they are of the same type with just different values **/
	public boolean isMatchingInfo(T info);

	/** if they are of the same type with just different values **/
	public boolean isMatchingType(IMonitorInfo info);

	public boolean isHeader();
	
	public MonitorHandler<T> getHandler();
	
	public boolean isValid();
	
}
