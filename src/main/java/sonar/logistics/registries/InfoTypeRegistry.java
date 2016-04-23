package sonar.logistics.registries;

import sonar.core.helpers.NBTRegistryHelper;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.BlockNameInfo;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.FluidInventoryInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.info.types.ModidInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredEnergyInfo;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.info.types.ThaumcraftAspectInfo;

public class InfoTypeRegistry extends NBTRegistryHelper.Buf<ILogicInfo> {

	@Override
	public void register() {
		registerObject(new BlockCoordsInfo());
		registerObject(new CategoryInfo());
		registerObject(new LogicInfo());
		registerObject(new StoredStackInfo());
		registerObject(new ProgressInfo());
		registerObject(new FluidInfo());
		registerObject(new ThaumcraftAspectInfo());
		registerObject(new ManaInfo());
		registerObject(new FluidStackInfo());
		registerObject(new InventoryInfo());
		registerObject(new StoredEnergyInfo());
		registerObject(new FluidInventoryInfo());
		registerObject(new BlockNameInfo());
		registerObject(new ModidInfo());

	}

	@Override
	public String registeryType() {
		return "Info Type";
	}
	
	@Override
	public boolean areTypesEqual(ILogicInfo target, ILogicInfo current) {
		if (target == null && current == null) {
			return true;
		}
		if (target == null || current == null) {
			return false;
		}
		return target.checkInfoTypes(current) && target.equals(current);
	}
}