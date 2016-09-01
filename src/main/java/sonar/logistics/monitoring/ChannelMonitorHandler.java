package sonar.logistics.monitoring;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;

public class ChannelMonitorHandler extends MonitorHandler<MonitoredBlockCoords> {

	@Override
	public String getName() {
		return MonitorHandler.CHANNEL;
	}

	@Override
	public MonitoredList<MonitoredBlockCoords> updateInfo(MonitoredList<MonitoredBlockCoords> info, BlockCoords coords, EnumFacing side) {
		return info;
	}

}
