package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.api.StoredFluidStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.EntityProvider;
import sonar.logistics.api.providers.TileProvider;
import sonar.logistics.api.wrappers.InfoWrapper;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidInfo;
import sonar.logistics.info.types.ProgressInfo;

public class InfoHelper extends InfoWrapper {

	public static final LogicInfo empty = new LogicInfo((byte) -1, "INFO", " ", "NO DATA");

	public ArrayList<ILogicInfo> getTileInfo(INetworkCache connections) {
		List<TileProvider> providers = Logistics.tileProviders.getObjects();
		ArrayList<ILogicInfo> providerInfo = new ArrayList();
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

	public ArrayList<ILogicInfo> getEntityInfo(IEntityNode tileNode) {
		List<EntityProvider> providers = Logistics.entityProviders.getObjects();
		ArrayList<ILogicInfo> providerInfo = new ArrayList();

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
					if (currentInfo.isMatchingInfo(tileInfo)) {
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
						if (currentInfo.isMatchingInfo(entityInfo)) {
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

	public static boolean writeInfoToNBT(NBTTagCompound tag, ArrayList<ILogicInfo> current, ArrayList<ILogicInfo> last, SyncType type) {
		if (type.isType(SyncType.SYNC)) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < current.size(); i++) {
				if (current.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					Logistics.infoTypes.writeToNBT(compound, current.get(i));
					list.appendTag(compound);
				}
			}
			if (list.tagCount() != 0) {
				tag.setTag("c", list);
				return false;
			} else {
				tag.setBoolean("n", true);
				return true;
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			if ((current == null || current.isEmpty())) {
				if (!(last == null || last.isEmpty()))
					tag.setBoolean("n", true);
				return true;
			}
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < current.size(); i++) {
				ILogicInfo info = current.get(i);
				if (info != null) {
					NBTTagCompound compound = new NBTTagCompound();
					boolean full = true;
					if (i < last.size()) {
						ILogicInfo lastInfo = last.get(i);
						SyncType sync = lastInfo.getNextSyncType(info);
						if (sync == null) {
							continue;
						}
						switch (sync) {
						case SYNC:
							lastInfo.writeUpdate(info, compound);
							full = false;
							break;
						default:
							break;
						}
					}
					if (full) {
						Logistics.infoTypes.writeToNBT(compound, current.get(i));
					} else {
						compound.setBoolean("up", true);
					}
					compound.setInteger("pos", i);
					list.appendTag(compound);
				}

			}
			if (list.tagCount() != 0) {
				tag.setTag("s", list);
			}
		}
		return false;
	}

	public static void readStorageToNBT(NBTTagCompound tag, ArrayList<ILogicInfo> current, SyncType type) {
		if (tag.hasKey("n")) {
			current.clear();
			return;
		}
		if (type.isType(SyncType.SYNC)) {
			if (!tag.hasKey("c")) {
				return;
			}
			NBTTagList list = tag.getTagList("c", 10);
			current.clear();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				current.add(Logistics.infoTypes.readFromNBT(compound));
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			if (!tag.hasKey("s")) {
				return;
			}
			NBTTagList list = tag.getTagList("s", 10);
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				int j = compound.getInteger("pos");
				if (j < current.size()) {
					if (compound.getBoolean("up")) {
						current.get(j).readUpdate(compound);
					} else {
						ILogicInfo info = current.get(j);
						info = Logistics.infoTypes.readFromNBT(compound);
						if (info != null)
							current.add(j, info);
					}
				} else {
					current.set(j, Logistics.infoTypes.readFromNBT(compound));
				}
				/*
				 * ILogicInfo stack = Logistics.infoTypes.readFromNBT(compound); for (ILogicInfo stored : (ArrayList<ILogicInfo>) current.clone()) { if (stored.isMatchingInfo(stack)) { if (compound.getBoolean("r")) { current.remove(stored); } else { stored = stack; } } } current.add(stack);
				 */
			}
		}
	}

	public static ArrayList<ILogicInfo> sortInfoList(ArrayList<ILogicInfo> current) {
		ArrayList<ILogicInfo> newInfo = new ArrayList();
		ILogicInfo lastInfo = null;
		for (ILogicInfo blockInfo : (ArrayList<ILogicInfo>) current.clone()) {
			if (lastInfo == null || !lastInfo.getCategory().equals(blockInfo.getCategory())) {
				newInfo.add(CategoryInfo.createInfo(blockInfo.getCategory()));
			}
			if (blockInfo.getDataType() != -2)
				newInfo.add(blockInfo);
			lastInfo = blockInfo;
		}
		return newInfo;
	}

}
