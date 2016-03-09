package sonar.logistics.api.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockInteraction;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;

/**implemented on Tiles of TileHandlers which have a default interaction when a screen has been clicked, if not other interaction handler is available*/
public interface IDefaultInteraction {
	
	/**called when no other handlers are available*/
	public void handleInteraction(ILogicInfo info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick);

}
