package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.api.ActionType;
import sonar.core.api.StoredItemStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.slots.SlotList;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.ICacheViewer;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class ContainerInventoryCache extends ContainerSync {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public ItemStack lastStack = null;
	public ICacheViewer viewer;

	public ContainerInventoryCache(ICacheViewer viewer, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(viewer, entity);
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
				if (!tile.getWorldObj().isRemote) {
					StoredItemStack stack = new StoredItemStack(itemstack);
					if (lastStack != null && ItemStack.areItemStackTagsEqual(itemstack1, lastStack) && lastStack.isItemEqual(itemstack1))
						LogisticsAPI.getItemHelper().insertInventoryFromPlayer(player, viewer.getNetwork(), slot.getSlotIndex());
					else {
						StoredItemStack perform = LogisticsAPI.getItemHelper().addItems(stack, viewer.getNetwork(), ActionType.PERFORM);
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

	public ItemStack slotClick(int slotID, int buttonID, int flag, EntityPlayer player) {
		if (slotID < this.inventorySlots.size()) {
			Slot targetSlot = slotID < 0 ? null : (Slot) this.inventorySlots.get(slotID);
			if ((targetSlot instanceof SlotList)) {
				if (buttonID == 2) {
					targetSlot.putStack(null);
				} else {
					targetSlot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
				}
				return player.inventory.getItemStack();
			}
			return super.slotClick(slotID, buttonID, flag, player);

		}
		return null;
	}

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.SPECIAL };
	}

}
