package sonar.logistics.api.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sonar.core.api.StoredFluidStack;
import sonar.core.api.InventoryHandler.StorageSize;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;

public class InfoWrapper {
		
	/** gets a list of all Info available at a given {@link IConnectionNode}, used by getInfoList(), here if the need should arise
	 * @param network the {@link INetworkCache} to get info from
	 * @return list of {@link ILogicInfo} */
	public ArrayList<ILogicInfo> getTileInfo(INetworkCache network) {
		return new ArrayList();
	}

	/** gets a list of all Info available at a given {@link IEntityNode}, useg by getInfoList(), here if the need should arise
	 * @param network the {@link INetworkCache} to get info from
	 * @return list of {@link ILogicInfo} */
	public ArrayList<ILogicInfo> getEntityInfo(IEntityNode tileNode) {
		return new ArrayList();
	}

	/** for updating {@link ILogicInfo}, used by the Info Reader
	 * @param tileInfo tile {@link ILogicInfo} in need of updating
	 * @param network the {@link INetworkCache} to get info from
	 * @return the latest {@link ILogicInfo} */
	public ILogicInfo getLatestTileInfo(ILogicInfo tileInfo, INetworkCache network) {
		return tileInfo;
	}

	/** for updating {@link ILogicInfo}, used by the Info Reader
	 * @param entityInfo entity {@link ILogicInfo} in need of updating
	 * @param entityNode the {@link IEntityNode} to check at
	 * @return the latest {@link ILogicInfo} */
	public ILogicInfo getLatestEntityInfo(ILogicInfo entityInfo, IEntityNode entityNode) {
		return entityInfo;
	}

	/** used for combining two bits of {@link ILogicInfo}, used by the Info Reader to create progress bars
	 * @param primary first {@link ILogicInfo}
	 * @param secondary second {@link ILogicInfo}
	 * @return progress bar info, or default {@link ILogicInfo} */
	public ILogicInfo combineData(ILogicInfo primary, ILogicInfo secondary) {
		return primary;
	}
}
