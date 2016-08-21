package sonar.logistics.info.registries;

import com.google.common.collect.Lists;

import sonar.logistics.Logistics;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.common.blocks.tileentity.TileEntityHammer;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.LogicRegistry.RegistryType;

@InfoRegistry(modid = Logistics.MODID)
public class LogisticsInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseMethods() {
		LogicRegistry.registerMethods(TileEntityHammer.class, RegistryType.TILE, Lists.newArrayList("getSpeed", "getProgress", "getCoolDown", "getCoolDownSpeed"));
		
	}

}
