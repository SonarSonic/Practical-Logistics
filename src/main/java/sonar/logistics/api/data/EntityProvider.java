package sonar.logistics.api.data;

import java.util.List;

import net.minecraft.entity.Entity;
import sonar.logistics.api.Info;
import sonar.logistics.info.providers.entity.EntityProviderRegistry;

/** used for providing information on Block/TileEntity for the Info Reader to read, the Provider must be registered in the PractialLogisticsAPI to be used */
public abstract class EntityProvider {

	public byte getID() {
		return EntityProviderRegistry.getProviderID(helperName());
	}

	/** the name the info helper will be registered too */
	public abstract String helperName();

	/**
	 * can provide info on the given entity
	 * 
	 * @param entity
	 * @return
	 */
	public abstract boolean canProvideInfo(Entity entity);

	/**
	 * gets all the available info from this provider
	 * 
	 * @param entity
	 * @return
	 */
	public abstract void getHelperInfo(List<Info> infoList, Entity entity);

	public abstract String getCategory(byte id);

	public abstract String getSubCategory(byte id);

	/** used when the provider is loaded normally used to check if relevant mods are loaded for APIs to work */
	public boolean isLoadable() {
		return true;
	}
}
