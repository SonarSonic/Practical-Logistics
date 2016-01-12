package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.inventory.ContainerSync;
import sonar.core.utils.helpers.NBTHelper;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;

public class ContainerDataReceiver extends ContainerSync {

	private static final int INV_START = 0, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	public ContainerDataReceiver(TileEntityDataReceiver entity, InventoryPlayer inventoryPlayer) {
		super(entity);
	}

	@Override
	public void detectAndSendChanges() {

		if (tile instanceof TileEntityDataReceiver) {
			if (sync != null) {
				if (crafters != null) {
					NBTTagCompound syncData = new NBTTagCompound();
					sync.writeData(syncData, NBTHelper.SyncType.SYNC);
					for (Object o : crafters) {
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
