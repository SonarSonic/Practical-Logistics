package sonar.logistics.api.info.monitor;

import java.util.Objects;

import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.LogisticsASMLoader;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.connections.monitoring.MonitoredList;

public abstract class LogicMonitorHandler<I extends IMonitorInfo> {

	public abstract String id();

	public static LogicMonitorHandler instance(String id) {
		LogicMonitorHandler tileHandler = (LogicMonitorHandler) LogisticsASMLoader.tileMonitorHandlers.get(id);
		return tileHandler == null ? (LogicMonitorHandler) LogisticsASMLoader.entityMonitorHandlers.get(id) : tileHandler;
	}

	public int hashCode() {
		return Objects.hashCode(id());
	}

}
