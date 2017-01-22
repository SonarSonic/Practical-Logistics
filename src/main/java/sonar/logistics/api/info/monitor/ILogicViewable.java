package sonar.logistics.api.info.monitor;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.utils.IUUIDIdentity;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.connections.monitoring.ViewersList;

public interface ILogicViewable extends ILogicTile, IUUIDIdentity {

	public ViewersList getViewersList();
	
	public void onViewerAdded(EntityPlayer player, List<MonitorType> type);
	
	public void onViewerRemoved(EntityPlayer player, List<MonitorType> type);
				
}
