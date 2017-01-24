package sonar.logistics.api.viewers;

import java.util.List;

import sonar.core.helpers.SonarHelper;

public enum ViewerType {
	INFO, CHANNEL, TEMPORARY, FULL_INFO;

	public static final List<ViewerType> ALL = SonarHelper.convertArray(ViewerType.values());
}
