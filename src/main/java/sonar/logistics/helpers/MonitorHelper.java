package sonar.logistics.helpers;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.MonitoredList;

public class MonitorHelper {

	public static final String DELETE = "del";
	public static final String SYNC = "syn";
	public static final String REMOVED = "r";
	public static final String SPECIAL = "spe";

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
						if (listType == 1)
							compound.setBoolean(REMOVED, true);
						// list.appendTag(InfoHelper.writeInfoToNBT(compound, info, listType == 1 ? SyncType.SAVE : SyncType.SAVE));
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
				T stack = (T) InfoHelper.readInfoFromNBT(infoTag);
				for (T stored : (ArrayList<T>) stacks.clone()) {
					if (stack.isMatchingInfo(stored)) {
						if (infoTag.getBoolean(REMOVED)) {
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
}
