package sonar.logistics.connections.monitoring;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.ISonarEnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.MonitorHandler;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;

@MonitorHandler(handlerID = EnergyMonitorHandler.id, modid = Logistics.MODID)
public class EnergyMonitorHandler extends LogicMonitorHandler<MonitoredEnergyStack> {

	public static final String id = "energy";

	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<MonitoredEnergyStack> updateInfo(INetworkCache network, MonitoredList<MonitoredEnergyStack> previousList, BlockCoords coords, EnumFacing side) {
		MonitoredList<MonitoredEnergyStack> list = MonitoredList.<MonitoredEnergyStack>newMonitoredList(network.getNetworkID());
		List<ISonarEnergyHandler> providers = SonarCore.energyHandlers;
		for (ISonarEnergyHandler provider : providers) {
			TileEntity tile = coords.getTileEntity();
			if (tile != null && provider.canProvideEnergy(tile, side)) {
				StoredEnergyStack info = provider.getEnergy(new StoredEnergyStack(provider.getProvidedType()), tile, side);
				if (info != null)
					list.addInfoToList(new MonitoredEnergyStack(info, new MonitoredBlockCoords(coords, coords.getBlock().getUnlocalizedName())), previousList);

			}
		}
		return null;
	}
}
