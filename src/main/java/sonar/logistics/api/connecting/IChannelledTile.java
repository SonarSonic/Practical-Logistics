package sonar.logistics.api.connecting;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;

public interface IChannelledTile extends ILogicTile {

	public IdentifiedCoordsList getChannels();

	public void removeViewer(EntityPlayer player);
}
