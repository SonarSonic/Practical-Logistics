package sonar.logistics.api.cache;

import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;

/**implemented on {@link INetworkCache}s which can provide Items or Fluids*/
public interface IStorageCache extends INetworkCache {

	/**gets the last cached list of Stored Items*/
	public StorageItems getStoredItems();

	/**gets the last cached list of Stored Fluids*/
	public StorageFluids getStoredFluids();	
}
