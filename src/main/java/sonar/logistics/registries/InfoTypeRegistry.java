package sonar.logistics.registries;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.info.types.ThaumcraftAspectInfo;

public class InfoTypeRegistry extends RegistryHelper<Info> {

	@Override
	public void register() {
		registerObject(new BlockCoordsInfo());
		registerObject(new CategoryInfo());
		registerObject(new StandardInfo());
		registerObject(new EntityInfo());
		registerObject(new StoredStackInfo());
		registerObject(new ProgressInfo());
		registerObject(new FluidInfo());
		registerObject(new ThaumcraftAspectInfo());
		registerObject(new ManaInfo());
		registerObject(new FluidStackInfo());
		
	}

	@Override
	public String registeryType() {
		return "Info Type";
	}
}