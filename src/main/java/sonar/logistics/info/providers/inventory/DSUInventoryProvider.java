package sonar.logistics.info.providers.inventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.providers.InventoryHandler;

public class DSUInventoryProvider extends InventoryHandler {

	public static String name = "DSU-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IDeepStorageUnit;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir) {
		if (!(slot > 0)) {
			return getStoredItem(tile);
		}
		return null;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		StoredItemStack stack = getStoredItem(tile);
		if (stack != null) {
			storedStacks.add(stack);
			return true;
		}
		return false;

	}

	public StoredItemStack getStoredItem(TileEntity tile) {
		IDeepStorageUnit inv = (IDeepStorageUnit) tile;
		if (inv.getStoredItemType() != null) {
			return new StoredItemStack(inv.getStoredItemType());
		}

		return null;
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir) {
		IDeepStorageUnit inv = (IDeepStorageUnit) tile;
		ItemStack stack = inv.getStoredItemType();
		if (stack != null) {
			if (add.equalStack(stack)) {
				long max = inv.getMaxStoredCount();
				long storedItems = stack.stackSize;
				if (max == storedItems) {
					return add;
				}

				storedItems += add.getStackSize();
				if (storedItems > max) {
					long remove = storedItems - max;
					inv.setStoredItemCount((int) max);
					return new StoredItemStack(add.getItemStack(), remove);
				} else {
					inv.setStoredItemCount(stack.stackSize + (int) add.getStackSize());
					return null;
				}
			}
		} else {
			//if (add.getTagCompound() != null) {
			//	return add;
			//}
			inv.setStoredItemType(add.getItemStack(), (int) add.getStackSize());
			return null;
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir) {
		IDeepStorageUnit inv = (IDeepStorageUnit) tile;
		ItemStack stack = inv.getStoredItemType();
		if (remove.equalStack(stack)) {
			if (remove.getStackSize() >= stack.stackSize) {
				stack = stack.copy();
				inv.setStoredItemCount(0);
				remove.stored-=stack.stackSize;
				return remove;
			} else {
				inv.setStoredItemCount(stack.stackSize - (int) remove.getStackSize());
				return null;
			}
		}
		return remove;
	}

}
