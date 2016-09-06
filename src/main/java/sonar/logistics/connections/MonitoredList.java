package sonar.logistics.connections;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import sonar.core.api.StorageSize;
import sonar.core.utils.Pair;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class MonitoredList<T extends IMonitorInfo> extends ArrayList<T> {

	// public MonitoredList<T> EMPTY = newMonitoredList();
	// public ArrayList<T> info = new ArrayList<T>();
	public ArrayList<T> changed = new ArrayList<T>();
	public ArrayList<T> removed = new ArrayList<T>();
	public StorageSize sizing;
	public boolean hasChanged = true;

	public MonitoredList() {
		super();
	}

	public MonitoredList(ArrayList<T> items, StorageSize sizing, ArrayList<T> changed, ArrayList<T> removed) {
		super(items);
		this.sizing = sizing;
		this.changed = changed;
		this.removed = removed;
	}

	public static <I extends IMonitorInfo> MonitoredList<I> newMonitoredList() {
		return new MonitoredList<I>(Lists.<I>newArrayList(), new StorageSize(0, 0), Lists.<I>newArrayList(), Lists.<I>newArrayList());
	}

	public ArrayList<T> cloneInfo() {
		return (ArrayList<T>) super.clone();
	}

	public void setInfo(ArrayList<T> info) {
		this.clear();
		this.addAll(info);
	}

	public MonitoredList<T> copyInfo() {
		return new MonitoredList<T>((ArrayList<T>) cloneInfo(), new StorageSize(sizing.getStored(), sizing.getMaxStored()), (ArrayList<T>) changed.clone(), (ArrayList<T>) removed.clone());
	}

	public void addInfoToList(T newInfo) {
		if (newInfo instanceof IJoinableInfo) {
			for (int i = 0; i < this.size(); i++) {
				T storedInfo = this.get(i);
				if (((IJoinableInfo) storedInfo).canJoinInfo(newInfo)) {
					set(i, (T) ((IJoinableInfo) storedInfo).joinInfo(newInfo.copy()));
					return;
				}
			}
		}
		add((T) newInfo.copy());
	}

	public MonitoredList<T> updateList(MonitoredList<T> lastList) {
		ArrayList<T> changed = ((ArrayList<T>) cloneInfo());
		ArrayList<T> removed = ((ArrayList<T>) lastList.cloneInfo());
		if (lastList != null) {
			changed.removeAll(removed);
		}
		((ArrayList<T>) removed.clone()).forEach(r -> changed.forEach(c -> {
			if (r.isMatchingInfo(c)) {
				removed.remove(r);
			}
		}));
		this.changed = changed;
		this.removed = removed;
		hasChanged = !changed.isEmpty() || !removed.isEmpty();
		return this;
	}

	public void markDirty() {
		hasChanged = true;
	}

	/** @param info the info type you wish to check
	 * @return a boolean for if the info was changed and the new info */
	public Pair<Boolean, IMonitorInfo> getLatestInfo(IMonitorInfo oldInfo) {
		for (T newInfo : this) {
			if (newInfo.isMatchingType(oldInfo) && newInfo.isMatchingInfo(oldInfo) && !newInfo.isIdenticalInfo(oldInfo)) {
				return new Pair(true, newInfo);
			}
		}
		return new Pair(false, oldInfo);
	}
}