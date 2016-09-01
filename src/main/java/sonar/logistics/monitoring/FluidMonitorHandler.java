package sonar.logistics.monitoring;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.fluids.FluidHandler;
import sonar.core.api.fluids.StoredFluidStack;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;

public class FluidMonitorHandler extends MonitorHandler<MonitoredFluidStack> {

	@Override
	public String getName() {
		return MonitorHandler.FLUIDS;
	}

	@Override
	public MonitoredList<MonitoredFluidStack> updateInfo(MonitoredList<MonitoredFluidStack> previousList, BlockCoords coords, EnumFacing side) {
		MonitoredList<MonitoredFluidStack> list = MonitoredList.<MonitoredFluidStack>newMonitoredList();
		List<FluidHandler> providers = SonarCore.fluidProviders.getObjects();
		for (FluidHandler provider : providers) {
			TileEntity fluidTile = coords.getTileEntity();
			if (fluidTile != null && provider.canHandleFluids(fluidTile, side)) {
				List<StoredFluidStack> info = new ArrayList();
				StorageSize size = provider.getFluids(info, fluidTile, side);
				list.sizing.add(size);
				for (StoredFluidStack fluid : info) {
					list.addInfoToList(new MonitoredFluidStack(fluid));
				}
				break;
			}
		}
		return list;
	}

}
