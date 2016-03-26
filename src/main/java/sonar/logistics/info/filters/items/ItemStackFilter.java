package sonar.logistics.info.filters.items;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.ItemStackHelper;
import sonar.logistics.api.utils.ItemFilter;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ItemStackFilter extends ItemFilter<ItemStackFilter> implements IInventory {

	public ItemStack[] filters = new ItemStack[1];
	public boolean matchNBT, ignoreDamage, matchOreDict, matchModid;

	@Override
	public String getName() {
		return "ItemStack Filter";
	}

	@Override
	public boolean matchesFilter(ItemStack stack) {
		int slotID = 0;
		if (stack != null && filters[slotID] != null) {
			if (stack.getItem() == filters[slotID].getItem() || (matchModid && ItemStackHelper.matchingModid(filters[slotID], stack)) || (matchOreDict && ItemStackHelper.matchingOreDictID(filters[slotID], stack))) {
				if (ignoreDamage || stack.getItemDamage() == filters[slotID].getItemDamage()) {
					if (!matchNBT || ItemStack.areItemStackTagsEqual(stack, filters[slotID])) {
						return true;
					}

				}
			}
		}
		return false;
	}

	public boolean equalFilter(ItemFilter itemFilter) {
		if (itemFilter != null && itemFilter instanceof ItemStackFilter) {
			ItemStackFilter sFilter = (ItemStackFilter) itemFilter;
			if (filters[0] != null && sFilter.matchNBT == matchNBT && sFilter.ignoreDamage == ignoreDamage && sFilter.matchOreDict == matchOreDict && sFilter.matchModid == matchModid && ItemStack.areItemStacksEqual(filters[0], sFilter.filters[0])) {
				return true;
			}
		}
		return false;

	}

	@Override
	public List<ItemStack> getFilters() {
		return Arrays.asList(filters);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("matchNBT", matchNBT);
		tag.setBoolean("ignoreDamage", ignoreDamage);
		tag.setBoolean("matchOreDict", matchOreDict);
		tag.setBoolean("matchModid", matchModid);

		NBTTagCompound stackTag = new NBTTagCompound();
		if (filters[0] != null) {
			filters[0].writeToNBT(stackTag);
			tag.setTag("stackTag", stackTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		matchNBT = tag.getBoolean("matchNBT");
		ignoreDamage = tag.getBoolean("ignoreDamage");
		matchOreDict = tag.getBoolean("matchOreDict");
		matchModid = tag.getBoolean("matchModid");

		if (tag.hasKey("stackTag"))
			filters[0] = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stackTag"));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		matchNBT = buf.readBoolean();
		ignoreDamage = buf.readBoolean();
		matchOreDict = buf.readBoolean();
		matchModid = buf.readBoolean();

		if (buf.readBoolean()) {
			filters[0] = ByteBufUtils.readItemStack(buf);
		}
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeBoolean(matchNBT);
		buf.writeBoolean(ignoreDamage);
		buf.writeBoolean(matchOreDict);
		buf.writeBoolean(matchModid);

		if (filters[0] != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeItemStack(buf, filters[0]);
		} else {
			buf.writeBoolean(false);
		}
	}

	@Override
	public ItemStackFilter instance() {
		return new ItemStackFilter();
	}

	@Override
	public int getSizeInventory() {
		return filters.length;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return filters[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		filters[slot] = null;
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (stack != null) {
			ItemStack filterStack = stack.copy();
			filterStack.stackSize = 1;
			filters[slot] = filterStack;
		} else {
			filters[slot] = null;
		}
	}

	@Override
	public String getInventoryName() {
		return "ItemStack Filter";
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

}
