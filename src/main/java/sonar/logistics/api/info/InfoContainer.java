package sonar.logistics.api.info;

import java.util.ArrayList;

public class InfoContainer implements IInfoContainer {

	public ArrayList<IDisplayInfo> storedInfo = new ArrayList();
	
	@Override
	public ArrayList<IDisplayInfo> getStoredInfo() {
		return storedInfo;
	}

	@Override
	public void addInfo(IDisplayInfo info) {
		storedInfo.add(info);		
	}

	@Override
	public void removeInfo(IDisplayInfo info) {
		storedInfo.remove(info);
	}

	@Override
	public void renderContainer() {
		
	}

}
