package sonar.logistics.api.info;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.monitoring.MonitoredList;

public interface ITileMonitorHandler<I extends IMonitorInfo> {

	public MonitoredList<I> updateInfo(INetworkCache network, MonitoredList<I> previousList, BlockCoords coords, EnumFacing side);
	
	public String id();
}
