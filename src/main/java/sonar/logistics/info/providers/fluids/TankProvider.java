package sonar.logistics.info.providers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import sonar.core.fluid.StoredFluidStack;
import sonar.logistics.api.providers.FluidProvider;

public class TankProvider extends FluidProvider {

	public static String name = "Tank-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideFluids(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null && (tile instanceof IFluidTank || tile instanceof IFluidHandler);
	}

	@Override
	public void getFluids(List<StoredFluidStack> fluids, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IFluidTank) {
			IFluidTank tank = (IFluidTank) tile;
			FluidTankInfo info = tank.getInfo();
			if (info != null && info.fluid != null) {
				fluids.add(new StoredFluidStack(info.fluid, info.capacity));
			}
		} else if (tile instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) tile;
			FluidTankInfo[] tankInfo = handler.getTankInfo(dir);
			if (tankInfo != null) {
				int tankNumber = 0;
				for (FluidTankInfo info : tankInfo) {
					if (info != null && info.fluid != null) {
						fluids.add(new StoredFluidStack(info.fluid, info.capacity));
					}
				}
			}
		}

	}
}
