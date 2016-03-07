package sonar.logistics;

import sonar.logistics.api.info.Info;
import sonar.logistics.api.providers.EnergyProvider;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.api.utils.EnergyType;
import sonar.logistics.api.wrappers.RegistryWrapper;
import sonar.logistics.utils.HammerRecipes;

public class LogisticsRegistry extends RegistryWrapper {

	public void registerInfoType(Info type) {
		Logistics.infoTypes.registerObject(type);
	}
	
	public void registerEnergyType(EnergyType type) {
		Logistics.energyTypes.registerObject(type);
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

	public void registerEnergyHandler(EnergyProvider provider) {
		Logistics.energyProviders.registerObject(provider);
	}

	public void registerForgingHammerRecipe(Object... objects) {
		HammerRecipes.instance().addRecipe(objects);
	}
	
	public EnergyType getEnergyType(String storage) {
		return Logistics.energyTypes.getEnergyType(storage);
	}
	
	public TileProvider getTileProvider(int id) {
		return Logistics.tileProviders.getRegisteredObject(id);
	}

	public EntityProvider getEntityProvider(int id) {
		return Logistics.entityProviders.getRegisteredObject(id);
	}

	public InventoryHandler getInventoryHandler(int id) {
		return Logistics.inventoryProviders.getRegisteredObject(id);
	}

	public FluidHandler getFluidHandler(int id) {
		return Logistics.fluidProviders.getRegisteredObject(id);
	}

	public EnergyProvider getEnergyHandler(int id) {
		return Logistics.energyProviders.getRegisteredObject(id);
	}

	public int getTileProviderID(String name) {
		return Logistics.tileProviders.getObjectID(name);
	}

	public int getEntityProviderID(String name) {
		return Logistics.entityProviders.getObjectID(name);
	}

	public int getInventorHandlerID(String name) {
		return Logistics.inventoryProviders.getObjectID(name);
	}

	public int getFluidHandlerID(String name) {
		return Logistics.fluidProviders.getObjectID(name);
	}

	public int getEnergyHandlerID(String name) {
		return Logistics.energyProviders.getObjectID(name);
	}

	public int getItemFilterID(String name) {
		return Logistics.itemFilters.getObjectID(name);
	}
		
}
