package sonar.logistics.info.providers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.logistics.api.providers.FluidHandler;
import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.common.Loader;

public class AE2FluidProvider extends FluidHandler {

	public static String name = "AE2-Fluids";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleFluids(TileEntity tile, ForgeDirection dir) {
		return tile instanceof ITileStorageMonitorable && tile instanceof IActionHost;
	}

	@Override
	public void getFluids(List<StoredFluidStack> fluids, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof ITileStorageMonitorable && tile instanceof IActionHost) {
			IStorageMonitorable monitor = ((ITileStorageMonitorable) tile).getMonitorable(dir, new MachineSource(((IActionHost) tile)));
			if (monitor != null) {
				IMEMonitor<IAEFluidStack> stacks = monitor.getFluidInventory();
				if (stacks != null) {
					IItemList<IAEFluidStack> fluidStacks = stacks.getStorageList();
					for (IAEFluidStack item : fluidStacks) {
						fluids.add(new StoredFluidStack(item.getFluidStack(), item.getStackSize(), item.getStackSize()));
					}
				}
			}

		}
	}

	@Override
	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir) {
		IStorageMonitorable monitor = ((ITileStorageMonitorable) tile).getMonitorable(dir, new MachineSource(((IActionHost) tile)));
		if (monitor != null) {
			IMEMonitor<IAEFluidStack> stacks = monitor.getFluidInventory();
			IItemList<IAEFluidStack> fluidStacks = stacks.getStorageList();

			IAEFluidStack stack = stacks.injectItems(AEApi.instance().storage().createFluidStack(add.fluid).setStackSize(add.stored), Actionable.MODULATE, new MachineSource(((IActionHost) tile)));
			if (stack == null || stack.getStackSize() == 0) {
				return null;
			}
			return new StoredFluidStack(stack.getFluidStack(), stack.getStackSize());
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir) {
		IStorageMonitorable monitor = ((ITileStorageMonitorable) tile).getMonitorable(dir, new MachineSource(((IActionHost) tile)));
		if (monitor != null) {
			IMEMonitor<IAEFluidStack> stacks = monitor.getFluidInventory();
			IItemList<IAEFluidStack> fluidStacks = stacks.getStorageList();
			IAEFluidStack stack = stacks.extractItems(AEApi.instance().storage().createFluidStack(remove.fluid).setStackSize(remove.stored), Actionable.MODULATE, new MachineSource(((IActionHost) tile)));
			if (stack.getStackSize() == 0) {
				return remove;
			}
			return new StoredFluidStack(stack.getFluidStack(), remove.stored - stack.getStackSize());
		}
		return remove;
	}

}
