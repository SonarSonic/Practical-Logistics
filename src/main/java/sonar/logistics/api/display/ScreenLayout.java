package sonar.logistics.api.display;

/** the layout of a display screen */
public enum ScreenLayout {
	ONE(1), DUAL(2), GRID(4), LIST(4);// , ALL(1);

	public int maxInfo;

	ScreenLayout(int maxInfo) {
		this.maxInfo = maxInfo;
	}
}