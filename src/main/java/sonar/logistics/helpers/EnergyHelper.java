package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.providers.EnergyProvider;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.utils.IdentifiedCoords;
import sonar.logistics.api.utils.StoredEnergyStack;
import sonar.logistics.api.wrappers.EnergyWrapper;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyHelper extends EnergyWrapper {

	public List<StoredEnergyInfo> getEnergyList(INetworkCache network) {
		List<StoredEnergyInfo> energyList = new ArrayList();
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity energyTile = entry.getKey().getTileEntity();
			if (energyTile != null) {
				for (EnergyType type : Logistics.energyTypes.getObjects()) {
					StoredEnergyStack stack = new StoredEnergyStack(type);
					boolean provided = false;
					for (EnergyProvider handler : getProviders(type)) {
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

	public List<EnergyProvider> getProviders(EnergyType type) {
		List<EnergyProvider> providers = new ArrayList();
		List<EnergyProvider> handlers = Logistics.energyProviders.getObjects();
		for (EnergyProvider provider : handlers) {
			if (provider.getProvidedType().getName().equals(type.getName())) {
				providers.add(provider);
			}
		}
		return providers;
	}
}
