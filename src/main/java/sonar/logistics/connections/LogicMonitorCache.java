package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.Map.Entry;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.utils.Pair;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class LogicMonitorCache {

	// server side
	public static final ArrayList<ILogicMonitor> monitors = new ArrayList();
	// client side
	public static LinkedHashMap<ILogicMonitor, MonitoredList<?>> monitoredLists = new LinkedHashMap();

	// server method
	public static ILogicMonitor getMonitorFromClient(int hashCode) {
		for (ILogicMonitor monitor : monitors) {
			if (monitor.getMonitorUUID().hashCode() == hashCode) {
				return monitor;
			}
		}
		return null;

	}

	// client method
	public static Pair<ILogicMonitor, MonitoredList<?>> getMonitorFromServer(int hashCode) {
		for (Entry<ILogicMonitor, ?> entry : monitoredLists.entrySet()) {
			if (entry.getKey().getMonitorUUID().hashCode() == hashCode) {
				return new Pair(entry.getKey(), entry.getValue());
			}
		}
		return null;
	}

	public static <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(ILogicMonitor monitor) {
		monitoredLists.putIfAbsent(monitor, MonitoredList.<T>newMonitoredList());
		for (Entry<ILogicMonitor, MonitoredList<?>> entry : monitoredLists.entrySet()) {
			if (entry.getKey().getMonitorUUID().equals(monitor.getMonitorUUID())) {
				return (MonitoredList<T>) entry.getValue();
			}
		}
		return MonitoredList.<T>newMonitoredList();
	}
}
