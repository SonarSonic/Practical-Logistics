package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.EnergyType;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.SonarHelper;
import sonar.logistics.api.cache.INetworkCache;
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
