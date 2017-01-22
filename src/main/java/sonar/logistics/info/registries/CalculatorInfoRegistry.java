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
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;

@InfoRegistry(modid = "calculator")
public class CalculatorInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseMethods() {
		LogicInfoRegistry.registerMethods(IHealthProcessor.class, RegistryType.TILE);
		LogicInfoRegistry.registerMethods(IHungerProcessor.class, RegistryType.TILE);
		LogicInfoRegistry.registerMethods(IProcessMachine.class, RegistryType.TILE);
		LogicInfoRegistry.registerMethods(IGreenhouse.class, RegistryType.TILE, Lists.newArrayList("getState"), true);
		LogicInfoRegistry.registerMethods(ITeleport.class, RegistryType.TILE, Lists.newArrayList("getCoords"), true);
		LogicInfoRegistry.registerMethods(IFlawlessGreenhouse.class, RegistryType.TILE, Lists.newArrayList("getPlantsHarvested", "getPlantsGrown"));
		//LogicInfoRegistry.registerMethods(ICalculatorGenerator.class, RegistryType.TILE, Lists.newArrayList("getItemLevel", "getMaxItemLevel"));
	}

	@Override
	public void registerAdjustments() {
		LogicInfoRegistry.registerInfoAdjustments(Lists.newArrayList("IHealthProcessor.getHealthPoints", "IHungerProcessor.getHungerPoints"), "", "points");
		LogicInfoRegistry.registerInfoAdjustments(Lists.newArrayList("IProcessMachine.getCurrentProcessTime", "IProcessMachine.getProcessTime", "IProcessMachine.getBaseProcessTime"), "", "ticks");
	}

}
