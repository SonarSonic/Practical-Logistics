package sonar.logistics;

import sonar.core.api.EnergyHandler;
import sonar.core.api.EnergyType;
import sonar.core.api.FluidHandler;
import sonar.core.api.InventoryHandler;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.api.render.InfoInteractionHandler;
import sonar.logistics.api.wrappers.RegistryWrapper;
import sonar.logistics.utils.HammerRecipes;

public class LogisticsRegistry extends RegistryWrapper {

	public void registerInfoType(ILogicInfo info) {
		Logistics.infoTypes.registerObject(info);
	}
	
	public void registerInteractionHandler(InfoInteractionHandler handler) {
		Logistics.infoInteraction.registerObject(handler);
	}
	
	public void registerTileProvider(TileProvider provider) {
		Logistics.tileProviders.registerObject(provider);
	}

	public void registerEntityProvider(EntityProvider provider) {
		Logistics.entityProviders.registerObject(provider);
	}

	public void registerForgingHammerRecipe(Object... objects) {
		HammerRecipes.instance().addRecipe(objects);
	}
	
	public TileProvider getTileProvider(int id) {
		return Logistics.tileProviders.getRegisteredObject(id);
	}

	public EntityProvider getEntityProvider(int id) {
		return Logistics.entityProviders.getRegisteredObject(id);
	}

	public int getTileProviderID(String name) {
		return Logistics.tileProviders.getObjectID(name);
	}

	public int getEntityProviderID(String name) {
		return Logistics.entityProviders.getObjectID(name);
	}
	public int getItemFilterID(String name) {
		return Logistics.itemFilters.getObjectID(name);
	}
		
}
