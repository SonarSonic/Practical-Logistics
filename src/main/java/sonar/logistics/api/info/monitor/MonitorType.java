package sonar.logistics.api.info.monitor;

import java.util.List;

import com.google.common.collect.Lists;

import sonar.core.helpers.SonarHelper;

public enum MonitorType {
	INFO, CHANNEL, TEMPORARY, FULL_INFO;

	public static final List<MonitorType> ALL = SonarHelper.convertArray(MonitorType.values());
}
