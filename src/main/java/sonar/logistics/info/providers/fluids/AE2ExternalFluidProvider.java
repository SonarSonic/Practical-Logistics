package sonar.logistics.info.providers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.FluidHandler;
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
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.common.Loader;

public class AE2ExternalFluidProvider extends FluidHandler {

	public static String name = "AE2-External-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleFluids(TileEntity tile, ForgeDirection dir) {
		IExternalStorageHandler handler = AEApi.instance().registries().externalStorage().getHandler(tile, dir, StorageChannel.FLUIDS, AE2Helper.sourceHandler);
		return handler != null;
	}

	@Override
	public boolean getFluids(List<StoredFluidStack> fluids, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.FLUIDS);
		if (inv == null) {
			return false;
		}
		IItemList<IAEFluidStack> items = inv.getAvailableItems(AEApi.instance().storage().createFluidList());
		if (items == null) {
			return false;
		}
		for (IAEFluidStack item : items) {
			LogisticsAPI.getFluidHelper().addFluidToList(fluids, AE2Helper.convertAEFluidStack(item));
		}
		return true;
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.FLUIDS);
		if (inv == null) {
			return add;
		}
		return AE2Helper.convertAEFluidStack(inv.injectItems(AE2Helper.convertStoredFluidStack(add), Actionable.MODULATE, AE2Helper.sourceHandler));
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir) {
		IMEInventory inv = AE2Helper.getMEInventory(tile, dir, StorageChannel.FLUIDS);
		if (inv == null) {
			return remove;
		}
		return AE2Helper.convertAEFluidStack(inv.extractItems(AE2Helper.convertStoredFluidStack(remove), Actionable.MODULATE, AE2Helper.sourceHandler));
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

}
