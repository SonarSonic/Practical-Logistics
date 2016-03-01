package sonar.logistics.api.cache;

import java.util.List;

import sonar.core.utils.BlockCoords;

public interface ICacheHelper<T extends ICacheable> {

	public void addCoordCache(T cacheable, INetworkCache cache);
}
