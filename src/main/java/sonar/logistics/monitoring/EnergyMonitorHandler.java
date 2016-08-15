package sonar.logistics.monitoring;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.MonitorHelper;

public class EnergyMonitorHandler extends MonitorHandler<MonitoredEnergyStack> {

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public String getName() {
		return MonitorHandler.ENERGY;
	}

	@Override
	public MonitoredList<MonitoredEnergyStack> updateInfo(MonitoredList<MonitoredEnergyStack> previousList, BlockCoords coords, EnumFacing side) {
		MonitoredList<MonitoredEnergyStack> list = MonitoredList.<MonitoredEnergyStack>newMonitoredList();
		List<EnergyHandler> providers = SonarCore.energyProviders.getObjects();
		for (EnergyHandler provider : providers) {
			TileEntity tile = coords.getTileEntity();
			if (tile != null && provider.canProvideEnergy(tile, side)) {
				StoredEnergyStack info = provider.getEnergy(new StoredEnergyStack(provider.getProvidedType()), tile, side);
				if (info != null)
					MonitorHelper.<MonitoredEnergyStack>addInfoToList(list, this, new MonitoredEnergyStack(info));

			}
		}
		return null;
	}

	@Override
	public MonitoredEnergyStack readInfo(NBTTagCompound tag, SyncType type) {
		return new MonitoredEnergyStack(StoredEnergyStack.readFromNBT(tag));
	}

	@Override
	public NBTTagCompound writeInfo(MonitoredEnergyStack info, NBTTagCompound tag, SyncType type) {
		return info.writeToNBT(tag, info);
	}

	@Override
	public boolean validateInfo(IMonitorInfo info) {
		return info instanceof MonitoredEnergyStack;
	}

}
