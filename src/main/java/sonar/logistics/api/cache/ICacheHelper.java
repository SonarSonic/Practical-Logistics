package sonar.logistics.api.cache;

import java.util.List;

import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.ILogicTile;

public interface ICacheHelper<T extends ILogicTile> {

	public void addCoordCache(T cacheable, INetworkCache cache);
}
