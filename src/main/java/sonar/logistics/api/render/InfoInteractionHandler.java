package sonar.logistics.api.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.IRegistryObject;
import sonar.core.api.utils.BlockInteraction;
import sonar.logistics.api.info.DEADILogicInfo;

/** used for Interactions with Display Screens, these can be registered with the {@link RegistryWrapper}, the generic T being the Info Type it provides for */
public abstract class InfoInteractionHandler<T extends DEADILogicInfo> implements IRegistryObject {

	public abstract void handleInteraction(T info, ScreenType type, TileEntity screen, EntityPlayer player, BlockPos pos, BlockInteraction interact);
	//public abstract void onClient(T info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick);
	
	public boolean isLoadable() {
		return true;
	}
}