package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.utils.SortingDirection;
import sonar.logistics.api.settings.EnergyReader.SortingType;
import sonar.logistics.api.wrappers.EnergyWrapper;
import sonar.logistics.connections.monitoring.MonitoredEnergyStack;

public class EnergyHelper extends EnergyWrapper {

	public ArrayList<ISonarEnergyHandler> getProviders(EnergyType type) {
		ArrayList<ISonarEnergyHandler> providers = new ArrayList();
		List<ISonarEnergyHandler> handlers = SonarCore.energyHandlers;
		for (ISonarEnergyHandler provider : handlers) {
			if (provider.getProvidedType().getName().equals(type.getName())) {
				providers.add(provider);
			}
		}
		return providers;
	}

	public static void sortItemList(ArrayList<MonitoredEnergyStack> info, final SortingDirection dir, SortingType type) {
		info.sort(new Comparator<MonitoredEnergyStack>() {
			public int compare(MonitoredEnergyStack str1, MonitoredEnergyStack str2) {
				StoredEnergyStack item1 = str1.energyStack.getObject(), item2 = str2.energyStack.getObject();
				switch (type) {
				case CAPACITY:
					return InfoHelper.compareWithDirection(item1.capacity, item2.capacity, dir);
				case INPUT:
					return InfoHelper.compareWithDirection(item1.input, item2.input, dir);
				case NAME:
					String modid1 = str1.coords.getMonitoredInfo().unlocalizedName.getObject();
					String modid2 = str2.coords.getMonitoredInfo().unlocalizedName.getObject();
					return InfoHelper.compareStringsWithDirection(modid1, modid2, dir);
				case STORED:
					return InfoHelper.compareWithDirection(item1.stored, item2.stored, dir);
				case TYPE:
					return InfoHelper.compareStringsWithDirection(item1.energyType.getName(), item2.energyType.getName(), dir);
				}
				return 0;
			}
		});
	}
}
