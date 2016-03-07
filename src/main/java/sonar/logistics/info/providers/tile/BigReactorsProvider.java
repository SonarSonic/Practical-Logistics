package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.info.Info;
import sonar.logistics.api.info.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import cpw.mods.fml.common.Loader;
import erogenousbeef.bigreactors.common.interfaces.IReactorFuelInfo;
import erogenousbeef.bigreactors.common.multiblock.MultiblockReactor;
import erogenousbeef.bigreactors.common.multiblock.MultiblockTurbine;
import erogenousbeef.bigreactors.common.multiblock.interfaces.IActivateable;
import erogenousbeef.core.multiblock.IMultiblockPart;
import erogenousbeef.core.multiblock.MultiblockControllerBase;

public class BigReactorsProvider extends TileProvider {

	public static String name = "Big-Reactors";
	public String[] categories = new String[] { "Big Reactor General", "Big Reactor Fuel" };
	public String[] subcategories = new String[] { "Connected Blocks", "Is Active", "Energy Generated", "Core Heat", "Casing Heat", "Fluid Consumed", "Intake Rate", "Max Intake Rate", "Rotor Speed", "Max Rotor Speed", "Rotor Mass", "Rotor Blades", "Current Fuel", "Current Waste", "Capacity", "Fuel Rods" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && target instanceof IMultiblockPart;
	}

	@Override
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IMultiblockPart) {
			IMultiblockPart part = (IMultiblockPart) target;
			MultiblockControllerBase controller = part.getMultiblockController();			
			infoList.add(new StandardInfo(id, 0, 0, controller.getNumConnectedBlocks()));
			
			if(controller instanceof IActivateable){
				IActivateable activate = (IActivateable) controller;
				infoList.add(new StandardInfo(id, 0, 1, activate.getActive()));
			}
			
			if(controller instanceof MultiblockReactor){
				MultiblockReactor reactor = (MultiblockReactor) controller;
				infoList.add(new StandardInfo(id, 0, 2, (long)reactor.getEnergyGeneratedLastTick()).addSuffix("RF"));
				infoList.add(new StandardInfo(id, 1, 3, (long)reactor.getFuelHeat()).addSuffix("C"));
				infoList.add(new StandardInfo(id, 0, 4, (long)reactor.getReactorHeat()).addSuffix("C"));
				
			}
			
			if(controller instanceof MultiblockTurbine){
				MultiblockTurbine turbine = (MultiblockTurbine) controller;
				infoList.add(new StandardInfo(id, 0, 2, (long)turbine.getEnergyGeneratedLastTick()).addSuffix("RF"));
				infoList.add(new StandardInfo(id, 0, 5, turbine.getFluidConsumedLastTick()).addSuffix("mB"));
				infoList.add(new StandardInfo(id, 0, 6, turbine.getMaxIntakeRate()).addSuffix("mB/T"));
				infoList.add(new StandardInfo(id, 0, 7, turbine.getMaxIntakeRateMax()).addSuffix("mB/T"));
				infoList.add(new StandardInfo(id, 0, 8, (long)turbine.getRotorSpeed()).addSuffix("RPM"));
				infoList.add(new StandardInfo(id, 0, 9, (long)turbine.getMaxRotorSpeed()).addSuffix("RPM"));
				infoList.add(new StandardInfo(id, 0, 10, turbine.getRotorMass()));
				infoList.add(new StandardInfo(id, 0, 11, turbine.getNumRotorBlades()));
			}
			
			if(controller instanceof IReactorFuelInfo){
				IReactorFuelInfo fuelInfo = (IReactorFuelInfo) controller;
				infoList.add(new StandardInfo(id, 1, 12, fuelInfo.getFuelAmount()).addSuffix("mB"));
				infoList.add(new StandardInfo(id, 1, 13, fuelInfo.getWasteAmount()).addSuffix("mB"));
				infoList.add(new StandardInfo(id, 1, 14, fuelInfo.getCapacity()).addSuffix("mB"));
				infoList.add(new StandardInfo(id, 1, 15, fuelInfo.getFuelRodCount()));
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

	public boolean isLoadable() {
		return Loader.isModLoaded("BigReactors");
	}
}
