package sonar.logistics.common.containers;

import ic2.api.item.IElectricItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.energy.DischargeValues;
import sonar.core.integration.SonarAPI;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.slots.SlotBlockedInventory;
import sonar.logistics.common.tileentity.TileEntityHammer;
import cofh.api.energy.IEnergyContainerItem;

public class ContainerHammer extends ContainerSync {

	private TileEntityHammer entity;

	public ContainerHammer(InventoryPlayer inventory, TileEntityHammer entity) {
		super(entity);
		this.entity = entity;

		addSlotToContainer(new Slot(entity, 0, 53, 24));
		addSlotToContainer(new SlotBlockedInventory(entity, 1, 107, 24));

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 62 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventory, i, 8 + i * 18, 119));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int id) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(id);

		if ((slot != null) && (slot.getHasStack())) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (id == 1) {
				if (!mergeItemStack(itemstack1, 2, 38, true)) {
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else if (id != 0 && id != 1) {
				if (entity.isItemValidForSlot(0, itemstack1)) {
					if (!mergeItemStack(itemstack1, 0, 1, false)) {
						return null;
					}
				} else if ((id >= 2) && (id < 29)) {
					if (!mergeItemStack(itemstack1, 29, 38, false)) {
						return null;
					}
				} else if ((id >= 29) && (id < 38) && (!mergeItemStack(itemstack1, 2, 29, false))) {
					return null;
				}
			} else if (!mergeItemStack(itemstack1, 2, 38, false)) {
				return null;
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return entity.isUseableByPlayer(player);
	}

}
