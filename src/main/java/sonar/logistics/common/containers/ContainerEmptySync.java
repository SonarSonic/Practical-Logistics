package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.inventory.ContainerSync;
import sonar.core.network.utils.ISyncTile;

public class ContainerEmptySync extends ContainerSync {

	public ContainerEmptySync(ISyncTile sync, TileEntity entity) {
		super(sync, entity);
	}
	public ContainerEmptySync(TileEntity entity) {
		super(entity);
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
