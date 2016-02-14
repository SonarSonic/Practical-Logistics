package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.energy.StoredEnergyStack;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.api.wrappers.EnergyWrapper;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyHelper extends EnergyWrapper {

	public List<StoredEnergyInfo> getEnergyList(List<BlockCoords> coords) {
		List<StoredEnergyInfo> energyList = new ArrayList();
		List<EnergyHandler> handlers = Logistics.energyProviders.getObjects();
		for (BlockCoords coord : coords) {
			TileEntity target = coord.getTileEntity();
			if (target != null && target instanceof IConnectionNode) {
				IConnectionNode node = (IConnectionNode) target;
				Map<BlockCoords, ForgeDirection> connections = node.getConnections();
				for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
					TileEntity energyTile = entry.getKey().getTileEntity(target.getWorldObj());
					if (energyTile != null) {
						StoredEnergyStack stack = new StoredEnergyStack();
						for (EnergyHandler handler : handlers) {
							handler.getEnergyInfo(stack, energyTile, entry.getValue());
						}
						IdentifiedCoords iCoords = new IdentifiedCoords("", SonarHelper.createStackedBlock(energyTile.getBlockType(), energyTile.getBlockMetadata()), entry.getKey());
						energyList.add(StoredEnergyInfo.createInfo(iCoords, stack));
					}
				}
			}
		}
		return energyList;
	}
}
