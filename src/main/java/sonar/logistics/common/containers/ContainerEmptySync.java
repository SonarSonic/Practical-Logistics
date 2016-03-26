package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerSync;
import sonar.core.network.utils.ISyncTile;

public class ContainerEmptySync extends ContainerSync {
	SyncType[] types = new SyncType[] { SyncType.SYNC };

	public ContainerEmptySync(ISyncTile sync, TileEntity entity) {
		super(sync, entity);
	}

	public ContainerEmptySync(TileEntity entity) {
		super(entity);
	}

	public ContainerEmptySync setTypes(SyncType[] types) {
		this.types = types;
		return this;
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
