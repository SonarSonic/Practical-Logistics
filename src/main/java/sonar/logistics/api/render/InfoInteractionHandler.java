package sonar.logistics.api.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.info.ILogicInfo;

/** used for Interactions with Display Screens, these can be registered with the {@link RegistryWrapper}, the generic T being the Info Type it provides for */
public abstract class InfoInteractionHandler<T extends ILogicInfo> implements IRegistryObject {

	public abstract void handleInteraction(T info, ScreenType type, TileEntity screen, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick);

	public boolean isLoadable() {
		return true;
	}
}