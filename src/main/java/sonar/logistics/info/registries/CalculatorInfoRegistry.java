package sonar.logistics.info.registries;

import com.google.common.collect.Lists;

import sonar.calculator.mod.api.machines.IFlawlessGreenhouse;
import sonar.calculator.mod.api.machines.IGreenhouse;
import sonar.calculator.mod.api.machines.ITeleport;
import sonar.calculator.mod.api.nutrition.IHealthProcessor;
import sonar.calculator.mod.api.nutrition.IHungerProcessor;
import sonar.core.api.machines.IProcessMachine;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.LogicRegistry.RegistryType;

@InfoRegistry(modid = "Calculator")
public class CalculatorInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseMethods() {
		LogicRegistry.registerMethods(IHealthProcessor.class, RegistryType.TILE);
		LogicRegistry.registerMethods(IHungerProcessor.class, RegistryType.TILE);
		LogicRegistry.registerMethods(IProcessMachine.class, RegistryType.TILE);
		LogicRegistry.registerMethods(IGreenhouse.class, RegistryType.TILE, Lists.newArrayList("getState"), true);
		LogicRegistry.registerMethods(ITeleport.class, RegistryType.TILE, Lists.newArrayList("getCoords"), true);
		LogicRegistry.registerMethods(IFlawlessGreenhouse.class, RegistryType.TILE, Lists.newArrayList("getPlantsHarvested", "getPlantsGrown"));
	}

	@Override
	public void registerAdjustments() {
		LogicRegistry.registerInfoAdjustments(Lists.newArrayList("IHealthProcessor.getHealthPoints", "IHungerProcessor.getHungerPoints"), "", "points");
		LogicRegistry.registerInfoAdjustments(Lists.newArrayList("IProcessMachine.getCurrentProcessTime", "IProcessMachine.getProcessTime", "IProcessMachine.getBaseProcessTime"), "", "ticks");
	}

}
