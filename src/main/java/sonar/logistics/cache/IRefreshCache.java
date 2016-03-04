package sonar.logistics.cache;

import sonar.logistics.api.cache.INetworkCache;

public interface IRefreshCache extends INetworkCache {
	
	public void refreshCache(int networkID);
}
