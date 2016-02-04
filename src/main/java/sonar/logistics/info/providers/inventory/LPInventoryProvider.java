package sonar.logistics.info.providers.inventory;

import java.util.ArrayList;
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
	public StoredItemStack getStack(int slot, World world, int x, int y, int z, ForgeDirection dir) {
		List<ItemStack> items = getStackList(world, x, y, z);
		if (slot < items.size()) {
			return new StoredItemStack(items.get(slot));
		}
		return null;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, World world, int x, int y, int z, ForgeDirection dir) {
		List<ItemStack> items = getStackList(world, x, y, z);
		for (ItemStack stack : items) {
			InfoHelper.addStackToList(storedStacks, stack);
		}

		return false;

	}

	public List<ItemStack> getStackList(World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof ILPPipeTile) {
			ILPPipe pipe = ((ILPPipeTile) tile).getLPPipe();
			if (pipe instanceof IRequestAPI) {
				IRequestAPI request = (IRequestAPI) pipe;
				List<ItemStack> items = request.getProvidedItems();
				return items;
			}
		}
		return new ArrayList();
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("LogisticsPipes");
	}

}
