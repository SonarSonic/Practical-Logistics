package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.inventory.ContainerSync;
import sonar.core.utils.helpers.NBTHelper;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;

public class ContainerChannelSelector extends ContainerSync {

	public ChannelSelectorHandler handler;

	public ContainerChannelSelector(TileEntity tile, ChannelSelectorHandler handler, InventoryPlayer inventoryPlayer) {
		super(handler, tile);
		this.handler = handler;
	}

	@Override
	public void detectAndSendChanges() {
		if (sync != null) {
			if (crafters != null) {
				NBTTagCompound syncData = new NBTTagCompound();
				sync.writeData(syncData, NBTHelper.SyncType.SYNC);
				for (Object o : crafters) {
					if (o != null && o instanceof EntityPlayerMP) {
						handler.sendAvailableData(tile, (EntityPlayerMP) o);
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
