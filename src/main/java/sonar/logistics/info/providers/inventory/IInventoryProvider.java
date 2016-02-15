package sonar.logistics.info.providers.inventory;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.InventoryHandler;

public class IInventoryProvider extends InventoryHandler {

	public static String name = "Standard Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IInventory;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir) {
		IInventory inv = (IInventory) tile;
		if (slot < inv.getSizeInventory()) {
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack != null)
				return new StoredItemStack(stack);
		}
		return null;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IInventory) {
			LogisticsAPI.getItemHelper().addInventoryToList(storedStacks, (IInventory) tile);
			return true;
		}
		return false;
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir) {
		IInventory inv = (IInventory) tile;
		List<Integer> empty = new ArrayList();
		int invSize = inv.getSizeInventory();
		int[] slots = null;
		if (tile instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) tile;
			slots = sidedInv.getAccessibleSlotsFromSide(dir.ordinal());
			// need to add other sided inv methods
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			int slot = slots != null ? slots[i] : i;
			ItemStack stack = inv.getStackInSlot(slot);
			if (!(tile instanceof ISidedInventory) || ((ISidedInventory) tile).canInsertItem(slot, add.item, dir.ordinal())) {
				if (stack != null) {
					if (add.equalStack(stack) && stack.stackSize < inv.getInventoryStackLimit()) {
						long used = (long) Math.min(add.stored, inv.getInventoryStackLimit() - stack.stackSize);
						stack.stackSize += used;
						add.stored -= used;
						inv.setInventorySlotContents(slot, stack);
						if (add.stored == 0) {
							return null;
						}
					}
				} else {
					empty.add(slot);
				}
			}
		}
		for (Integer slot : empty) {
			ItemStack stack = add.item.copy();
			int used = (int) Math.min(add.stored, inv.getInventoryStackLimit());
			stack.stackSize = used;
			add.stored -= used;
			inv.setInventorySlotContents(slot, stack);
			if (add.stored == 0) {
				return null;
			}
		}
		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir) {
		// int removed = 0;
		IInventory inv = (IInventory) tile;
		int invSize = inv.getSizeInventory();
		int[] slots = null;
		if (tile instanceof ISidedInventory) {
			ISidedInventory sidedInv = (ISidedInventory) tile;
			slots = sidedInv.getAccessibleSlotsFromSide(dir.ordinal());
			invSize = slots.length;
		}
		for (int i = 0; i < invSize; i++) {
			int slot = slots != null ? slots[i] : i;
			ItemStack stack = inv.getStackInSlot(slot);
			if (stack != null) {
				if (!(tile instanceof ISidedInventory) || ((ISidedInventory) tile).canExtractItem(slot, stack, dir.ordinal())) {
					if (remove.equalStack(stack)) {
						long used = (long) Math.min(remove.stored, Math.min(inv.getInventoryStackLimit(), stack.stackSize));
						stack.stackSize -= used;
						remove.stored -= used;
						if (stack.stackSize == 0) {
							stack = null;
						}
						inv.setInventorySlotContents(slot, stack);
						if (remove.stored == 0) {
							return null;
						}
					}
				}
			}
		}
		return new StoredItemStack(remove.item, remove.stored);
	}
}
