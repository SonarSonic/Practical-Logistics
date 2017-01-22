package sonar.logistics.api.info.monitor;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.info.InfoContainer;

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
	
	public LogicMonitorHandler<T> getHandler();
	
	public boolean isValid();
	
	public T copy();
	
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos);
	
	public void identifyChanges(T newInfo);
}
