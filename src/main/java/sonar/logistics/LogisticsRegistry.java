package sonar.logistics;

import sonar.logistics.api.Info;
import sonar.logistics.api.RegistryWrapper;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidProvider;
import sonar.logistics.api.providers.InventoryProvider;
import sonar.logistics.api.providers.TileProvider;
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

	public void registerInventoryProvider(InventoryProvider provider) {
		Logistics.inventoryProviders.registerObject(provider);
	}

	public void registerFluidProvider(FluidProvider provider) {
		Logistics.fluidProviders.registerObject(provider);
	}

	public void registerForgingHammerRecipe(Object... objects) {
		HammerRecipes.instance().addRecipe(objects);
	}
}
