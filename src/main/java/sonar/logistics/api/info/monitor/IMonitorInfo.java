package sonar.logistics.api.info.monitor;

import sonar.core.api.nbt.INBTSyncable;
import sonar.logistics.api.asm.LogicInfoType;

/**for your info to be registered you must use {@link LogicInfoType} implement this for all types of info*/
public interface IMonitorInfo<T extends IMonitorInfo> extends INBTSyncable{
	
	/**this must be the same as the ID specified in {@link LogicInfoType}*/
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
