package sonar.logistics.api.cache;

import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;

/**implemented on {@link INetworkCache}s which can provide Items or Fluids*/
public interface IMonitorCache extends INetworkCache {

	public <T extends IMonitorInfo> void addMonitor(ILogicMonitor<T> monitor);

	public <T extends IMonitorInfo> void removeMonitor(ILogicMonitor<T> monitor);
	
	public <T extends IMonitorInfo> void getAndSendFullMonitoredList(ILogicMonitor<T> monitor, MonitoredList<T> lastList);

	public <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(MonitorHandler<T> type);
}
