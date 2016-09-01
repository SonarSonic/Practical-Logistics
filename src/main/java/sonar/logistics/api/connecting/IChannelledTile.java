package sonar.logistics.api.connecting;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;

/**a tile which has channels which can be configured by the operator*/
public interface IChannelledTile extends ILogicTile {

	/**the currently selected channels*/
	public IdentifiedCoordsList getChannels();

	/**removes a EntityPlayer viewer, used when the gui is closed*/
	public void removeViewer(EntityPlayer player);
}
