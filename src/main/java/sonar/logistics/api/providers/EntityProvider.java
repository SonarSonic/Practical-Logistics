package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.IRegistryObject;
import sonar.core.api.SonarProvider;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.info.ILogicInfo;

/** used for providing information on Entities for the Info Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class EntityProvider extends SonarProvider implements ICategoryProvider {

	public int getID() {
		return LogisticsAPI.getRegistry().getEntityProviderID(getName());
	}

	/** can provide info on the given entity
	 * @param entity the {@link Entity} to check */
	public abstract boolean canProvideInfo(Entity entity);

	/** gets all the available info from this provider
	 * @param infoList current list of info for the given entity, providers only add to this and don't remove.
	 * @param entity the {@link Entity} to check */
	public abstract void getHelperInfo(List<ILogicInfo> infoList, Entity entity);
}
