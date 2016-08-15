package sonar.logistics.api.info.monitor;

public enum MonitorStatus {
	PLAYER, SCREEN, NONE;

	public boolean isBeingMonitored() {
		return this != NONE;
	}
}
