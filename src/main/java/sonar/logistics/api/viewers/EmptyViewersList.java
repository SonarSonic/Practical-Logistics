package sonar.logistics.api.viewers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.ILogicViewable;

public class EmptyViewersList implements IViewersList {
	
	public static final EmptyViewersList INSTANCE = new EmptyViewersList();
	
	@Override
	public boolean hasViewers() {
		return false;
	}

	@Override
	public ArrayList<ILogicViewable> getConnectedDisplays() {
		return new ArrayList();
	}

	@Override
	public HashMap<EntityPlayer, ArrayList<MonitorTally>> getViewers(boolean includeDisplays) {
		return new HashMap();
	}

	@Override
	public ArrayList<EntityPlayer> getViewers(boolean includeDisplays, ViewerType... types) {
		return new ArrayList();
	}

	@Override
	public void addViewer(EntityPlayer player, ViewerType type) {}

	@Override
	public void addViewer(EntityPlayer player, ArrayList<ViewerType> typesToAdd) {}

	@Override
	public void removeViewer(EntityPlayer player, ViewerType type) {}

	@Override
	public void removeViewer(EntityPlayer player, ArrayList<ViewerType> typesToRemove) {}


}
