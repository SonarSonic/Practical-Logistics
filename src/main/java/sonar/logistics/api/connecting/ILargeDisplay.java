package sonar.logistics.api.connecting;

import sonar.logistics.api.render.LargeScreenSizing;

/** implemented by LargeDisplays */
public interface ILargeDisplay extends ILogicTile {
	/**
	 * when displays join together they create networks which are stored under
	 * IDs in the registry, this returns this id
	 */
	public int registryID();

	/** DON'T CALL THIS OUTSIDE OF THE DISPLAY REGISTRY */
	public void setRegistryID(int id);

	public int getOrientation();
	
	public LargeScreenSizing getSizing();
}
