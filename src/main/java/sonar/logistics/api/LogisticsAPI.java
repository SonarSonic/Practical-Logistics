package sonar.logistics.api;

import sonar.logistics.api.wrappers.CablingWrapper;
import sonar.logistics.api.wrappers.EnergyWrapper;
import sonar.logistics.api.wrappers.FluidWrapper;
import sonar.logistics.api.wrappers.InfoWrapper;
import sonar.logistics.api.wrappers.ItemWrapper;
import sonar.logistics.api.wrappers.RegistryWrapper;
import sonar.logistics.api.wrappers.RenderWrapper;
import cpw.mods.fml.common.Loader;

/**Use this for all your interaction with the mod.
 * This will be initilized by Practical Logistics if it is loaded. Make sure you only register stuff once Practical Logistics is loaded therefore in the FMLPostInitializationEvent*/
public final class LogisticsAPI {

	public static final String MODID = "PracticalLogistics";
	public static final String NAME = "PracticalLogisticsAPI";	
	public static final String VERSION = "1.2";
	
	private static RegistryWrapper registry = new RegistryWrapper();	
	private static CablingWrapper cables = new CablingWrapper();
	private static EnergyWrapper energy = new EnergyWrapper();
	private static FluidWrapper fluids = new FluidWrapper();
	private static ItemWrapper items = new ItemWrapper();
	private static InfoWrapper info = new InfoWrapper();
	private static RenderWrapper renderer = new RenderWrapper();

	public static void init() {
		if (Loader.isModLoaded("PracticalLogistics")) {
			try {
				registry = (RegistryWrapper) Class.forName("sonar.logistics.LogisticsRegistry").newInstance();
				cables = (CablingWrapper) Class.forName("sonar.logistics.helpers.CableHelper").newInstance();
				energy = (EnergyWrapper) Class.forName("sonar.logistics.helpers.EnergyHelper").newInstance();
				fluids = (FluidWrapper) Class.forName("sonar.logistics.helpers.FluidHelper").newInstance();
				items = (ItemWrapper) Class.forName("sonar.logistics.helpers.ItemHelper").newInstance();
				info = (InfoWrapper) Class.forName("sonar.logistics.helpers.InfoHelper").newInstance();
				renderer = (RenderWrapper) Class.forName("sonar.logistics.helpers.InfoRenderer").newInstance();
			} catch (Exception exception) {
				System.err.println("Practical Logistics API : FAILED TO INITILISE API" + exception.getMessage());
			}
		}
	}

	public static RegistryWrapper getRegistry() {
		return registry;
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
	public static InfoWrapper getInfoHelper() {
		return info;
	}
	public static RenderWrapper getInfoRenderer() {
		return renderer;
	}
}
