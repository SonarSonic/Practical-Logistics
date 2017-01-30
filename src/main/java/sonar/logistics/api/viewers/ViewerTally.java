package sonar.logistics.api.viewers;

/**a tally of all the ways an EntityPlayer is viewing a {@link ILogicViewable}, when there is 0 - no data packets won't be sent*/
public class ViewerTally {
	public IViewersList origin; //used for removing viewing types, preventing connected displays from being altered, when they shouldn't be.
	public ViewerType type;
	public int value = 0;

	public ViewerTally(IViewersList origin, ViewerType type, int value) {
		this(origin, type);
		this.value = value;
	}

	public ViewerTally(IViewersList origin, ViewerType type) {
		this.origin=origin;
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