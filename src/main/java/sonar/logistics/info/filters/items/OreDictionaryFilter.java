package sonar.logistics.info.filters.items;

import io.netty.buffer.ByteBuf;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import sonar.logistics.api.ItemFilter;
import cpw.mods.fml.common.network.ByteBufUtils;

public class OreDictionaryFilter extends ItemFilter<OreDictionaryFilter> {

	public String oreDict = "";

	@Override
	public String getName() {
		return "OreDict Filter";
	}

	@Override
	public boolean matchesFilter(ItemStack stack) {

		if (oreDict != null && stack != null) {

			int[] names = OreDictionary.getOreIDs(stack);

			for (Integer id : names) {
				if (OreDictionary.getOreName(id).equals(oreDict)) {
					return true;
				}
			}

			// List<ItemStack> ores = OreDictionary.getOres(oreDict);
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
	public void readFromBuf(ByteBuf buf) {
		oreDict = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, oreDict);
	}

	@Override
	public OreDictionaryFilter instance() {
		return new OreDictionaryFilter();
	}

	@Override
	public List<ItemStack> getFilters() {
		if (oreDict != null)
			return OreDictionary.getOres(oreDict);
		return Collections.EMPTY_LIST;
	}

}
