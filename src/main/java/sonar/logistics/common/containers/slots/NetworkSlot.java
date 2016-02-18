package sonar.logistics.common.containers.slots;

import sonar.core.integration.fmp.FMPHelper;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

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
		 return stored == null ? null : stored.getFullStack();
		//return null;
		/*
		 * if (this.getSlotIndex() < handler.stacks.size()) { StoredItemStack
		 * stack = this.handler.stacks.get(this.getSlotIndex()); if (stack ==
		 * null) { return null; } return stack.getFullStack(); } else { return
		 * null; }
		 */
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
		return this.getStoredStack() != null;
	}

	public void putStack(ItemStack add) {
		/*
		 * if (add == null) { return; } StoredItemStack stack =
		 * LogisticsAPI.getItemHelper().addItems(new StoredItemStack(add),
		 * LogisticsAPI.getCableHelper().getConnections(te,
		 * ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()),
		 * ActionType.PERFORM);
		 * 
		 * if (stack == null || stack.stored == 0) { add = null; } else {
		 * add.stackSize = (int) stack.stored; }
		 * 
		 * this.onSlotChanged();
		 */
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

			// StoredItemStack perform =
			// LogisticsAPI.getItemHelper().removeItems(stack.copy(),
			// handler.getNetwork(te), ActionType.PERFORM);
			StoredItemStack storedStack = new StoredItemStack(stack.getItemStack(), extractSize);

			return storedStack.getFullStack().copy();
		}
		/*
		 * StoredItemStack stack = this.getStoredStack(); if (stack == null) {
		 * return null; } int extractSize = (int)
		 * Math.min(stack.item.getMaxStackSize(), size);
		 * stack.setStackSize(extractSize); //StoredItemStack stack = new
		 * StoredItemStack(add, extractSize); StoredItemStack remainder =
		 * LogisticsAPI
		 * .getItemHelper().removeItems(stack.setStackSize(extractSize),
		 * LogisticsAPI.getCableHelper().getConnections(te,
		 * ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()),
		 * ActionType.PERFORM); StoredItemStack storedStack = null; if
		 * (remainder == null || remainder.stored == 0) { storedStack = new
		 * StoredItemStack(stack.getItemStack(), extractSize); } else {
		 * storedStack = new StoredItemStack(stack.getItemStack(), extractSize -
		 * remainder.stored); }
		 */
		// System.out.print("yo");
		// if (!te.getWorldObj().isRemote && this.getSlotIndex() <
		// handler.stacks.size() && handler.stacks.get(this.getSlotIndex()) !=
		// null) {
		// StoredItemStack stack = new
		// StoredItemStack(handler.stacks.get(this.getSlotIndex()).getFullStack(),
		// Math.min(size, handler.stacks.get(getSlotIndex()).stored));
		// StoredItemStack perform =
		// LogisticsAPI.getItemHelper().removeItems(stack,
		// handler.getNetwork(te), ActionType.PERFORM);
		/*
		 * if (perform == null || perform.stored == 0) { itemstack1.stackSize =
		 * 0; } else { itemstack1.stackSize = (int) (perform.getStackSize()); }
		 */
		// }

		// return null;
	}
}
