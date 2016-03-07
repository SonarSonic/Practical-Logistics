package sonar.logistics.api.interaction;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockInteraction;
import sonar.logistics.api.info.Info;
import sonar.logistics.api.render.ScreenType;

public interface IDefaultInteraction {
	
	public void handleInteraction(Info info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick);

}
