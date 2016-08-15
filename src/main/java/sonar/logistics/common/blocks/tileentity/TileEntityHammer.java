package sonar.logistics.common.blocks.tileentity;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.inventory.SonarInventory;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.utils.HammerRecipes;

public class TileEntityHammer extends TileEntityInventory implements ISidedInventory, IByteBufTile {

	public SyncTagType.INT progress = new SyncTagType.INT(0);
	public SyncTagType.INT coolDown = new SyncTagType.INT(1);
	public static int speed = 100;

	public TileEntityHammer() {
		syncParts.addAll(Lists.newArrayList(progress, coolDown));
		super.inv = new SonarInventory(this, 2);
	}

	public void update() {
		super.update();
		if (this.worldObj.isBlockIndirectlyGettingPowered(pos) == 15) {
			return;
		}
		if (coolDown.getObject() != 0) {
			coolDown.increaseBy(-1);
			// if (!this.worldObj.isRemote && coolDown.getObject() == 1)
			// SonarCore.sendPacketAround(this, 64, 1);
			// SonarCore.sendFullSyncAround(this, 64);
		} else if (canProcess()) {
			if (progress.getObject() < speed) {
				progress.increaseBy(1);
				// if (!this.worldObj.isRemote && progress.getObject() == 1)
				// SonarCore.sendPacketAround(this, 64, 0);
			} else {
				coolDown.setObject(speed * 2);
				progress.setObject(0);
				if (!this.worldObj.isRemote) {
					finishProcess();
					markBlockForUpdate();
				}
			}
			// SonarCore.sendPacketAround(this, 64, 0);
			// SonarCore.sendFullSyncAround(this, 64);
		} else {
			if (progress.getObject() != 0) {
				this.progress.setObject(0);
				// SonarCore.sendPacketAround(this, 64, 0);
			}

		}
	}

	public boolean canProcess() {
		if (slots()[0] == null) {
			return false;
		}
		ItemStack[] output = HammerRecipes.instance().getOutput(slots()[0]);
		if (output == null || output.length != 1) {
			return false;
		}
		if (output[0] == null) {
			return false;
		} else if (slots()[1] != null) {
			if (!slots()[1].isItemEqual(output[0])) {
				return false;
			} else if (slots()[1].stackSize + output[0].stackSize > slots()[1].getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	public void finishProcess() {
		ItemStack[] output = HammerRecipes.instance().getOutput(slots()[0]);
		if (output != null && output[0] != null) {
			if (this.slots()[1] == null) {
				this.slots()[1] = output[0].copy();
			} else if (this.slots()[1].isItemEqual(output[0])) {
				this.slots()[1].stackSize += output[0].stackSize;
			}
			this.slots()[0].stackSize -= HammerRecipes.instance().getInputSize(0, output);
			if (this.slots()[0].stackSize <= 0) {
				this.slots()[0] = null;
			}
		}

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (slot == 0 && HammerRecipes.instance().validInput(stack)) {
			return true;
		}
		return false;

	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[] { 0, 1 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, EnumFacing side) {
		return slot == 0;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return slot == 1;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		super.setInventorySlotContents(i, itemstack);
		if (i == 1) {
			markBlockForUpdate();
			// SonarCore.sendFullSyncAround(this, 64);
		}
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			progress.writeToBuf(buf);
			break;
		case 1:
			coolDown.writeToBuf(buf);
			break;
		}

	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			progress.readFromBuf(buf);
			break;
		case 1:
			coolDown.readFromBuf(buf);
			break;
		}
	}

	public int getSpeed() {
		return speed;
	}

	public int getProgress() {
		return progress.getObject();
	}

	public int getCoolDown() {
		return coolDown.getObject();
	}

	public int getCoolDownSpeed() {
		return speed * 2;
	}
}
