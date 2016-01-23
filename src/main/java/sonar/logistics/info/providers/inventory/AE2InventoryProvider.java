package sonar.logistics.info.providers.inventory;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.providers.InventoryProvider;
import appeng.api.AEApi;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.common.Loader;

public class AE2InventoryProvider extends InventoryProvider {

	public static String name = "AE2-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideItems(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null && tile instanceof ITileStorageMonitorable && tile instanceof IActionHost;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof ITileStorageMonitorable && tile instanceof IActionHost) {
			IStorageMonitorable monitor = ((ITileStorageMonitorable) tile).getMonitorable(dir, new MachineSource(((IActionHost) tile)));
			if (monitor != null) {
				IMEMonitor<IAEItemStack> stacks = monitor.getItemInventory();
				IItemList<IAEItemStack> items = stacks.getAvailableItems(AEApi.instance().storage().createItemList());
				for (IAEItemStack item : items) {
					storedStacks.add(new StoredItemStack(item.getItemStack(), item.getStackSize()));
				}
				return true;
			}
		}
		return false;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

}
