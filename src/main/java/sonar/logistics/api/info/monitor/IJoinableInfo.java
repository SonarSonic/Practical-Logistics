package sonar.logistics.api.info.monitor;

public interface IJoinableInfo<T extends IMonitorInfo> extends IMonitorInfo<T> {

	public boolean canJoinInfo(T info);
	
	public IJoinableInfo joinInfo(T info);
}
