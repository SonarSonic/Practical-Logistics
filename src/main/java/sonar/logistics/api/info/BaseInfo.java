package sonar.logistics.api.info;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.logistics.api.info.monitor.IMonitorInfo;

/** typical implementation of IMonitorInfo which has a sync parts list for all the Info things it also has the required constructor which required empty constructor */
public abstract class BaseInfo<T extends IMonitorInfo> implements IMonitorInfo<T> {

	protected ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();

	public BaseInfo() {}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, syncParts, type.isType(SyncType.SAVE));
		return nbt;
	}

	@Override
	public boolean isHeader() {
		return false;
	}

	public boolean equals(Object object) {
		if (object != null && object instanceof IMonitorInfo) {
			IMonitorInfo info = (IMonitorInfo) object;
			return (info.isHeader() && isHeader()) || (this.isMatchingType(info) && isIdenticalInfo((T) info));
		}
		return false;
	}
}
