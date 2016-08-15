package sonar.logistics.api.info;

import java.util.ArrayList;

public interface IInfoContainer {

	public ArrayList<IDisplayInfo> getStoredInfo();
	
	public void addInfo(IDisplayInfo info);
	
	public void removeInfo(IDisplayInfo info);
	
	public void renderContainer();
	
}
