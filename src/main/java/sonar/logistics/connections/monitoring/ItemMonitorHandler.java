package sonar.logistics.connections.monitoring;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.InventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.MonitorHandler;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;

@MonitorHandler(handlerID = ItemMonitorHandler.id, modid = Logistics.MODID)
public class ItemMonitorHandler extends LogicMonitorHandler<MonitoredItemStack> {

	public static final String id = "item";
	
	@Override
	public String id() {
		return id;
	}

	@Override
	public MonitoredList<MonitoredItemStack> updateInfo(INetworkCache network, MonitoredList<MonitoredItemStack> previousList, BlockCoords coords, EnumFacing side) {
		MonitoredList<MonitoredItemStack> list = MonitoredList.<MonitoredItemStack>newMonitoredList(network.getNetworkID());
		List<InventoryHandler> providers = SonarCore.inventoryProviders.getObjects();
		TileEntity tile = coords.getTileEntity();
		if (tile != null) {
			for (InventoryHandler provider : providers) {
				if (provider.canHandleItems(tile, side)) {
					List<StoredItemStack> info = new ArrayList();
					StorageSize size = provider.getItems(info, tile, side);
					list.sizing.add(size);
					for (StoredItemStack item : info) {
						list.addInfoToList(new MonitoredItemStack(item));
					}
					break;
				}
			}
		}
		return list;
	}
}
