package sonar.logistics.info.providers.inventory;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.integration.AE2Helper;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IExternalStorageHandler;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.common.Loader;

public class AE2ExternalItemProvider extends InventoryHandler {

	public static String name = "AE2-External-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, ForgeDirection dir) {
		IExternalStorageHandler handler = AEApi.instance().registries().externalStorage().getHandler(tile, dir, StorageChannel.ITEMS, AE2Helper.sourceHandler);

		return handler != null;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.ITEMS);
		if (inv == null) {
			return null;
		}
		IItemList<IAEItemStack> items = inv.getAvailableItems(AEApi.instance().storage().createItemList());
		if (items == null) {
			return null;
		}
		int current = 0;
		for (IAEItemStack item : items) {
			if (current == slot) {
				return new StoredItemStack(item.getItemStack(), item.getStackSize());
			}
			current++;
		}

		return null;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.ITEMS);
		if (inv == null) {
			return false;
		}
		IItemList<IAEItemStack> items = inv.getAvailableItems(AEApi.instance().storage().createItemList());
		if (items == null) {
			return false;
		}
		for (IAEItemStack item : items) {
			LogisticsAPI.getItemHelper().addStackToList(storedStacks, AE2Helper.convertAEItemStack(item));
		}
		return true;
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.ITEMS);
		if (inv == null) {
			return add;
		}
		return AE2Helper.convertAEItemStack(inv.injectItems(AE2Helper.convertStoredItemStack(add), Actionable.MODULATE, AE2Helper.sourceHandler));
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.ITEMS);
		if (inv == null) {
			return remove;
		}
		return AE2Helper.convertAEItemStack(inv.extractItems(AE2Helper.convertStoredItemStack(remove), Actionable.MODULATE, AE2Helper.sourceHandler));
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

}
