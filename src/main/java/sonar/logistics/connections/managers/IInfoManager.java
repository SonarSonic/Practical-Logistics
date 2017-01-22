package sonar.logistics.connections.managers;

import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.monitoring.MonitoredList;

public interface IInfoManager {
	
	public LinkedHashMap<UUID, ILogicMonitor> getMonitors();
	
	public LinkedHashMap<InfoUUID, IMonitorInfo> getInfoList();
	
	public ConcurrentHashMap<Integer, ConnectedDisplayScreen> getConnectedDisplays();
	
	public <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(int networkID, InfoUUID uuid);
	
	public void addMonitor(ILogicMonitor monitor);

	public void removeMonitor(ILogicMonitor monitor);
}
