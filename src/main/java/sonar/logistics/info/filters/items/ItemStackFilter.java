package sonar.logistics.info.filters.items;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.ItemFilter;

public class ItemStackFilter extends ItemFilter<ItemStackFilter> {

	public ItemStack filter = null;
	public boolean matchNBT, ignoreDamage, matchOreDict;

	@Override
	public String getName() {
		return "ItemStack Filter";
	}

	@Override
	public boolean matchesFilter(ItemStack stack) {
		if (stack != null && filter != null) {
			if (stack.getItem() == filter.getItem()) {
				if (ignoreDamage || stack.getItemDamage() == filter.getItemDamage()) {
					if (!matchNBT || ItemStack.areItemStackTagsEqual(stack, filter)) {
						if (!matchOreDict || OreDictionary.itemMatches(stack, filter, false)) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean equalFilter(ItemFilter itemFilter) {
		if (itemFilter!=null && itemFilter instanceof ItemStackFilter) {
			ItemStackFilter sFilter = (ItemStackFilter) itemFilter;
			if (filter != null && sFilter.matchNBT == matchNBT && sFilter.ignoreDamage == ignoreDamage && sFilter.matchOreDict == matchOreDict && ItemStack.areItemStacksEqual(filter, sFilter.filter)) {
				return true;
			}
		}
		return false;

	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("matchNBT", matchNBT);
		tag.setBoolean("ignoreDamage", ignoreDamage);
		tag.setBoolean("matchOreDict", matchOreDict);
		NBTTagCompound stackTag = new NBTTagCompound();
		if (filter != null) {
			filter.writeToNBT(stackTag);
			tag.setTag("stackTag", stackTag);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		matchNBT = tag.getBoolean("matchNBT");
		ignoreDamage = tag.getBoolean("ignoreDamage");
		matchOreDict = tag.getBoolean("matchOreDict");
		if (tag.hasKey("stackTag"))
			filter = ItemStack.loadItemStackFromNBT(tag.getCompoundTag("stackTag"));
	}

	@Override
	public ItemStackFilter instance() {
		return new ItemStackFilter();
	}

}
