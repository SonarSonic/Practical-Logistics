package sonar.logistics.monitoring;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.StorageSize;
import sonar.core.api.inventories.InventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.MonitorHelper;

public class ItemMonitorHandler extends MonitorHandler<MonitoredItemStack> {

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public String getName() {
		return MonitorHandler.ITEMS;
	}

	@Override
	public MonitoredList<MonitoredItemStack> updateInfo(MonitoredList<MonitoredItemStack> previousList, BlockCoords coords, EnumFacing side) {
		MonitoredList<MonitoredItemStack> list = MonitoredList.<MonitoredItemStack>newMonitoredList();
		List<InventoryHandler> providers = SonarCore.inventoryProviders.getObjects();
		for (InventoryHandler provider : providers) {
			TileEntity tile = coords.getTileEntity();
			if (tile != null && provider.canHandleItems(tile, side)) {
				List<StoredItemStack> info = new ArrayList();
				StorageSize size = provider.getItems(info, tile, side);
				for (StoredItemStack item : info) {
					MonitorHelper.<MonitoredItemStack>addInfoToList(list, this, new MonitoredItemStack(item));
				}
				break;
			}
		}
		return list;
	}

	@Override
	public MonitoredItemStack readInfo(NBTTagCompound tag, SyncType type) {
		return new MonitoredItemStack(StoredItemStack.readFromNBT(tag));
	}

	@Override
	public NBTTagCompound writeInfo(MonitoredItemStack info, NBTTagCompound tag, SyncType type) {
		if (info.item == null) {
			return tag;
		}
		return info.writeToNBT(tag, info);
	}

	@Override
	public boolean validateInfo(IMonitorInfo info) {
		return info instanceof MonitoredItemStack;
	}

}
