package sonar.logistics.registries;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.helpers.NBTRegistryHelper;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.ManaInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.info.types.ThaumcraftAspectInfo;

public class InfoTypeRegistry extends NBTRegistryHelper.Buf<Info> {

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
		registerObject(new InventoryInfo());

	}

	@Override
	public String registeryType() {
		return "Info Type";
	}

	@Override
	public boolean areTypesEqual(Info target, Info current) {
		if (target == null && current == null) {
			return true;
		}
		if (target == null || current == null) {
			return false;
		}
		return target.areTypesEqual(current) && target.equals(current);
	}
}