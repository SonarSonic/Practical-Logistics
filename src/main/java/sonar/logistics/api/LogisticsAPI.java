package sonar.logistics.api;

import net.minecraftforge.fml.common.Loader;
import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.api.wrappers.EnergyWrapper;
import sonar.logistics.api.wrappers.FluidWrapper;
import sonar.logistics.api.wrappers.ItemWrapper;

/** Use this for all your interaction with the mod. This will be initilized by Practical Logistics if it is loaded. Make sure you only register stuff once Practical Logistics is loaded therefore in the FMLPostInitializationEvent */
public final class LogisticsAPI {

	public static final String MODID = "practicallogistics";
	public static final String NAME = "practicallogisticsapi";
	public static final String VERSION = "1.4";

	private static CablingWrapper cables = new CablingWrapper();
	private static EnergyWrapper energy = new EnergyWrapper();
	private static FluidWrapper fluids = new FluidWrapper();
	private static ItemWrapper items = new ItemWrapper();

	public static void init() {
		if (Loader.isModLoaded("practicallogistics") || Loader.isModLoaded("PracticalLogistics")) {
			try {
				cables = (CablingWrapper) Class.forName("sonar.logistics.helpers.CableHelper").newInstance();
				energy = (EnergyWrapper) Class.forName("sonar.logistics.helpers.EnergyHelper").newInstance();
				fluids = (FluidWrapper) Class.forName("sonar.logistics.helpers.FluidHelper").newInstance();
				items = (ItemWrapper) Class.forName("sonar.logistics.helpers.ItemHelper").newInstance();
			} catch (Exception exception) {
				System.err.println("Practical Logistics API : FAILED TO INITILISE API" + exception.getMessage());
			}
		}
	}

	public static CablingWrapper getCableHelper() {
		return cables;
	}

	public static EnergyWrapper getEnergyHelper() {
		return energy;
	}

	public static FluidWrapper getFluidHelper() {
		return fluids;
	}

	public static ItemWrapper getItemHelper() {
		return items;
	}
}
