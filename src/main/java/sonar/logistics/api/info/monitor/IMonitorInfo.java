package sonar.logistics.api.info.monitor;

import io.netty.buffer.ByteBuf;

public interface IMonitorInfo<T extends IMonitorInfo> {

	/** if they are identical **/
	public boolean isIdenticalInfo(T info);

	/** if they are of the same type with just different values **/
	public boolean isMatchingInfo(T info);

	/** if they are of the same type with just different values **/
	public boolean isMatchingType(IMonitorInfo info);

	public boolean isHeader();

	public T getLastInfo();
	
	public boolean writeChangesToBuf(T lastInfo, ByteBuf buf);
	
	// setting stack sizes and stuff like that
	public void updateFrom(ByteBuf buf);

}
