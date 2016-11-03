package sonar.logistics.api.connecting;

import java.util.ArrayList;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncCoords;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.utils.IUUIDIdentity;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.network.SyncMonitoredType;

public class ClientDataEmitter implements IUUIDIdentity, INBTSyncable {

	public ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();
	public SyncUUID uuid = new SyncUUID(0);
	public SyncCoords coords = new SyncCoords(1);
	public SyncTagType.STRING name = new SyncTagType.STRING(2);
	{
		syncParts.addAll(Lists.newArrayList(uuid, coords, name));
	}

	public ClientDataEmitter() {}

	public ClientDataEmitter(IDataEmitter emitter) {
		this.uuid.setObject(emitter.getIdentity());
		this.coords.setCoords(emitter.getCoords());
		this.name.setObject(emitter.getEmitterName());
	}

	@Override
	public UUID getIdentity() {
		return uuid.getUUID();
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

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ClientDataEmitter) {
			return getIdentity().equals(((IUUIDIdentity) obj).getIdentity()) && coords.getCoords().equals(((ClientDataEmitter) obj).coords.getCoords());
		}
		return false;
	}

	public int hashCode() {
		return getIdentity().hashCode();
	}

}
