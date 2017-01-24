package sonar.logistics.api.viewers;

public class MonitorTally {
	public ViewerType type;
	public int value = 0;

	public MonitorTally(ViewerType type, int value) {
		this(type);
		this.value = value;
	}

	public MonitorTally(ViewerType type) {
		this.type = type;
	}

	public String toString() {
		return type.toString() + ": " + value;
	}

	public int hashCode() {
		return type.hashCode();
	}

	public boolean equals(Object object) {
		if (object != null && object instanceof MonitorTally) {
			return ((MonitorTally) object).type == this.type;
		}
		return false;
	}
}