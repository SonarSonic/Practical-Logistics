package sonar.logistics.info.providers.inventory;

import java.util.List;

import logisticspipes.api.ILPPipe;
import logisticspipes.api.ILPPipeTile;
import logisticspipes.api.IRequestAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.providers.InventoryProvider;
import sonar.logistics.helpers.InfoHelper;
import cpw.mods.fml.common.Loader;

public class LPInventoryProvider extends InventoryProvider {

	public static String name = "LP-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideItems(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null && tile instanceof ILPPipeTile;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof ILPPipeTile) {
			ILPPipe pipe = ((ILPPipeTile) tile).getLPPipe();
			if (pipe instanceof IRequestAPI) {
				IRequestAPI request = (IRequestAPI) pipe;
				List<ItemStack> items = request.getProvidedItems();
				for (ItemStack stack : items) {
					InfoHelper.addStackToList(storedStacks, stack);
				}
				return true;
			}
		}
		return false;

	}

	public boolean isLoadable() {
		return Loader.isModLoaded("LogisticsPipes");
	}
}
