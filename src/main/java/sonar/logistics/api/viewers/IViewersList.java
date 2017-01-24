package sonar.logistics.api.viewers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.ILogicViewable;

public interface IViewersList {
	
	public boolean hasViewers();

	public ArrayList<ILogicViewable> getConnectedDisplays();

	public HashMap<EntityPlayer, ArrayList<MonitorTally>> getViewers(boolean includeDisplays);

	public ArrayList<EntityPlayer> getViewers(boolean includeDisplays, ViewerType... types);

	public void addViewer(EntityPlayer player, ViewerType type);

	public void addViewer(EntityPlayer player, ArrayList<ViewerType> typesToAdd);

	public void removeViewer(EntityPlayer player, ViewerType type);

	public void removeViewer(EntityPlayer player, ArrayList<ViewerType> typesToRemove);

}
