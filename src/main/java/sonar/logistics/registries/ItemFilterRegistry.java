package sonar.logistics.registries;

import sonar.core.helpers.NBTRegistryHelper;
import sonar.logistics.api.utils.ItemFilter;
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
	
	@Override
	public boolean areTypesEqual(ItemFilter target, ItemFilter current) {
		return target.equalFilter(current);
	}
}