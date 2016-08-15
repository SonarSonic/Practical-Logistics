package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.inventory.ContainerSync;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;

public class ContainerDataReceiver extends ContainerSync {

	public ContainerDataReceiver(TileEntityDataReceiver entity, InventoryPlayer inventoryPlayer) {
		super(entity);
	}

	@Override
	public void detectAndSendChanges() {
		if (tile instanceof TileEntityDataReceiver) {
			if (sync != null) {
				if (listeners != null) {
					for (IContainerListener o : listeners) {
						if (o != null && o instanceof EntityPlayerMP) {
							((TileEntityDataReceiver) tile).sendAvailableData(tile, (EntityPlayerMP) o);
						}
					}
				}
			}
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotID);
		return itemstack;
	}
}
