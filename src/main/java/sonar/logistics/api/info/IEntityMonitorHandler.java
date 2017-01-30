package sonar.logistics.api.info;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.monitoring.MonitoredList;

public interface IEntityMonitorHandler<I extends IMonitorInfo> {

	public MonitoredList<I> updateInfo(INetworkCache network, MonitoredList<I> previousList, Entity entity);

	public String id();
}
