package sonar.logistics.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.SonarCore;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.network.sync.SyncInt;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.utils.HammerRecipes;

public class TileEntityHammer extends TileEntityInventory implements ISidedInventory {

	public SyncInt progress = new SyncInt(0);
	public SyncInt coolDown = new SyncInt(1);
	public static int speed = 100;

	public TileEntityHammer() {
		super.slots = new ItemStack[2];
	}

	public void updateEntity() {
		if (isClient()) {
			return;
		}
		if (this.worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord)) {
			return;
		}
		if (coolDown.getInt() != 0) {
			coolDown.increaseBy(-1);
			SonarCore.sendFullSyncAround(this, 64);
		} else if (canProcess()) {
			if (progress.getInt() < speed) {
				progress.increaseBy(1);
			} else {
				finishProcess();
				this.coolDown.setInt(speed * 2);
				progress.setInt(0);
				this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			}
			SonarCore.sendFullSyncAround(this, 64);
		} else {
			this.coolDown.setInt(this.progress.getInt() * 2);
			this.progress.setInt(0);
		}
	}

	public boolean canProcess() {
		if (slots[0] == null) {
			return false;
		}
		ItemStack[] output = HammerRecipes.instance().getOutput(slots[0]);
		if (output == null || output.length != 1) {
			return false;
		}
		if (output[0] == null) {
			return false;
		} else if (slots[1] != null) {
			if (!slots[1].isItemEqual(output[0])) {
				return false;
			} else if (slots[1].stackSize + output[0].stackSize > slots[1].getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	public void finishProcess() {
		ItemStack[] output = HammerRecipes.instance().getOutput(slots[0]);
		if (output != null && output[0] != null) {
			if (this.slots[1] == null) {
				this.slots[1] = output[0].copy();
			} else if (this.slots[1].isItemEqual(output[0])) {
				this.slots[1].stackSize += output[0].stackSize;
			}
			this.slots[0].stackSize -= HammerRecipes.instance().getInputSize(0, output);
			if (this.slots[0].stackSize <= 0) {
				this.slots[0] = null;
			}
		}

	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			this.progress.readFromNBT(nbt, type);
			this.coolDown.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			this.progress.writeToNBT(nbt, type);
			this.coolDown.writeToNBT(nbt, type);
		}
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (slot == 0) {
			if (HammerRecipes.instance().validInput(stack)) {
				return true;
			}
		}
		return false;

	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return slot == 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return slot == 1;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		super.setInventorySlotContents(i, itemstack);
		if (i == 1) {
			SonarCore.sendFullSyncAround(this, 64);
		}
	}
}
