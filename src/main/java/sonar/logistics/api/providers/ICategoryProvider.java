package sonar.logistics.api.providers;

import sonar.core.utils.IRegistryObject;

/**implemented on Providers which can provide categorised info for the Info Reader*/
public interface ICategoryProvider extends IRegistryObject {

	public String getCategory(int id);

	public String getSubCategory(int id);
}
