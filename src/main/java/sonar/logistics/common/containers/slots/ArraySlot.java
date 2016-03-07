package sonar.logistics.common.containers.slots;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.logistics.common.handlers.ArrayHandler;

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
			//CacheRegistry.refreshCache(((TileEntityArray) handler.tile).registryID);
		}
	}

	public boolean isItemValid(ItemStack stack) {
		return handler.isItemValidForSlot(this.getSlotIndex(), stack);
	}
}
