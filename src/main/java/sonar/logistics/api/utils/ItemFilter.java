package sonar.logistics.api.utils;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTObject;
import sonar.logistics.api.LogisticsAPI;

/** used for filtering Item Router */
public abstract class ItemFilter<T extends ItemFilter> implements INBTObject, IBufObject {

	public int getID() {
		return LogisticsAPI.getRegistry().getItemFilterID(getName());
	}

	/** the name the info helper will be registered too */
	public abstract String getName();

	public abstract boolean matchesFilter(ItemStack stack);

	public abstract boolean equalFilter(ItemFilter stack);

	public abstract void writeToNBT(NBTTagCompound tag);

	public abstract void readFromNBT(NBTTagCompound tag);

	public abstract T instance();

	public abstract List<ItemStack> getFilters();

	/**
	 * used when the provider is loaded normally used to check if relevant mods
	 * are loaded for APIs to work
	 */
	public boolean isLoadable() {
		return true;
	}

}
