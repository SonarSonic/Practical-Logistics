package sonar.logistics.api.info;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;

public interface IInfoContainer {

	//public void updateInfo(InfoUUID id, ByteBuf updateBuf);
	
	public ArrayList<IDisplayInfo> getStoredInfo();
	
	public void addInfo(IDisplayInfo info);
	
	public void removeInfo(IDisplayInfo info);
	
	public void renderContainer();
	
}
