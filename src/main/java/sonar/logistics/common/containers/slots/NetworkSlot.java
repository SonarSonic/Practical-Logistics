package sonar.logistics.common.containers.slots;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.ActionType;
import sonar.core.api.StoredItemStack;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class NetworkSlot extends Slot {

	InventoryReaderHandler handler;
	TileEntity te;

	public NetworkSlot(InventoryReaderHandler handler, TileEntity te, int index, int x, int y) {
		super(handler, index, x, y);
		this.handler = handler;
		this.te = te;
	}

	public ItemStack getStack() {
		StoredItemStack stored = getStoredStack();
		return null;
	}

	public StoredItemStack getStoredStack() {
		if (this.getSlotIndex() < handler.stacks.size()) {
			StoredItemStack stack = this.handler.stacks.get(this.getSlotIndex());
			return stack;
		} else {
			return null;
		}
	}

	public boolean getHasStack() {
		return false;
	}

	public void putStack(ItemStack add) {
	}

	public ItemStack decrStackSize(int size) {
		if (!te.getWorldObj().isRemote) {
			int extractSize = (int) Math.min(handler.stacks.get(getSlotIndex()).item.getMaxStackSize(), Math.min(handler.stacks.get(getSlotIndex()).stored, size));
			StoredItemStack stack = new StoredItemStack(handler.stacks.get(getSlotIndex()).getFullStack(), extractSize);

			StoredItemStack perform = LogisticsAPI.getItemHelper().removeItems(stack.copy(), handler.getNetwork(te), ActionType.PERFORM);
			StoredItemStack storedStack = null;
			if (perform == null || perform.stored == 0) {
				storedStack = new StoredItemStack(stack.getItemStack(), extractSize);
			} else {
				storedStack = new StoredItemStack(stack.getItemStack(), extractSize - perform.stored);
			}
			return storedStack.getFullStack().copy();
		} else {
			int extractSize = (int) Math.min(handler.stacks.get(getSlotIndex()).item.getMaxStackSize(), Math.min(handler.stacks.get(getSlotIndex()).stored, size));
			StoredItemStack stack = new StoredItemStack(handler.stacks.get(getSlotIndex()).getFullStack(), extractSize);
			StoredItemStack storedStack = new StoredItemStack(stack.getItemStack(), extractSize);

			return storedStack.getFullStack().copy();
		}

	}

	public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
