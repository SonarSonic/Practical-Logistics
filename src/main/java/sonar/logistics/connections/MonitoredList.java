package sonar.logistics.connections;

import java.util.ArrayList;

import sonar.core.api.StorageSize;
import sonar.core.utils.Pair;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class MonitoredList<T extends IMonitorInfo> {

	// public MonitoredList<T> EMPTY = newMonitoredList();
	public ArrayList<T> info = new ArrayList<T>();
	public ArrayList<T> changed = new ArrayList<T>();
	public ArrayList<T> removed = new ArrayList<T>();
	public StorageSize sizing;

	public MonitoredList() {
	}

	public MonitoredList(ArrayList<T> items, StorageSize sizing, ArrayList<T> changed, ArrayList<T> removed) {
		this.info = items;
		this.sizing = sizing;
		this.changed = changed;
		this.removed = removed;
	}

	public static <I extends IMonitorInfo> MonitoredList<I> newMonitoredList() {
		return new MonitoredList<I>();
	}

	public MonitoredList<T> copy() {
		return new MonitoredList<T>((ArrayList<T>) this.info.clone(), sizing, (ArrayList<T>) changed.clone(), (ArrayList<T>) removed.clone());
	}

	public void updateList(MonitoredList<T> lastList) {
		ArrayList<T> changed = ((ArrayList<T>) info.clone());
		ArrayList<T> removed = ((ArrayList<T>) lastList.info.clone());
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
	}

	/** @param info the info type you wish to check
	 * @return a boolean for if the info was changed and the new info */
	public Pair<Boolean, IMonitorInfo> getLatestInfo(IMonitorInfo oldInfo) {
		for (T newInfo : info) {
			if (newInfo.isMatchingType(oldInfo) && newInfo.isMatchingInfo(oldInfo) && !newInfo.isIdenticalInfo(oldInfo)) {
				return new Pair(true, newInfo);
			}
		}
		return new Pair(false, oldInfo);
	}
}