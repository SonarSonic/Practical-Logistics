package sonar.logistics.api.render;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.BlockInteractionType;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.Info;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.info.types.InventoryInfo;

public abstract class InfoInteractionHandler<T extends Info> implements IRegistryObject {

	public abstract boolean canHandle(ScreenType type, TileEntity te, TileEntity object);

	public abstract void handleInteraction(T info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick);

	public boolean isLoadable() {
		return true;
	}
}