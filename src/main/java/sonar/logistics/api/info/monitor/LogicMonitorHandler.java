package sonar.logistics.api.info.monitor;

import java.util.Objects;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.LogisticsASMLoader;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.connections.monitoring.MonitoredList;

public abstract class LogicMonitorHandler<I extends IMonitorInfo> {

	public abstract String id();
	
	/** @param network TODO
	 * @param coords the position to obtain the info from
	 * @return the info found */
	public abstract MonitoredList<I> updateInfo(INetworkCache network, MonitoredList<I> info, BlockCoords coords, EnumFacing side);

	public static LogicMonitorHandler instance(String id){
		return LogisticsASMLoader.monitorHandlers.get(id);
	}

	public int hashCode(){
		return Objects.hashCode(id());		
	}
	
}
