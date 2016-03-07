package sonar.logistics.cache;

import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;

public interface IStorageCache extends INetworkCache {

	public StorageItems getStoredItems();
	
	public StorageFluids getStoredFluids();	
}
