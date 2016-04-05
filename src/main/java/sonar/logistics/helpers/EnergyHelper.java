package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.api.EnergyHandler;
import sonar.core.api.EnergyType;
import sonar.core.api.StoredEnergyStack;
import sonar.core.helpers.SonarHelper;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.utils.IdentifiedCoords;
import sonar.logistics.api.wrappers.EnergyWrapper;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyHelper extends EnergyWrapper {

	public List<StoredEnergyInfo> getEnergyList(INetworkCache network) {
		List<StoredEnergyInfo> energyList = new ArrayList();
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity energyTile = entry.getKey().getTileEntity();
			if (energyTile != null) {
				for (EnergyType type : SonarCore.energyTypes.getObjects()) {
					StoredEnergyStack stack = new StoredEnergyStack(type);
					boolean provided = false;
					for (EnergyHandler handler : getProviders(type)) {
						if (handler.canProvideEnergy(energyTile, entry.getValue())) {
							handler.getEnergy(stack, energyTile, entry.getValue());
							provided = true;
						}
					}
					if (provided) {
						IdentifiedCoords iCoords = new IdentifiedCoords("", SonarHelper.createStackedBlock(energyTile.getBlockType(), energyTile.getBlockMetadata()), entry.getKey());
						energyList.add(StoredEnergyInfo.createInfo(iCoords, stack));
						break;
					}
				}
			}
		}
		return energyList;
	}

	public List<EnergyHandler> getProviders(EnergyType type) {
		List<EnergyHandler> providers = new ArrayList();
		List<EnergyHandler> handlers = SonarCore.energyProviders.getObjects();
		for (EnergyHandler provider : handlers) {
			if (provider.getProvidedType().getName().equals(type.getName())) {
				providers.add(provider);
			}
		}
		return providers;
	}
}
