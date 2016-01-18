package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.info.providers.fluids.AE2FluidProvider;
import sonar.logistics.info.providers.fluids.TankProvider;

public class FluidProviderRegistry extends RegistryHelper<FluidProvider> {

	@Override
	public void register() {
		registerObject(new TankProvider());
		registerObject(new AE2FluidProvider());
	}

	@Override
	public String registeryType() {
		return "Fluid Provider";
	}
}