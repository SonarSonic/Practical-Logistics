package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import mcmultipart.multipart.IMultipart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.SonarAPI;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;
import sonar.core.utils.SortingDirection;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsASMLoader;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.IScaleableDisplay;
import sonar.logistics.api.display.RenderInfoProperties;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.types.LogicInfo;
import sonar.logistics.common.multiparts.LogisticsMultipart;
import sonar.logistics.connections.monitoring.MonitoredList;

public class InfoHelper {

	public static final String DELETE = "del";
	public static final String SYNC = "syn";
	public static final String REMOVED = "rem";
	public static final String SPECIAL = "spe";

	public static void screenItemStackClicked(StoredItemStack itemstack, int networkID, BlockInteractionType type, boolean doubleClick, RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		Pair<Integer, ItemInteractionType> toRemove = InfoHelper.getItemsToRemove(type);
		if (toRemove.a != 0 && networkID != -1) {
			INetworkCache cache = Logistics.instance.networkManager.getNetwork(networkID);
			switch (toRemove.b) {
			case ADD:
				if (stack != null) {
					if (!doubleClick) {
						LogisticsAPI.getItemHelper().insertItemFromPlayer(player, cache, player.inventory.currentItem);
					} else {
						LogisticsAPI.getItemHelper().insertInventoryFromPlayer(player, cache, player.inventory.currentItem);
					}
				}
				break;
			case REMOVE:
				IMultipart part = hit.partHit;
				if (part != null && part instanceof LogisticsMultipart) {
					BlockPos pos = part.getPos();
					StoredItemStack extract = LogisticsAPI.getItemHelper().extractItem(cache, itemstack.copy().setStackSize(toRemove.a));
					if (extract != null) {
						pos = pos.offset(hit.sideHit);
						SonarAPI.getItemHelper().spawnStoredItemStack(extract, part.getWorld(), pos.getX(), pos.getY(), pos.getZ(), hit.sideHit);
					}
				}
				break;
			default:
				break;
			}
		}
	}

	// FIXME - to use updateWriting for some of the tags, like ILogicInfo
	public static <T extends IMonitorInfo> NBTTagCompound writeMonitoredList(NBTTagCompound tag, boolean lastWasNull, MonitoredList<T> stacks, SyncType type) {
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			stacks.sizing.writeData(tag, SyncType.SAVE);
			NBTTagList list = new NBTTagList();
			stacks.forEach(info -> {
				if (info != null && info.isValid()) {
					list.appendTag(InfoHelper.writeInfoToNBT(new NBTTagCompound(), info, SyncType.SAVE));
				}
			});
			if (list.tagCount() != 0) {
				tag.setTag(SYNC, list);
				return tag;
			} else {
				if (!lastWasNull)
					tag.setBoolean(DELETE, true);
				return tag;
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			stacks.sizing.writeData(tag, SyncType.DEFAULT_SYNC);
			if ((stacks == null || stacks.isEmpty())) {
				if (!lastWasNull)
					tag.setBoolean(DELETE, true);
				return tag;
			}
			NBTTagList list = new NBTTagList();
			for (int listType = 0; listType < 2; listType++) {
				ArrayList<T> stackList = listType == 0 ? stacks.changed : stacks.removed;
				for (int i = 0; i < stackList.size(); i++) {
					T info = stackList.get(i);
					if (info != null && info.isValid()) {
						NBTTagCompound compound = new NBTTagCompound();
						compound.setBoolean(REMOVED, listType == 1);
						list.appendTag(InfoHelper.writeInfoToNBT(compound, info, SyncType.SAVE));
					}
				}
			}
			if (list.tagCount() != 0) {
				tag.setTag(SPECIAL, list);
			}
		}
		return tag;
	}

	public static <T extends IMonitorInfo> MonitoredList<T> readMonitoredList(NBTTagCompound tag, MonitoredList<T> stacks, SyncType type) {
		if (tag.hasKey(DELETE)) {
			stacks.clear();
			return stacks;
		}
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			if (!tag.hasKey(SYNC)) {
				return stacks;
			}
			NBTTagList list = tag.getTagList(SYNC, 10);
			stacks.clear();
			for (int i = 0; i < list.tagCount(); i++) {
				stacks.add((T) InfoHelper.readInfoFromNBT(list.getCompoundTagAt(i)));
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			if (!tag.hasKey(SPECIAL)) {
				return stacks;
			}
			NBTTagList list = tag.getTagList(SPECIAL, 10);
			tags: for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound infoTag = list.getCompoundTagAt(i);
				boolean removed = infoTag.getBoolean(REMOVED);
				T stack = (T) InfoHelper.readInfoFromNBT(infoTag);
				Iterator<T> iterator = stacks.iterator();
				while (iterator.hasNext()) {
					T stored = iterator.next();
					if (stack.isMatchingInfo(stored)) {
						if (removed) {
							stacks.remove(stored);
						} else {
							stored.readData(infoTag, SyncType.SAVE);
						}
						continue tags;
					}
				}
				stacks.add(stack);
			}
		}
		return stacks;
	}

	public static int compareWithDirection(long stored1, long stored2, SortingDirection dir) {
		if (stored1 < stored2)
			return dir == SortingDirection.DOWN ? 1 : -1;
		if (stored1 == stored2)
			return 0;
		return dir == SortingDirection.DOWN ? -1 : 1;
	}

	public static int compareStringsWithDirection(String string1, String string2, SortingDirection dir) {
		int res = String.CASE_INSENSITIVE_ORDER.compare(string1, string2);
		if (res == 0) {
			res = string1.compareTo(string2);
		}
		return dir == SortingDirection.DOWN ? res : -res;
	}

	public static ArrayList<LogicInfo> sortInfoList(ArrayList<LogicInfo> oldInfo) {
		ArrayList<LogicInfo> providerInfo = (ArrayList<LogicInfo>) oldInfo.clone();
		Collections.sort(providerInfo, new Comparator<LogicInfo>() {
			public int compare(LogicInfo str1, LogicInfo str2) {
				return Integer.compare(str1.getRegistryType().sortOrder, str2.getRegistryType().sortOrder);
			}
		});
		ArrayList<LogicInfo> sortedInfo = new ArrayList();
		LogicInfo lastInfo = null;
		for (LogicInfo blockInfo : (ArrayList<LogicInfo>) providerInfo.clone()) {
			if (lastInfo == null || !lastInfo.getRegistryType().equals(blockInfo.getRegistryType())) {
				sortedInfo.add(LogicInfo.buildCategoryInfo(blockInfo.getRegistryType()));
			}
			sortedInfo.add(blockInfo);
			lastInfo = blockInfo;
		}
		return sortedInfo;
	}

	public static double[] getScaling(IInfoDisplay display, ScreenLayout layout, int pos) {
		DisplayType type = display.getDisplayType();
		double width = type.width, height = type.height, scale = type.scale;
		if (display instanceof IScaleableDisplay) {
			double[] scaling = ((IScaleableDisplay) display).getScaling();
			width = scaling[0];
			height = scaling[1];
			scale = scaling[2];
		}

		switch (layout) {
		case DUAL:
			return new double[] { width, height / 2, scale };
		case GRID:
			return new double[] { width / 2, height / 2, scale / 1.5 };
		case LIST:
			return new double[] { width, height / 4, scale / 1.5 };
		default:
			return new double[] { width, height, scale * 1.2 };
		}
	}

	public static double[] getTranslation(IInfoDisplay display, ScreenLayout layout, int pos) {
		DisplayType type = display.getDisplayType();
		double width = type.width, height = type.height, scale = type.scale;
		if (display instanceof IScaleableDisplay) {
			double[] scaling = ((IScaleableDisplay) display).getScaling();
			// width = scaling[0];
			// height = scaling[1];
			// scale = scaling[2];
		}

		switch (layout) {
		case DUAL:
			return new double[] { 0, pos == 1 ? height / 2 : 0, 0 };
		case GRID:
			return new double[] { pos == 1 || pos == 3 ? height / 2 : 0, pos > 1 ? height / 2 : 0, 0 };
		case LIST:
			return new double[] { 0, pos * (height / 4), 0 };
		default:
			return new double[] { 0, 0, 0 };
		}
	}

	public static double[] getIntersect(IInfoDisplay display, ScreenLayout layout, int pos) {
		DisplayType type = display.getDisplayType();
		double width = type.width, height = type.height, scale = type.scale;
		if (display instanceof IScaleableDisplay) {
			double[] scaling = ((IScaleableDisplay) display).getScaling();
			width = scaling[0];
			height = scaling[1];
			scale = scaling[2];
		}

		switch (layout) {
		case DUAL:
			return new double[] { 0, pos == 1 ? type.height / 2 : 0, pos == 1 ? 1 : type.width / 2, pos == 1 ? 1 : type.height / 2 };
		case GRID:
			return new double[] { (pos == 1 || pos == 3 ? type.width / 2 : 0), (pos == 1 || pos == 3 ? type.height / 2 : 0), (pos == 1 || pos == 3 ? 1 : type.width / 2), (pos == 1 || pos == 3 ? 1 : type.height / 2) };
		case LIST:
			return new double[] { 0, pos * (type.height / 4), 0, (pos + 1) * (type.height / 4) };

		default:
			return new double[] { 0, 0, type.width, type.height };
		}
	}

	public static boolean canBeClickedStandard(RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (renderInfo.container.getMaxCapacity() == 1) {
			return true;
		}
		IInfoDisplay display = renderInfo.container.getDisplay();
		double[] intersect = getIntersect(display, display.getLayout(), renderInfo.infoPos);
		BlockPos pos = hit.getBlockPos();
		double x = hit.hitVec.xCoord - pos.getX();
		double y = hit.hitVec.yCoord - pos.getY();
		if (x >= intersect[0] && x <= intersect[2] && 1 - y >= intersect[1] && 1 - y <= intersect[3]) {
			return true;
		}
		return false;
	}

	public enum ItemInteractionType {
		ADD, REMOVE;
	}

	public static Pair<Integer, ItemInteractionType> getItemsToRemove(BlockInteractionType type) {
		switch (type) {
		case LEFT:
			return new Pair(1, ItemInteractionType.REMOVE);
		case RIGHT:
			return new Pair(64, ItemInteractionType.ADD);
		case SHIFT_LEFT:
			return new Pair(64, ItemInteractionType.REMOVE);
		default:
			return new Pair(0, ItemInteractionType.ADD);
		}
	}

	public boolean hasInfoChanged(IMonitorInfo info, IMonitorInfo newInfo) {
		if (info == null && newInfo == null) {
			return false;
		} else if (info == null && newInfo != null || info != null && newInfo == null) {
			return true;
		}
		return info.isMatchingType(newInfo) && info.isIdenticalInfo(newInfo) && info.isIdenticalInfo(newInfo);
	}

	public static int getName(String name) {
		return LogisticsASMLoader.infoIds.get(name);
	}

	public static Class<? extends IMonitorInfo> getInfoType(int id) {
		return LogisticsASMLoader.infoClasses.get(LogisticsASMLoader.infoNames.get(id));
	}

	public static NBTTagCompound writeInfoToNBT(NBTTagCompound tag, IMonitorInfo info, SyncType type) {
		tag.setInteger("iiD", LogisticsASMLoader.infoIds.get(info.getID()));
		info.writeData(tag, type);
		return tag;
	}

	public static IMonitorInfo readInfoFromNBT(NBTTagCompound tag) {
		return loadInfo(tag.getInteger("iiD"), tag);
	}

	public static IMonitorInfo loadInfo(int id, NBTTagCompound tag) {
		return NBTHelper.instanceNBTSyncable(getInfoType(id), tag);
	}
}
