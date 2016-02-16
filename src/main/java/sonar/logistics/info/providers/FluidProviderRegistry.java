package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.info.providers.fluids.AE2ExternalFluidProvider;
import sonar.logistics.info.providers.fluids.AE2FluidProvider;
import sonar.logistics.info.providers.fluids.TankProvider;
import cpw.mods.fml.common.Loader;

public class FluidProviderRegistry extends RegistryHelper<FluidHandler> {

	@Override
	public void register() {
		if (Loader.isModLoaded("appliedenergistics2")) {
			registerObject(new AE2ExternalFluidProvider());
			registerObject(new AE2FluidProvider());
		}
		registerObject(new TankProvider());
	}

	@Override
	public String registeryType() {
		return "Fluid Provider";
	}
}