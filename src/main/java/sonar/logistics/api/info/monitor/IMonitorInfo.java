package sonar.logistics.api.info.monitor;

public interface IMonitorInfo<T extends IMonitorInfo> {
	
	/**if they are identical**/
	public boolean isIdenticalInfo(T info);
	
	/**if they are of the same type with just different values**/
	public boolean isMatchingInfo(T info);
	
	/**if they are of the same type with just different values**/
	public boolean isMatchingType(IMonitorInfo info);
	
	//setting stack sizes and stuff like that
	public void updateFrom(T info);	
	
	public boolean isHeader();
	
}
