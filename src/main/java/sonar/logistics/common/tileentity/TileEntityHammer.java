package sonar.logistics.common.tileentity;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.inventory.SonarInventory;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.recipes.ISonarRecipe;
import sonar.core.recipes.RecipeHelperV2;
import sonar.core.utils.IGuiTile;
import sonar.logistics.client.gui.GuiHammer;
import sonar.logistics.common.containers.ContainerHammer;
import sonar.logistics.utils.HammerRecipes;

public class TileEntityHammer extends TileEntityInventory implements ISidedInventory, IByteBufTile, IGuiTile {

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

		ISonarRecipe recipe = HammerRecipes.instance().getRecipeFromInputs(null, new Object[] { slots()[0] });
		if (recipe == null) {
			return false;
		}
		ItemStack outputStack = RecipeHelperV2.getItemStackFromList(recipe.outputs(), 0);
		if (outputStack == null) {
			return false;
		} else if (slots()[1] != null) {
			if (!slots()[1].isItemEqual(outputStack)) {
				return false;
			} else if (slots()[1].stackSize + outputStack.stackSize > slots()[1].getMaxStackSize()) {
				return false;
			}
		}

		return true;
	}

	public void finishProcess() {
		ISonarRecipe recipe = HammerRecipes.instance().getRecipeFromInputs(null, new Object[] { slots()[0] });
		if (recipe == null) {
			return;
		}
		ItemStack outputStack = RecipeHelperV2.getItemStackFromList(recipe.outputs(), 0);
		if (outputStack != null && outputStack != null) {
			if (this.slots()[1] == null) {
				this.slots()[1] = outputStack.copy();
			} else if (this.slots()[1].isItemEqual(outputStack)) {
				this.slots()[1].stackSize += outputStack.stackSize;
			}
			this.slots()[0].stackSize -= recipe.inputs().get(0).getStackSize();
			if (this.slots()[0].stackSize <= 0) {
				this.slots()[0] = null;
			}
		}

	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (slot == 0 && HammerRecipes.instance().isValidInput(stack)) {
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

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerHammer(player, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiHammer(player, this);
	}
}
