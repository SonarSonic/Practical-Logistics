package sonar.logistics.info.providers;

import sonar.core.utils.helpers.RegistryHelper;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.info.providers.energy.EUProvider;
import sonar.logistics.info.providers.energy.MekanismProvider;
import sonar.logistics.info.providers.energy.RFHandler;

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