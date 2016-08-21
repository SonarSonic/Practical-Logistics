package sonar.logistics.api.info;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public abstract class BaseInfo<T extends IMonitorInfo> implements IMonitorInfo<T> {

	protected ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();

	public BaseInfo() {}

	public BaseInfo(NBTTagCompound tag) {
		this.readData(tag, SyncType.SAVE);
	}

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
}
