package sonar.logistics.common.containers.slots;

import sonar.logistics.common.handlers.ArrayHandler;
import sonar.logistics.common.tileentity.TileEntityArray;
import sonar.logistics.registries.CacheRegistry;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ArraySlot extends Slot {

	ArrayHandler handler;

	public ArraySlot(ArrayHandler handler, int index, int x, int y) {
		super(handler, index, x, y);
		this.handler = handler;
	}

	public void onSlotChanged() {
		super.onSlotChanged();
		if (!handler.tile.getWorldObj().isRemote) {
			handler.updateCoordsList();
			CacheRegistry.refreshCache(((TileEntityArray) handler.tile).registryID);
		}
	}

	public boolean isItemValid(ItemStack stack) {
		return handler.isItemValidForSlot(this.getSlotIndex(), stack);
	}
}
