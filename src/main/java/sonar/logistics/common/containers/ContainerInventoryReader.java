package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.slots.SlotList;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.common.containers.slots.NetworkSlot;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class ContainerInventoryReader extends ContainerSync {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public boolean stackMode = false;
	InventoryReaderHandler handler;

	public ContainerInventoryReader(InventoryReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(handler, entity);
		addSlots(handler, inventoryPlayer, handler.setting.getInt() == 0);
		this.handler = handler;
	}

	public void addSlots(InventoryReaderHandler handler, InventoryPlayer inventoryPlayer, boolean hasStack) {
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
		for (int i = 0; i < 7; ++i) {
			for (int j = 0; j < 12; ++j) {
				this.addSlotToContainer(new NetworkSlot(handler, tile, j + i * 12, 13 + j * 18, 32 + i * 18));
			}
		}
		if (hasStack)
			addSlotToContainer(new SlotList(handler, 0, 103, 9));
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer player, int id) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(id);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = null;
			if (slot instanceof NetworkSlot) {
				itemstack1 = ((NetworkSlot) slot).getStoredStack().getFullStack();
			} else {
				itemstack1 = slot.getStack();
			}
			itemstack = itemstack1.copy();
			if (id < 36) {
				// ((Slot)
				// this.inventorySlots.get(36)).putStack(itemstack1.copy());
				// handler.insertItem(player, tile, slot.getSlotIndex());

				if (!tile.getWorldObj().isRemote) {
					return null;
				}
				if (!this.mergeItemStack(itemstack1, 37, 121, false)) {
					return null;
				}
				if (slot instanceof NetworkSlot) {
					return ((NetworkSlot) slot).getStoredStack().getFullStack();
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
