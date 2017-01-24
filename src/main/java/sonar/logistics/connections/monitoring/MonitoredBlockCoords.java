package sonar.logistics.connections.monitoring;

import sonar.core.api.utils.BlockCoords;
import sonar.core.network.sync.SyncCoords;
import sonar.core.network.sync.SyncTagType;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.api.info.types.BaseInfo;
import sonar.logistics.helpers.InfoRenderer;

@LogicInfoType(id = MonitoredBlockCoords.id, modid = Logistics.MODID)
public class MonitoredBlockCoords extends BaseInfo<MonitoredBlockCoords> implements INameableInfo<MonitoredBlockCoords> {

	public static final String id = "coords";
	public static LogicMonitorHandler<MonitoredBlockCoords> handler = LogicMonitorHandler.instance(ChannelMonitorHandler.id);
	public SyncCoords syncCoords = new SyncCoords(1);
	public SyncTagType.STRING unlocalizedName = new SyncTagType.STRING(2);
	{
		syncParts.addParts(syncCoords, unlocalizedName);
	}

	public MonitoredBlockCoords() {}

	public MonitoredBlockCoords(BlockCoords coords, String unlocalizedName) {
		this.syncCoords.setCoords(coords);
		this.unlocalizedName.setObject(unlocalizedName);
	}

	@Override
	public boolean isIdenticalInfo(MonitoredBlockCoords info) {
		return info.syncCoords.equals(syncCoords);
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
		return unlocalizedName.getObject();
	}


	@Override
	public String getClientObject() {		
		return syncCoords.getCoords().toString();
	}

	@Override
	public String getClientType() {
		return "position";
	}

	public boolean equals(Object obj) {
		if (obj instanceof MonitoredBlockCoords) {
			MonitoredBlockCoords monitoredCoords = (MonitoredBlockCoords) obj;
			return monitoredCoords.syncCoords.equals(syncCoords) && monitoredCoords.unlocalizedName.equals(unlocalizedName);
		}
		return false;
	}

	@Override
	public LogicMonitorHandler<MonitoredBlockCoords> getHandler() {
		return handler;
	}

	@Override
	public boolean isValid() {
		return syncCoords.getCoords() != null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public MonitoredBlockCoords copy() {
		return new MonitoredBlockCoords(syncCoords.getCoords(), unlocalizedName.getObject());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		InfoRenderer.renderNormalInfo(container.display.getDisplayType(), width, height, scale, getClientIdentifier(), getClientObject());
	}
}