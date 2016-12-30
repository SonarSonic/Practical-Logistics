package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipart;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.common.multiparts.InfoReaderPart;

public class ContainerInfoDisplay extends ContainerMultipartSync {

	public ContainerInfoDisplay(IInfoDisplay display) {
		super((SonarMultipart) display);
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

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.DEFAULT_SYNC };
	}
}