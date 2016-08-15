package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerSync;

public class DEADContainerEmptySync extends ContainerSync {
	SyncType[] types = new SyncType[] { SyncType.DEFAULT_SYNC };

	public ContainerEmptySync(INBTSyncable sync, TileEntity entity) {
		super(sync, entity);
	}

	public ContainerEmptySync(TileEntity entity) {
		super(entity);
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
		return types;
	}
}
