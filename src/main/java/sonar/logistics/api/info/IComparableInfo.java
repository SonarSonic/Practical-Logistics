package sonar.logistics.api.info;

import java.util.Map;

import sonar.logistics.api.info.monitor.IMonitorInfo;

public interface IComparableInfo<T extends IMonitorInfo> extends IMonitorInfo<T> {

	public void getComparableObjects(Map<String, Object> objects);

}
