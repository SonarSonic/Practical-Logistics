package sonar.logistics.api.render;

import sonar.logistics.api.LogisticsAPI;

public enum ScreenType {
	NORMAL, HOLOGRAPHIC, LARGE, CONNECTED;

	public boolean isNormalSize() {
		return this == NORMAL || this == HOLOGRAPHIC;
	}

	public float getScaling() {
		return LogisticsAPI.getInfoRenderer().getScaling(this);
	}
}
