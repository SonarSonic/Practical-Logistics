package sonar.logistics.common.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.inventory.slots.SlotList;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.settings.FluidReader.Modes;
import sonar.logistics.common.multiparts.FluidReaderPart;

public class ContainerFluidReader extends ContainerMultipartSync {

	private static final int INV_START = 1, INV_END = INV_START + 26, HOTBAR_START = INV_END + 1, HOTBAR_END = HOTBAR_START + 8;
	public boolean stackMode = false;
	FluidReaderPart part;

	public ContainerFluidReader(FluidReaderPart part, EntityPlayer player) {
		super(part);
		this.part = part;
		addSlots(part, player, part.setting.getObject() == Modes.SELECTED);
	}

	public void addSlots(FluidReaderPart handler, EntityPlayer player, boolean hasStack) {
		stackMode = hasStack;
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
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(par2);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (stackMode && par2 >= INV_START) {
				if (!part.getWorld().isRemote) {
					ItemStack copy = itemstack1.copy();
					//FIXME
					if (copy != null && copy.getItem() instanceof IFluidContainerItem) {
						IFluidContainerItem container = (IFluidContainerItem) copy.getItem();
						FluidStack stack = container.getFluid(copy);
						if (stack != null) {
							//part.current = stack;
						}
					} else if (copy != null) {
						FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(copy);
						if (fluid != null) {
							//part.current = fluid;
						}
					}
				}
				if (!this.mergeItemStack(itemstack1.copy(), 0, INV_START, false)) {
					return null;
				}
			} else if (par2 >= INV_START && par2 < HOTBAR_START) {
				if (!this.mergeItemStack(itemstack1, HOTBAR_START, HOTBAR_END + 1, false)) {
					return null;
				}
			} else if (par2 >= HOTBAR_START && par2 < HOTBAR_END + 1) {
				if (!this.mergeItemStack(itemstack1, INV_START, INV_END + 1, false)) {
					return null;
				}
			}
			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}

    public ItemStack slotClick(int slotID, int drag, ClickType click, EntityPlayer player){
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

	public SyncType[] getSyncTypes() {
		return new SyncType[] { SyncType.SPECIAL };
	}

	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		part.getViewersList().removeViewer(player, MonitorType.INFO);
	}

}
