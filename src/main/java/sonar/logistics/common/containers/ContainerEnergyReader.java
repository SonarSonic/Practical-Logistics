package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.logistics.common.multiparts.EnergyReaderPart;
import sonar.logistics.common.multiparts.InfoReaderPart;

public class ContainerEnergyReader extends ContainerMultipartSync {
	public EnergyReaderPart part;

	public ContainerEnergyReader(EntityPlayer player, EnergyReaderPart part) {
		super(part);
		this.part = part;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	public boolean syncInventory() {
		return false;
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

	@Override
	public ItemStack slotClick(int slotID, int drag, ClickType click, EntityPlayer player) {
		if (slotID >= 0 && getSlot(slotID) != null && getSlot(slotID).getStack() == player.getHeldItemMainhand()) {
			return null;
		}
		return super.slotClick(slotID, drag, click, player);
	}

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.DEFAULT_SYNC };
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		part.removeViewer(player);
	}
}
