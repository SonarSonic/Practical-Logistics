package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.entity.Entity;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.info.Info;

/** used for providing information on Entities for the Info Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class EntityProvider implements IRegistryObject {

	public int getID() {
		return LogisticsAPI.getRegistry().getEntityProviderID(getName());
	}

	/** the name the info helper will be registered too */
	public abstract String getName();

	/** can provide info on the given entity
	 * @param entity
	 * @return */
	public abstract boolean canProvideInfo(Entity entity);

	/** gets all the available info from this provider
	 * @param entity
	 * @return */
	public abstract void getHelperInfo(List<Info> infoList, Entity entity);

	public abstract String getCategory(int id);

	public abstract String getSubCategory(int id);

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}
}
