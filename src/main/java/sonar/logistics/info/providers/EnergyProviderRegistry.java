package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.info.providers.energy.EUProvider;
import sonar.logistics.info.providers.energy.MekanismProvider;
import sonar.logistics.info.providers.energy.RFHandler;
import sonar.logistics.info.providers.fluids.AE2FluidProvider;
import sonar.logistics.info.providers.fluids.TankProvider;
import cpw.mods.fml.common.Loader;

public class EnergyProviderRegistry extends RegistryHelper<EnergyHandler> {

	@Override
	public void register() {
		registerObject(new MekanismProvider());
		registerObject(new EUProvider());
		registerObject(new RFHandler());
	}

	@Override
	public String registeryType() {
		return "Energy Provider";
	}
}