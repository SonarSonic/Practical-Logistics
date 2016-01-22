package sonar.logistics.registries;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.info.filters.items.OreDictionaryFilter;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.info.types.ThaumcraftAspectInfo;

public class ItemFilterRegistry extends RegistryHelper<ItemFilter> {

	@Override
	public void register() {
		registerObject(new ItemStackFilter());
		registerObject(new OreDictionaryFilter());
	}

	@Override
	public String registeryType() {
		return "Item Filter";
	}
}