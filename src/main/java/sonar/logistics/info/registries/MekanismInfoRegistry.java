package sonar.logistics.info.registries;

import com.google.common.collect.Lists;

import mekanism.api.IEvaporationSolar;
import mekanism.api.IHeatTransfer;
import mekanism.api.lasers.ILaserReceptor;
import mekanism.api.reactor.IFusionReactor;
import mekanism.api.reactor.IReactorBlock;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.LogicRegistry.RegistryType;

@InfoRegistry(modid = "Mekanism")
public class MekanismInfoRegistry extends IInfoRegistry {

	@Override
	public void registerBaseReturns() {
		LogicRegistry.registerReturn(IFusionReactor.class);
	}	

	@Override
	public void registerBaseMethods() {
		LogicRegistry.registerMethods(IHeatTransfer.class, RegistryType.TILE, Lists.newArrayList("getTemp", "getInverseConductionCoefficient", "getInsulationCoefficient", "canConnectHeat"));
		LogicRegistry.registerMethods(IEvaporationSolar.class, RegistryType.TILE, Lists.newArrayList("seesSun"));
		LogicRegistry.registerMethods(IEvaporationSolar.class, RegistryType.TILE, Lists.newArrayList("seesSun"));
		LogicRegistry.registerMethods(ILaserReceptor.class, RegistryType.TILE, Lists.newArrayList("canLasersDig"));
		LogicRegistry.registerMethods(IReactorBlock.class, RegistryType.TILE, Lists.newArrayList("getReactor"));
		LogicRegistry.registerMethods(IFusionReactor.class, RegistryType.TILE, Lists.newArrayList("isBurning", "isFormed", "getCaseTemp", "getPlasmaTemp", "getInjectionRate"));
	}

}
