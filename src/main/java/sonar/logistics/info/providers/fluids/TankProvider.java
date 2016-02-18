package sonar.logistics.info.providers.fluids;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.ActionType;
import sonar.logistics.api.providers.FluidHandler;

public class TankProvider extends FluidHandler {

	public static String name = "Tank-Inventory";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleFluids(TileEntity tile, ForgeDirection dir) {
		return (tile instanceof IFluidHandler);
	}

	@Override
	public boolean getFluids(List<StoredFluidStack> fluids, TileEntity tile, ForgeDirection dir) {
		if (tile instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) tile;
			FluidTankInfo[] tankInfo = handler.getTankInfo(dir);
			if (tankInfo != null) {
				int tankNumber = 0;
				for (FluidTankInfo info : tankInfo) {
					if (info != null && info.fluid != null && info.fluid.amount != 0) {
						fluids.add(new StoredFluidStack(info.fluid, info.capacity));
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) tile;
			if (handler.canFill(dir, add.fluid.getFluid())) {
				int used = handler.fill(dir, add.getFullStack(), !action.shouldSimulate());
				add.stored -= used;
				if (add.stored == 0) {
					return null;
				}
			}
		}
		return add;
	}

	@Override
	public StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir, ActionType action) {
		if (tile instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) tile;
			if (handler.canDrain(dir, remove.fluid.getFluid())) {
				FluidStack used = handler.drain(dir, remove.getFullStack(), !action.shouldSimulate());
				if (used != null) {
					remove.stored -= used.amount;
					if (remove.stored == 0) {
						return null;
					}
				}
			}
		}
		return remove;
	}
}
