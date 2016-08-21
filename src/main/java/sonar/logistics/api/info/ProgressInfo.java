package sonar.logistics.api.info;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;

@LogicInfoType(id = ProgressInfo.id, modid = Logistics.MODID)
public class ProgressInfo implements IMonitorInfo<ProgressInfo>, INBTSyncable, INameableInfo<ProgressInfo> {

	public static final String id = "progress";
	public LogicInfo first, second;

	public ProgressInfo(LogicInfo first, LogicInfo second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public String getClientIdentifier() {
		return "Progress Info";
	}

	@Override
	public String getClientObject() {
		return "HOY";
	}

	@Override
	public String getClientType() {
		return null;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {

	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		return null;
	}

	@Override
	public boolean isIdenticalInfo(ProgressInfo info) {
		return false;
	}

	@Override
	public boolean isMatchingInfo(ProgressInfo info) {
		return false;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return false;
	}

	@Override
	public boolean isHeader() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public MonitorHandler<ProgressInfo> getHandler() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public String getID() {
		return id;
	}

}
