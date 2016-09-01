package sonar.logistics.api.info.monitor;

/**implemented on info which can be joined in a list, 
 * used for things like Items or Fluids which are joined together to show a value across the whole system rather than individual stacks*/
public interface IJoinableInfo<T extends IMonitorInfo> extends IMonitorInfo<T> {

	/**can this info be joined together*/
	public boolean canJoinInfo(T info);
	
	/**join the info*/
	public IJoinableInfo joinInfo(T info);
}
