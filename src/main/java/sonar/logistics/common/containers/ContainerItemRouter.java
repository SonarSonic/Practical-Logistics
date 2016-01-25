package sonar.logistics.common.containers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.common.item.calculators.CalculatorItem;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.slots.SlotAllowed;
import sonar.core.inventory.slots.SlotList;
import sonar.core.network.PacketTileSync;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.handlers.ItemRouterHandler;
import sonar.logistics.common.tileentity.TileEntityItemRouter;

public class ContainerItemRouter extends ContainerSync {

	private static final int INV_START = 9, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;

	public int state = 0;

	public ContainerItemRouter(TileEntityItemRouter entity, InventoryPlayer inventoryPlayer) {
		super(entity);
		addSlots(inventoryPlayer, entity);
	}

	public void addSlots(InventoryPlayer player, TileEntityItemRouter entity) {
		if (state == 0) {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 151 + i * 18));
				}
			}

			for (int i = 0; i < 9; ++i) {
				this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 209));
			}
			for (int i = 0; i < 9; ++i) {
				addSlotToContainer(new Slot(entity, i, 8 + i * 18, 129));
			}
		} else {
			for (int i = 0; i < 3; ++i) {
				for (int j = 0; j < 9; ++j) {
					this.addSlotToContainer(new Slot(player, j + i * 9 + 9, 8 + j * 18, 56 + i * 18));
				}
			}

			for (int i = 0; i < 9; ++i) {
				this.addSlotToContainer(new Slot(player, i, 8 + i * 18, 114));
			}
			addSlotToContainer(new SlotList(entity.handler.clientStackFilter, 0, 23, 23));

		}

	}

	public void switchState(InventoryPlayer player, TileEntityItemRouter entity, int state) {
		this.state = state;
		this.inventoryItemStacks.clear();
		this.inventorySlots.clear();
		this.addSlots(player, entity);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
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
			if (state == 0) {

				if (id < 36) {
					if (!this.mergeItemStack(itemstack1, 36, 45, false)) {
						return null;
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
			} else {
				if (id < 36) {
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
		}
		return itemstack;
	}

	public ItemStack slotClick(int slotID, int buttonID, int flag, EntityPlayer player) {
		if (state == 1) {
			Slot targetSlot = slotID < 0 ? null : (Slot) this.inventorySlots.get(slotID);
			if ((targetSlot instanceof SlotList)) {
				if (buttonID == 1) {
					targetSlot.putStack(null);
				} else {
					targetSlot.putStack(player.inventory.getItemStack() == null ? null : player.inventory.getItemStack().copy());
				}
				return player.inventory.getItemStack();
			}
		}
		return super.slotClick(slotID, buttonID, flag, player);
	}

	@SideOnly(Side.CLIENT)
	public void putStacksInSlots(ItemStack[] items) {
		for (int i = 0; i < items.length; ++i) {
			if (i < (state == 0 ? 45 : 37))
				this.getSlot(i).putStack(items[i]);
		}
	}
}
