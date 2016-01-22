package sonar.logistics.info.filters.items;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.ItemFilter;

public class OreDictionaryFilter extends ItemFilter<OreDictionaryFilter> {

	String oreDict = " ";

	@Override
	public String getName() {
		return "OreDict - Filter";
	}

	@Override
	public boolean matchesFilter(ItemStack stack) {
		if (oreDict != null && stack != null) {
			List<ItemStack> ores = OreDictionary.getOres(oreDict);
			for (ItemStack item : ores) {
				if (item.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(item, stack)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean equalFilter(ItemFilter itemFilter) {
		if (itemFilter != null && itemFilter instanceof OreDictionaryFilter) {
			OreDictionaryFilter oFilter = (OreDictionaryFilter) itemFilter;
			if (oFilter.oreDict.equals(oreDict)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("ore", oreDict);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		oreDict = tag.getString("ore");
	}

	@Override
	public OreDictionaryFilter instance() {
		return new OreDictionaryFilter();
	}

}
