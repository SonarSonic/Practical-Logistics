package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.inventory.slots.SlotList;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.readers.InventoryReader.Modes;
import sonar.logistics.parts.InventoryReaderPart;

public class ContainerInventoryReader extends ContainerMultipartSync {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public boolean stackMode = false;
	public ItemStack lastStack = null;
	InventoryReaderPart part;

	public ContainerInventoryReader(InventoryReaderPart part, EntityPlayer player) {
		super(part);
		addSlots(part, player.inventory, part.setting.getObject() == Modes.STACK);
		this.part = part;
	}

	public void addSlots(InventoryReaderPart handler, InventoryPlayer inventoryPlayer, boolean hasStack) {
		stackMode = hasStack;
		this.inventoryItemStacks.clear();
		this.inventorySlots.clear();
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 41 + j * 18, 174 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 41 + i * 18, 232));
		}
		if (hasStack)
			addSlotToContainer(new SlotList(handler.inventory, 0, 103, 9));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int id) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(id);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (id < 36) {
				if (!part.getWorld().isRemote) {
					StoredItemStack stack = new StoredItemStack(itemstack);
					if (lastStack != null && ItemStack.areItemStackTagsEqual(itemstack1, lastStack) && lastStack.isItemEqual(itemstack1))
						LogisticsAPI.getItemHelper().insertInventoryFromPlayer(player, part.network, slot.getSlotIndex());
					else {
						StoredItemStack perform = LogisticsAPI.getItemHelper().addItems(stack, part.network, ActionType.PERFORM);
						lastStack = itemstack1;
						if (perform == null || perform.stored == 0) {
							itemstack1.stackSize = 0;
						} else {
							itemstack1.stackSize = (int) (perform.getStackSize());
						}
					}
					// this.detectAndSendChanges();
				}
			} else if (id < 27) {
				if (!this.mergeItemStack(itemstack1, 27, 36, false)) {
					return null;
				}
			} else if (id >= 27 && id < 36) {
				if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
					return null;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, 36, false)) {
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

	public ItemStack slotClick(int slotID, int drag, ClickType click, EntityPlayer player) {
		if (slotID < this.inventorySlots.size()) {
			Slot targetSlot = slotID < 0 ? null : (Slot) this.inventorySlots.get(slotID);
			if ((targetSlot instanceof SlotList)) {
				if (drag == 2) {
					targetSlot.putStack(null);
				} else {
					targetSlot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
				}
				return player.inventory.getItemStack();
			}
			return super.slotClick(slotID, drag, click, player);

		}
		return null;
	}

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.DEFAULT_SYNC };
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		part.removeViewer(player);
	}
}
