package sonar.logistics.common.containers;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.inventory.ContainerSync;
import sonar.core.inventory.StoredItemStack;
import sonar.core.inventory.slots.SlotList;
import sonar.core.network.PacketStackUpdate;
import sonar.core.utils.ActionType;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.common.containers.slots.NetworkSlot;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class ContainerInventoryReader extends ContainerSync {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public boolean stackMode = false;
	public ItemStack lastStack = null;
	InventoryReaderHandler handler;

	public ContainerInventoryReader(InventoryReaderHandler handler, TileEntity entity, InventoryPlayer inventoryPlayer) {
		super(handler, entity);
		addSlots(handler, inventoryPlayer, handler.setting.getObject() == 0);
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
				if (!tile.getWorldObj().isRemote) {
					StoredItemStack stack = new StoredItemStack(itemstack);
					if (lastStack!=null && ItemStack.areItemStackTagsEqual(itemstack1,lastStack) && lastStack.isItemEqual(itemstack1))
						handler.insertInventory(player, tile, slot.getSlotIndex());
					else {
						StoredItemStack perform = LogisticsAPI.getItemHelper().addItems(stack, handler.getNetwork(tile), ActionType.PERFORM);
						lastStack = itemstack1;
						if (perform == null || perform.stored == 0) {
							itemstack1.stackSize = 0;
						} else {
							itemstack1.stackSize = (int) (perform.getStackSize());
						}
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

	public ItemStack slotClick(int slotID, int buttonID, int flag, EntityPlayer player) {
		if (slotID < this.inventorySlots.size()) {
			Slot targetSlot = slotID < 0 ? null : (Slot) this.inventorySlots.get(slotID);
			if (targetSlot instanceof NetworkSlot) {
				NetworkSlot slot = (NetworkSlot) targetSlot;
				if (!tile.getWorldObj().isRemote) {
					
					List<BlockCoords> network = LogisticsAPI.getCableHelper().getConnections(tile, ForgeDirection.getOrientation(FMPHelper.getMeta(tile)).getOpposite());
					
					if (flag == 1) {
						StoredItemStack stack = slot.getStoredStack();

						if (stack == null || stack.stored == 0) {
							return null;
						}
						int extractSize = (int) Math.min(buttonID == 1 ? Integer.MAX_VALUE : stack.getItemStack().getMaxStackSize(), Math.min(stack.stored, buttonID == 1 ? stack.stored : 64));

						StoredItemStack simulate = LogisticsAPI.getItemHelper().removeToPlayerInventory(stack.copy().setStackSize(extractSize), extractSize, network, player, ActionType.SIMULATE);
						if (simulate != null) {
							StoredItemStack storedStack = LogisticsAPI.getItemHelper().getStackToAdd(simulate.stored, stack, LogisticsAPI.getItemHelper().removeItems(simulate, network, ActionType.PERFORM));
							
							if (storedStack != null && storedStack.stored != 0) {
								LogisticsAPI.getItemHelper().addStackToPlayer(storedStack, player, false, ActionType.PERFORM);
								this.detectAndSendChanges();
								return null;
							}
							
						}
					} else  if (player.inventory.getItemStack() != null) {
						ItemStack add = player.inventory.getItemStack();
						int stackSize = Math.min(buttonID == 1 ? 1 : 64, add.stackSize);
						StoredItemStack stack = LogisticsAPI.getItemHelper().addItems(new StoredItemStack(add.copy()).setStackSize(stackSize), network, ActionType.PERFORM);

						if (stack == null || stack.stored == 0) {
							add.stackSize = add.stackSize - stackSize;
						} else {
							add.stackSize = (int) (add.stackSize - (stackSize - stack.stored));
						}
						if (add.stackSize <= 0) {
							add = null;
						}
						if (!ItemStack.areItemStacksEqual(add, player.inventory.getItemStack())) {
							player.inventory.setItemStack(add);
							SonarCore.network.sendTo(new PacketStackUpdate(add), (EntityPlayerMP) player);

						}
						return add;
					} else if (player.inventory.getItemStack() == null) {
						StoredItemStack stack = slot.getStoredStack();

						if (stack == null || stack.stored == 0) {
							return null;
						}
						int extractSize = (int) Math.min(stack.getItemStack().getMaxStackSize(), Math.min(stack.stored, buttonID == 1 ? 1 : 64));
						StoredItemStack storedStack = LogisticsAPI.getItemHelper().getStackToAdd(extractSize, stack, LogisticsAPI.getItemHelper().removeItems(stack.copy().setStackSize(extractSize), network, ActionType.PERFORM));
						if (storedStack != null && storedStack.stored != 0) {
							player.inventory.setItemStack(storedStack.getFullStack());
							SonarCore.network.sendTo(new PacketStackUpdate(storedStack.getFullStack()), (EntityPlayerMP) player);
							this.detectAndSendChanges();
						}
						return storedStack.getFullStack();
					}
				}
				return null;

			} else if ((targetSlot instanceof SlotList)) {
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
