package sonar.logistics.registries;

import sonar.core.utils.helpers.NBTRegistryHelper;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.info.filters.items.OreDictionaryFilter;

public class ItemFilterRegistry extends NBTRegistryHelper.Buf<ItemFilter> {

	@Override
	public void register() {
		registerObject(new ItemStackFilter());
		registerObject(new OreDictionaryFilter());
	}

	@Override
	public String registeryType() {
		return "Item Filter";
	}
/*
	@Override
	public boolean equalTypes(ItemFilter target, ItemFilter current) {
		if (target == null && current != null) {
			return false;
		}
		if (current == null) {
			return false;
		}
		return current.equalFilter(target);
	}
	*/

	@Override
	public boolean areTypesEqual(ItemFilter target, ItemFilter current) {
		return target.equalFilter(current);
	}
}