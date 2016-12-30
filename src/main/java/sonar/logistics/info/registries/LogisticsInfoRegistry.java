package sonar.logistics.info.registries;

import com.google.common.collect.Lists;

import sonar.logistics.Logistics;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;

@InfoRegistry(modid = Logistics.MODID)
public class LogisticsInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseMethods() {
		LogicInfoRegistry.registerMethods(TileEntityHammer.class, RegistryType.TILE, Lists.newArrayList("getSpeed", "getProgress", "getCoolDown", "getCoolDownSpeed"));
	}

	@Override
	public void registerAdjustments() {
		LogicInfoRegistry.registerInfoAdjustments(Lists.newArrayList("TileEntityHammer.getSpeed", "TileEntityHammer.getProgress", "TileEntityHammer.getCoolDown", "TileEntityHammer.getCoolDownSpeed"), "", "ticks");
		LogicInfoRegistry.registerInfoAdjustments("item.storage", "", "items");
		
	}
}
