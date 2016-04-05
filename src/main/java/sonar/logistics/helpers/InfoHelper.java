package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.api.wrappers.InfoWrapper;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.ProgressInfo;

public class InfoHelper extends InfoWrapper {

	public List<ILogicInfo> getTileInfo(INetworkCache connections) {
		List<TileProvider> providers = Logistics.tileProviders.getObjects();
		List<ILogicInfo> providerInfo = new ArrayList();
		LinkedHashMap<BlockCoords, ForgeDirection> map = connections.getExternalBlocks(true);
		for (TileProvider provider : providers) {
			for (Map.Entry<BlockCoords, ForgeDirection> entry : map.entrySet()) {
				BlockCoords coords = entry.getKey();
				if (provider.canProvideInfo(coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue())) {
					List<ILogicInfo> info = new ArrayList();
					provider.getTileInfo(info, coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue());
					for (ILogicInfo blockInfo : info) {
						providerInfo.add(blockInfo);
					}
				}
				break;
			}
		}
		Collections.sort(providerInfo, new Comparator<ILogicInfo>() {
			public int compare(ILogicInfo str1, ILogicInfo str2) {
				int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getCategory(), str2.getCategory());
				if (res == 0) {
					res = str1.getCategory().compareTo(str2.getCategory());
				}
				return res;
			}
		});
		return providerInfo;
	}

	public List<ILogicInfo> getEntityInfo(IEntityNode tileNode) {
		List<EntityProvider> providers = Logistics.entityProviders.getObjects();
		List<ILogicInfo> providerInfo = new ArrayList();

		List<Entity> entityList = tileNode.getEntities();
		for (EntityProvider provider : providers) {
			for (Entity entity : entityList) {
				if (entity != null && provider.canProvideInfo(entity)) {
					List<ILogicInfo> info = new ArrayList();
					provider.getHelperInfo(info, entity);
					for (ILogicInfo blockInfo : info) {
						providerInfo.add(blockInfo);
					}
				}
			}
		}
		Collections.sort(providerInfo, new Comparator<ILogicInfo>() {
			public int compare(ILogicInfo str1, ILogicInfo str2) {
				int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getCategory(), str2.getCategory());
				if (res == 0) {
					res = str1.getCategory().compareTo(str2.getCategory());
				}
				return res;
			}
		});

		return providerInfo;
	}

	public ILogicInfo getLatestTileInfo(ILogicInfo tileInfo, INetworkCache network) {
		if (network == null || tileInfo == null) {
			return null;
		}
		TileProvider provider = Logistics.tileProviders.getRegisteredObject(tileInfo.getProviderID());
		if (provider == null) {
			return null;
		}
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			BlockCoords coords = entry.getKey();
			if (provider.canProvideInfo(coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue())) {
				List<ILogicInfo> info = new ArrayList();
				provider.getTileInfo(info, coords.getWorld(), coords.getX(), coords.getY(), coords.getZ(), entry.getValue());
				for (ILogicInfo currentInfo : info) {
					if (currentInfo.equals(tileInfo)) {
						return currentInfo;
					}
				}
			}
			break;
		}

		return null;
	}

	public ILogicInfo getLatestEntityInfo(ILogicInfo entityInfo, IEntityNode entityNode) {
		if (entityInfo == null) {
			return null;
		}
		List<Entity> entityList = entityNode.getEntities();
		EntityProvider provider = Logistics.entityProviders.getRegisteredObject(entityInfo.getProviderID());
		if (provider != null) {
			for (Entity entity : entityList) {
				if (entity != null && provider.canProvideInfo(entity)) {
					List<ILogicInfo> info = new ArrayList();
					provider.getHelperInfo(info, entity);
					for (ILogicInfo currentInfo : info) {
						if (currentInfo.equals(entityInfo)) {
							return currentInfo;
						}
					}
				}
				break;
			}
		}
		return null;
	}

	public ILogicInfo combineData(ILogicInfo primary, ILogicInfo secondary) {
		if (!(primary instanceof CategoryInfo) && !(secondary instanceof CategoryInfo)) {
			if (primary.getDataType() == 0 && secondary.getDataType() == 0) {
				long stored = Long.parseLong(secondary.getData());
				long max = Long.parseLong(primary.getData());

				if (stored < 0 || max < 0) {
					return primary;
				}
				int fluidId = -1;
				if (primary instanceof FluidInfo) {
					FluidInfo fluidinfo = (FluidInfo) primary;
					fluidId = fluidinfo.fluidID;
				}
				if (stored > max) {
					if (stored != 0) {
						return new ProgressInfo(max, stored, primary.getDisplayableData(), fluidId);
					}
				}
				if (max != 0) {
					return new ProgressInfo(stored, max, secondary.getDisplayableData(), fluidId);
				}
			} else {
				return primary;
			}
		} else {
			return new LogicInfo((byte) -1, primary.getCategory(), "Combined Data", primary.getDisplayableData() + secondary.getDisplayableData());

		}
		return primary;
	}

	
}
