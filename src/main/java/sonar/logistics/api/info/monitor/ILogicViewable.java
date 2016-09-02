package sonar.logistics.api.info.monitor;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import sonar.core.utils.IUUIDIdentity;
import sonar.logistics.api.connecting.ILogicTile;

public interface ILogicViewable extends ILogicTile, IUUIDIdentity {

	public ArrayList<MonitorViewer> getViewers(boolean sentFirstPacket);

	/**removes a EntityPlayer viewer, used when the gui is closed*/
	public void removeViewer(EntityPlayer player);

	public void addViewer(MonitorViewer viewer);
}
