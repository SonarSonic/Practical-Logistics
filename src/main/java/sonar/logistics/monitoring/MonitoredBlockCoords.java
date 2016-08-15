package sonar.logistics.monitoring;

import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.FontHelper;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.CacheRegistry;

public class MonitoredBlockCoords implements IMonitorInfo<MonitoredBlockCoords>, INameableInfo<MonitoredBlockCoords> {

	public BlockCoords coords;
	public String unlocalizedName;

	public MonitoredBlockCoords(BlockCoords coords, String unlocalizedName) {
		this.coords = coords;
		this.unlocalizedName = unlocalizedName;
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
	public void updateFrom(MonitoredBlockCoords info) {
		coords = info.coords;
	}

	@Override
	public boolean isHeader() {
		return false;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return CacheRegistry.handler.validateInfo(info);
	}

	@Override
	public String getClientIdentifier() {
		return FontHelper.translate(unlocalizedName + ".name");
	}

	@Override
	public String getClientObject() {
		return coords.toString();
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

}
