package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;

import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.logistics.api.wrappers.EnergyWrapper;
public class EnergyHelper extends EnergyWrapper {
	
	public ArrayList<EnergyHandler> getProviders(EnergyType type) {
		ArrayList<EnergyHandler> providers = new ArrayList();
		List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
		for (EnergyHandler provider : handlers) {
			if (provider.getProvidedType().getName().equals(type.getName())) {
				providers.add(provider);
			}
		}
		return providers;
	}
}
