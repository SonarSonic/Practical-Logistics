package sonar.logistics.info.providers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.logistics.api.providers.FluidProvider;
import appeng.api.implementations.tiles.ITileStorageMonitorable;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IStorageMonitorable;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IItemList;
import cpw.mods.fml.common.Loader;

public class AE2FluidProvider extends FluidProvider {

	public static String name = "AE2-Fluids";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideFluids(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null && tile instanceof ITileStorageMonitorable && tile instanceof IActionHost;
	}

	@Override
	public void getFluids(List<StoredFluidStack> fluids, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
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

}
