package sonar.logistics.info.providers.inventory;

import java.util.ArrayList;
import java.util.List;

import logisticspipes.api.ILPPipe;
import logisticspipes.api.ILPPipeTile;
import logisticspipes.api.IRequestAPI;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.InventoryHandler;
import cpw.mods.fml.common.Loader;

public class LPInventoryProvider extends InventoryHandler {

	public static String name = "LP-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, ForgeDirection dir) {
		return tile != null && tile instanceof ILPPipeTile;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir) {
		List<ItemStack> items = getStackList(tile);
		if (slot < items.size()) {
			return new StoredItemStack(items.get(slot));
		}
		return null;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		List<ItemStack> items = getStackList(tile);
		for (ItemStack stack : items) {
			LogisticsAPI.getItemHelper().addStackToList(storedStacks, new StoredItemStack(stack));
			return true;
		}

		return false;

	}

	public List<ItemStack> getStackList(TileEntity tile) {
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

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir) {
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir) {
		return remove;
	}

}
