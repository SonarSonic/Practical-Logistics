package sonar.logistics.integration;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import appeng.api.AEApi;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

public class AE2Helper {
	
	public static void addNetworkItems(TileEntity tile, ForgeDirection dir, List<StoredItemStack> storedStacks) {
		if (tile instanceof ITileStorageMonitorable && tile instanceof IActionHost) {
			IStorageMonitorable monitor = ((ITileStorageMonitorable) tile).getMonitorable(dir, new MachineSource(((IActionHost) tile)));
			if (monitor != null) {
				IMEMonitor<IAEItemStack> stacks = monitor.getItemInventory();
				IItemList<IAEItemStack> items = stacks.getAvailableItems(AEApi.instance().storage().createItemList());
				for (IAEItemStack item : items) {
					storedStacks.add(new StoredItemStack(item.getItemStack(), item.getStackSize()));
				}
			}
		}
	}
}
