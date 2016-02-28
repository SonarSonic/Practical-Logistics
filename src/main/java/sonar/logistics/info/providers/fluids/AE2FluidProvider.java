package sonar.logistics.info.providers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.integration.AE2Helper;
import appeng.api.AEApi;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import cpw.mods.fml.common.Loader;

public class AE2FluidProvider extends FluidHandler {

	public static String name = "AE2-Fluids";

	@Override
	public String getName() {
		return name;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public boolean canHandleFluids(TileEntity tile, ForgeDirection dir) {
		return tile instanceof IGridProxyable;
	}

	@Override
	public StorageSize getFluids(List<StoredFluidStack> storedStacks, TileEntity tile, ForgeDirection dir) {
		long maxStorage = 0;
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			IItemList<IAEFluidStack> fluids = storage.getFluidInventory().getStorageList();
			if (fluids == null) {
				return StorageSize.EMPTY;
			}
			for (IAEFluidStack fluid : fluids) {
				LogisticsAPI.getFluidHelper().addFluidToList(storedStacks, AE2Helper.convertAEFluidStack(fluid));
				maxStorage += fluid.getStackSize();
			}
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return new StorageSize(maxStorage, maxStorage);
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			IAEFluidStack fluid = storage.getFluidInventory().injectItems(AE2Helper.convertStoredFluidStack(add), AE2Helper.getActionable(action), new MachineSource(((IActionHost) tile)));
			if (fluid == null || fluid.getStackSize() == 0) {
				return null;
			}
			return AE2Helper.convertAEFluidStack(fluid);
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir, ActionType action) {
		IGridProxyable proxy = (IGridProxyable) tile;
		try {
			IStorageGrid storage = proxy.getProxy().getStorage();
			StoredFluidStack fluid = LogisticsAPI.getFluidHelper().getStackToAdd(remove.stored, remove, AE2Helper.convertAEItemStack(storage.getFluidInventory().extractItems(AE2Helper.convertStoredFluidStack(remove), AE2Helper.getActionable(action), new MachineSource(((IActionHost) tile)))));
			if (fluid == null || fluid.stored == 0) {
				return null;
			}
			return fluid;
		} catch (GridAccessException e) {
			e.printStackTrace();
		}
		return remove;
	}

}
