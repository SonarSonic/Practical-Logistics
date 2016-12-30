package sonar.logistics.common.containers;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sonar.core.api.IFlexibleContainer;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.inventory.slots.SlotList;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.settings.InventoryReader;
import sonar.logistics.api.settings.InventoryReader.Modes;
import sonar.logistics.common.multiparts.InventoryReaderPart;

public class ContainerInventoryReader extends ContainerMultipartSync implements IFlexibleContainer<InventoryReader.Modes> {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public boolean stackMode = false;
	public ItemStack lastStack = null;
	public InventoryReaderPart part;
	public EntityPlayer player;

	public ContainerInventoryReader(InventoryReaderPart part, EntityPlayer player) {
		super(part);
		this.part = part;
		this.player = player;
		refreshState();
	}

	@Override
	public void refreshState() {
		InventoryReader.Modes state = getCurrentState();
		stackMode = state == Modes.STACK;
		this.inventoryItemStacks.clear();
		this.inventorySlots.clear();
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 41 + j * 18, 174 + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(player.inventory, i, 41 + i * 18, 232));
		}
		if (stackMode)
			addSlotToContainer(new SlotList(part.inventory, 0, 103, 9));
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
					StoredItemStack stack = new StoredItemStack(itemstack1);
					if (lastStack != null && ItemStack.areItemStackTagsEqual(itemstack1, lastStack) && lastStack.isItemEqual(itemstack1)) {
						LogisticsAPI.getItemHelper().addItemsFromPlayer(stack, player, part.network, ActionType.PERFORM);
					} else {
						StoredItemStack perform = LogisticsAPI.getItemHelper().addItems(stack, part.network, ActionType.PERFORM);
						lastStack = itemstack1;

						itemstack1.stackSize = (int) (perform == null || perform.stored == 0 ? 0 : (perform.getStackSize()));
						player.inventory.markDirty();
					}
					this.detectAndSendChanges();
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
				targetSlot.putStack(drag == 2 ? null : player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
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

	@Override
	public InventoryReader.Modes getCurrentState() {
		return part.setting.getObject();
	}
}
