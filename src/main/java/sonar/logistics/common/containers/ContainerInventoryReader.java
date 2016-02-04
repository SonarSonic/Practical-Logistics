package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.slots.SlotList;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class ContainerInventoryReader extends ContainerSync {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public boolean stackMode = false;

	public ContainerInventoryReader(InventoryReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(handler, entity);
		addSlots(handler, inventoryPlayer, handler.setting.getInt() == 0);
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
		if (hasStack)
			addSlotToContainer(new SlotList(handler, 0, 103, 9));
	}

	@Override
	public void detectAndSendChanges() {
		for (int i = 0; i < this.inventorySlots.size(); ++i) {
			ItemStack itemstack = ((Slot) this.inventorySlots.get(i)).getStack();
			ItemStack itemstack1 = (ItemStack) this.inventoryItemStacks.get(i);

			if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
				itemstack1 = itemstack == null ? null : itemstack.copy();
				this.inventoryItemStacks.set(i, itemstack1);

				for (int j = 0; j < this.crafters.size(); ++j) {
					((ICrafting) this.crafters.get(j)).sendSlotContents(this, i, itemstack1);
				}
			}
		}
		if (sync != null) {
			if (crafters != null) {
				NBTTagCompound tag = new NBTTagCompound();
				TileHandler handler = FMPHelper.getHandler(tile);
				handler.writeData(tag, SyncType.SPECIAL);
				if (tag.hasNoTags()) {
					return;
				}
				for (Object o : crafters) {
					if (o != null && o instanceof EntityPlayerMP) {
						SonarCore.network.sendTo(new PacketTileSync(tile.xCoord, tile.yCoord, tile.zCoord, tag, SyncType.SPECIAL), (EntityPlayerMP) o);

					}
				}

			}

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
			if (stackMode && id < 36) {
				((Slot) this.inventorySlots.get(36)).putStack(itemstack1.copy());

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

}
