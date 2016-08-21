package sonar.logistics.helpers;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;

public class MonitorHelper {

	public static final String DELETE = "del";
	public static final String SYNC = "syn";
	public static final String REMOVED = "r";
	public static final String SPECIAL = "spe";

	public static <T extends IMonitorInfo> void addInfoToList(MonitoredList<T> list, MonitorHandler<T> handler, T info) {
		if (info instanceof IJoinableInfo) {
			IJoinableInfo joinableInfo = (IJoinableInfo) info;
			for (T storedInfo : list.info) {
				if (((IJoinableInfo) storedInfo).canJoinInfo(joinableInfo)) {
					storedInfo = (T) ((IJoinableInfo) storedInfo).joinInfo(joinableInfo);
					return;
				}
			}
		}
		list.info.add(info);
	}

	// FIXME - to use updateWriting for some of the tags, like ILogicInfo
	public static <T extends IMonitorInfo> NBTTagCompound writeMonitoredList(NBTTagCompound tag, boolean lastWasNull, MonitorHandler<T> handler, MonitoredList<T> stacks, SyncType type) {
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			NBTTagList list = new NBTTagList();
			stacks.info.forEach(info -> list.appendTag(InfoHelper.writeInfoToNBT(new NBTTagCompound(), info, SyncType.SAVE)));
			if (list.tagCount() != 0) {
				tag.setTag(SYNC, list);
				return tag;
			} else {
				tag.setBoolean(DELETE, true);
				return tag;
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			if ((stacks.info == null || stacks.info.isEmpty())) {
				if (!lastWasNull)
					tag.setBoolean(DELETE, true);
				return tag;
			}
			NBTTagList list = new NBTTagList();
			for (int listType = 0; listType < 2; listType++) {
				ArrayList<T> stackList = listType == 0 ? stacks.changed : stacks.removed;
				for (int i = 0; i < stackList.size(); i++) {
					T info = stackList.get(i);
					if (info != null) {
						NBTTagCompound compound = new NBTTagCompound();
						if (listType == 1)
							compound.setBoolean(REMOVED, true);
						list.appendTag(InfoHelper.writeInfoToNBT(compound, info, listType == 1 ? SyncType.SAVE : SyncType.SAVE));
					}
				}
			}
			if (list.tagCount() != 0) {
				tag.setTag(SPECIAL, list);
			}
		}
		return tag;
	}

	public static <T extends IMonitorInfo> MonitoredList<T> readMonitoredList(NBTTagCompound tag, MonitorHandler<T> handler, MonitoredList<T> stacks, SyncType type) {
		if (tag.hasKey(DELETE)) {
			stacks.info.clear();
			return stacks;
		}
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			if (!tag.hasKey(SYNC)) {
				return stacks;
			}
			NBTTagList list = tag.getTagList(SYNC, 10);
			stacks.info.clear();
			for (int i = 0; i < list.tagCount(); i++) {
				stacks.info.add((T) InfoHelper.readInfoFromNBT(list.getCompoundTagAt(i)));
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			if (!tag.hasKey(SPECIAL)) {
				return stacks;
			}
			NBTTagList list = tag.getTagList(SPECIAL, 10);
			tags: for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound infoTag = list.getCompoundTagAt(i);
				T stack = (T) InfoHelper.readInfoFromNBT(infoTag);
				for (T stored : (ArrayList<T>) stacks.info.clone()) {
					if (stack.isMatchingInfo(stored)) {
						if (infoTag.getBoolean(REMOVED)) {
							stacks.info.remove(stored);
						} else {
							stored.readData(infoTag, SyncType.SAVE);
						}
						continue tags;
					}
				}
				stacks.info.add(stack);
			}
		}
		return stacks;
	}
}
