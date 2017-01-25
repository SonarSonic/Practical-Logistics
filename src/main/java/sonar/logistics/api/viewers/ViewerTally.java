package sonar.logistics.api.viewers;

public class ViewerTally {
	public ViewerType type;
	public int value = 0;

	public ViewerTally(ViewerType type, int value) {
		this(type);
		this.value = value;
	}

	public ViewerTally(ViewerType type) {
		this.type = type;
	}

	public String toString() {
		return type.toString() + ": " + value;
	}

	public int hashCode() {
		return type.hashCode();
	}

	public boolean equals(Object object) {
		if (object != null && object instanceof ViewerTally) {
			return ((ViewerTally) object).type == this.type;
		}
		return false;
	}
}