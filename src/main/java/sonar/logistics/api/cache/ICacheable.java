package sonar.logistics.api.cache;

import java.util.ArrayList;

import sonar.core.utils.BlockCoords;

public interface ICacheable {

	/**
	 * @param types an empty ArrayList to be filled with relevant {@link CacheType}s
	 */
	public void getCacheTypes(ArrayList<CacheTypes> types);

	/**the {@link BlockCoords} this Block/FMP Part should be registered as on the Network
	 * @return the {@link BlockCoords}
	 */
	public BlockCoords getCoords();
}
