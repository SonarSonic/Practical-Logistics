package sonar.logistics.monitoring;

import com.google.common.collect.Lists;

import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.SyncCoords;
import sonar.core.network.sync.SyncTagType;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.info.BaseInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;

@LogicInfoType(id = MonitoredBlockCoords.id, modid = Logistics.MODID)
public class MonitoredBlockCoords extends BaseInfo<MonitoredBlockCoords> implements INameableInfo<MonitoredBlockCoords> {

	public static final String id = "coords";
	public static MonitorHandler<MonitoredBlockCoords> handler = Logistics.monitorHandlers.getRegisteredObject(MonitorHandler.CHANNEL);
	public SyncCoords coords = new SyncCoords(1);
	public SyncTagType.STRING unlocalizedName = new SyncTagType.STRING(2);

	{
		syncParts.addAll(Lists.newArrayList(coords, unlocalizedName));
	}

	public MonitoredBlockCoords(BlockCoords coords, String unlocalizedName) {
		this.coords.setCoords(coords);
		this.unlocalizedName.setObject(unlocalizedName);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredBlockCoords info) {
		return info.coords.equals(coords);
	}

	@Override
	public boolean isMatchingInfo(MonitoredBlockCoords info) {
		return true;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof MonitoredBlockCoords;
	}

	@Override
	public String getClientIdentifier() {
		return FontHelper.translate(unlocalizedName + ".name");
	}

	@Override
	public String getClientObject() {
		return coords.getCoords().toString();
	}

	@Override
	public String getClientType() {
		return "position";
	}

	public boolean equals(Object obj) {
		if (obj instanceof MonitoredBlockCoords) {
			MonitoredBlockCoords monitoredCoords = (MonitoredBlockCoords) obj;
			return monitoredCoords.coords.equals(coords) && monitoredCoords.unlocalizedName.equals(unlocalizedName);
		}
		return false;
	}

	@Override
	public MonitorHandler<MonitoredBlockCoords> getHandler() {
		return handler;
	}

	@Override
	public boolean isValid() {
		return coords.getCoords()!=null;
	}

	@Override
	public String getID() {
		return id;
	}

}
