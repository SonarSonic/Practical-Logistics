package sonar.logistics.info.registries;

import com.google.common.collect.Lists;

import mekanism.api.IEvaporationSolar;
import mekanism.api.IHeatTransfer;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.reactor.IFusionReactor;
import mekanism.api.reactor.IReactorBlock;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;

@InfoRegistry(modid = "Mekanism")
public class MekanismInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseReturns() {
		LogicInfoRegistry.registerReturn(IFusionReactor.class);
	}	

	@Override
	public void registerBaseMethods() {
		LogicInfoRegistry.registerMethods(IHeatTransfer.class, RegistryType.TILE, Lists.newArrayList("getTemp", "getInverseConductionCoefficient", "getInsulationCoefficient", "canConnectHeat"));
		LogicInfoRegistry.registerMethods(IEvaporationSolar.class, RegistryType.TILE, Lists.newArrayList("seesSun"));
		LogicInfoRegistry.registerMethods(IEvaporationSolar.class, RegistryType.TILE, Lists.newArrayList("seesSun"));
		LogicInfoRegistry.registerMethods(ILaserReceptor.class, RegistryType.TILE, Lists.newArrayList("canLasersDig"));
		LogicInfoRegistry.registerMethods(IReactorBlock.class, RegistryType.TILE, Lists.newArrayList("getReactor"));
		LogicInfoRegistry.registerMethods(IFusionReactor.class, RegistryType.TILE, Lists.newArrayList("isBurning", "isFormed", "getCaseTemp", "getPlasmaTemp", "getInjectionRate"));
	}

}
