package sonar.logistics.connections;

import java.util.ArrayList;

import sonar.core.api.StorageSize;
import sonar.core.utils.Pair;
import sonar.logistics.api.info.monitor.IJoinableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class MonitoredList<T extends IMonitorInfo> extends ArrayList<T> {

	// public MonitoredList<T> EMPTY = newMonitoredList();
	//public ArrayList<T> info = new ArrayList<T>();
	public ArrayList<T> changed = new ArrayList<T>();
	public ArrayList<T> removed = new ArrayList<T>();
	public StorageSize sizing;

	public MonitoredList() {
	}

	public MonitoredList(ArrayList<T> items, StorageSize sizing, ArrayList<T> changed, ArrayList<T> removed) {
		this.addAll(items);
		this.sizing = sizing;
		this.changed = changed;
		this.removed = removed;
	}

	public static <I extends IMonitorInfo> MonitoredList<I> newMonitoredList() {
		return new MonitoredList<I>();
	}

	public ArrayList<T> cloneInfo(){
		return (ArrayList<T>) super.clone();
	}
	
	public void setInfo(ArrayList<T> info){
		this.clear();
		this.addAll(info);
	}
	public MonitoredList<T> copyInfo() {
		return new MonitoredList<T>((ArrayList<T>) cloneInfo(), sizing, (ArrayList<T>) changed.clone(), (ArrayList<T>) removed.clone());
	}
	//public MonitoredList<T> clone() {
	//	return new MonitoredList<T>((ArrayList<T>) cloneInfo(), sizing, (ArrayList<T>) changed.clone(), (ArrayList<T>) removed.clone());
	//}

	public void addInfoToList(T newInfo) {
		if (newInfo instanceof IJoinableInfo) {
			IJoinableInfo joinableInfo = (IJoinableInfo) newInfo;
			for (T storedInfo : this) {
				if (((IJoinableInfo) storedInfo).canJoinInfo(newInfo)) {
					((IJoinableInfo) storedInfo).joinInfo(newInfo);
					return;
				}
			}
		}
		add(newInfo);
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
		return this;
	}

	/** @param info the info type you wish to check
	 * @return a boolean for if the info was changed and the new info */
	public Pair<Boolean, IMonitorInfo> getLatestInfo(IMonitorInfo oldInfo) {
		for (T newInfo : cloneInfo()) {
			if (newInfo.isMatchingType(oldInfo) && newInfo.isMatchingInfo(oldInfo) && !newInfo.isIdenticalInfo(oldInfo)) {
				return new Pair(true, newInfo);
			}
		}
		return new Pair(false, oldInfo);
	}
}