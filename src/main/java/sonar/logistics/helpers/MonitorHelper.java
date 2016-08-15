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
	//FIXME - to use updateWriting for some of the tags, like ILogicInfo
	public static <T extends IMonitorInfo> NBTTagCompound writeMonitoredList(NBTTagCompound tag, boolean lastWasNull, MonitorHandler<T> handler, MonitoredList<T> stacks, SyncType type) {
		if (type.isType(SyncType.DEFAULT_SYNC)) {
			NBTTagList list = new NBTTagList();
			stacks.info.forEach(info -> list.appendTag(handler.writeInfo(info, new NBTTagCompound(), SyncType.SAVE)));
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
			for (int l = 0; l < 2; l++) {
				ArrayList<T> stackList = l == 0 ? stacks.changed : stacks.removed;
				for (int i = 0; i < stackList.size(); i++) {
					if (stackList.get(i) != null) {
						NBTTagCompound compound = new NBTTagCompound();
						if (l == 1)
							compound.setBoolean(REMOVED, true);
						list.appendTag(handler.writeInfo(stackList.get(i), compound, SyncType.SAVE));
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
				stacks.info.add(handler.readInfo(list.getCompoundTagAt(i), SyncType.SAVE));
			}
		} else if (type.isType(SyncType.SPECIAL)) {
			if (!tag.hasKey(SPECIAL)) {
				return stacks;
			}
			NBTTagList list = tag.getTagList(SPECIAL, 10);
			tags: for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				T stack = handler.readInfo(compound, SyncType.SAVE);
				for (T stored : (ArrayList<T>) stacks.info.clone()) {
					if (stack.isMatchingInfo(stored)) {
						if (compound.getBoolean(REMOVED)) {
							stacks.info.remove(stored);
						} else {
							stored.updateFrom(stack);
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
