package sonar.logistics;

import sonar.logistics.api.Info;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.api.wrappers.RegistryWrapper;
import sonar.logistics.utils.HammerRecipes;

public class LogisticsRegistry extends RegistryWrapper {

	public void registerInfoType(Info provider) {
		Logistics.infoTypes.registerObject(provider);
	}

	public void registerTileProvider(TileProvider provider) {
		Logistics.tileProviders.registerObject(provider);
	}

	public void registerEntityProvider(EntityProvider provider) {
		Logistics.entityProviders.registerObject(provider);
	}

	public void registerInventoryHandler(InventoryHandler provider) {
		Logistics.inventoryProviders.registerObject(provider);
	}

	public void registerFluidHandler(FluidHandler provider) {
		Logistics.fluidProviders.registerObject(provider);
	}

	public void registerForgingHammerRecipe(Object... objects) {
		HammerRecipes.instance().addRecipe(objects);
	}
}
