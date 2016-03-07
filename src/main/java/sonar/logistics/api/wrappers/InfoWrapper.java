package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.info.Info;

public class InfoWrapper {

	/**gets a list of all Info available at a given {@link BlockCoords}
	 * @param coords {@link BlockCoords} to check at
	 * @return list of {@link Info}
	 */
	//public List<Info> getInfoList(BlockCoords coords) {
	//	return Collections.EMPTY_LIST;
	//}
	/**gets a list of all Info available at a given {@link IConnectionNode}, used by getInfoList(), here if the need should arise
	 * @param coordList the map containing coords and directions.
	 * @return list of {@link Info}
	 */
	public List<Info> getTileInfo(INetworkCache coordList) {
		return Collections.EMPTY_LIST;
	}

	/**gets a list of all Info available at a given {@link IEntityNode}, useg by getInfoList(), here if the need should arise
	 * @param coords {@link IEntityNode} to check at
	 * @return list of {@link Info}
	 */
	public List<Info> getEntityInfo(IEntityNode tileNode) {
		return Collections.EMPTY_LIST;
	}
	/**for updating {@link Info}, used by the Info Reader
	 * @param tileInfo tile {@link Info} in need of updating
	 * @param network the {@link IConnectionNode} to check at
	 * @return the latest {@link Info}
	 */
	public Info getLatestTileInfo(Info tileInfo, INetworkCache network) {
		return tileInfo;
	}
	/**for updating {@link Info}, used by the Info Reader
	 * @param entityInfo entity {@link Info} in need of updating
	 * @param entityNode the {@link IEntityNode} to check at
	 * @return the latest {@link Info}
	 */
	public Info getLatestEntityInfo(Info entityInfo, IEntityNode entityNode) {
		return entityInfo;
	}
	/** used for combining two bits of {@link Info}, used by the Info Reader to create progress bars
	 * @param primary first {@link Info}
	 * @param secondary second {@link Info}
	 * @return progress bar info, or default {@link Info}
	 */
	public Info combineData(Info primary, Info secondary) {
		return primary;
	}
}
