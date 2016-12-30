package sonar.logistics.connections.monitoring;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.MonitorHandler;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;

@MonitorHandler(handlerID = ChannelMonitorHandler.id, modid = Logistics.MODID)
public class ChannelMonitorHandler extends LogicMonitorHandler<MonitoredBlockCoords> {

	public static final String id = "channels";
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<MonitoredBlockCoords> updateInfo(INetworkCache network, MonitoredList<MonitoredBlockCoords> info, BlockCoords coords, EnumFacing side) {
		return info;
	}

}
