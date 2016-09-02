package sonar.logistics.monitoring;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.MonitorHandler;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.connections.MonitoredList;

@MonitorHandler(handlerID = ChannelMonitorHandler.id, modid = Logistics.MODID)
public class ChannelMonitorHandler extends LogicMonitorHandler<MonitoredBlockCoords> {

	public static final String id = "channels";
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<MonitoredBlockCoords> updateInfo(MonitoredList<MonitoredBlockCoords> info, BlockCoords coords, EnumFacing side) {
		return info;
	}

}
