package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.info.types.FluidInfo;

public class TankProvider extends TileProvider {

	public static String name = "Tank-Helper";
	public String[] categories = new String[] { "TANK" };
	public String[] subcategories = new String[] {"Capacity","Stored","Fluid Type"};

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IFluidTank || target instanceof IFluidHandler);
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IFluidTank) {
			IFluidTank tank = (IFluidTank) target;
			int capacity = tank.getCapacity();
			int currentStored = tank.getFluidAmount();
			infoList.add(new StandardInfo(id, 0, 0, tank.getCapacity()).addSuffix("MB"));
			infoList.add(new StandardInfo(id, 0, 1, tank.getFluidAmount()).addSuffix("MB"));
			if (tank.getFluid() != null) {
				FluidStack fluid = tank.getFluid();
				infoList.add(new StandardInfo(id, 0, 2, fluid.getLocalizedName()));

			}
		} else if (target instanceof IFluidHandler) {
			IFluidHandler handler = (IFluidHandler) target;
			FluidTankInfo[] info = handler.getTankInfo(dir);
			if (info != null) {
				int tankNumber = 0;
				for (FluidTankInfo tankInfo : info) {
					tankNumber++;
					String prefix = "ID " + tankNumber + ": ";
					if (info.length == 1) {
						prefix = "";
					}
					if (tankInfo.fluid != null) {
						int fluidID = tankInfo.fluid.getFluidID();
						infoList.add(new FluidInfo(id, "TANK", prefix + "Capacity", tankInfo.capacity, fluidID).addSuffix("MB"));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Stored", tankInfo.fluid.amount, fluidID).addSuffix("MB"));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Fluid ID", tankInfo.fluid.getFluidID(), fluidID));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Fluid Name", tankInfo.fluid.getLocalizedName(), fluidID));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Temperature", tankInfo.fluid.getFluid().getTemperature(), fluidID));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Density", tankInfo.fluid.getFluid().getDensity(), fluidID));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Viscosity", tankInfo.fluid.getFluid().getViscosity(), fluidID));
					} else {
						infoList.add(new FluidInfo(id, "TANK", prefix + "Capacity", tankInfo.capacity, -1).addSuffix("MB"));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Stored", 0, -1).addSuffix("MB"));
						infoList.add(new FluidInfo(id, "TANK", prefix + "Fluid Name", "NO FLUID", -1));
					}
				}
			}
		}
	}

	@Override
	public String getCategory(int id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(int id) {
		return subcategories[id];
	}
}
