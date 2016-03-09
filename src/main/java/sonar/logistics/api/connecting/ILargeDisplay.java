package sonar.logistics.api.connecting;

import sonar.logistics.api.render.LargeScreenSizing;

/** implemented by LargeDisplays */
public interface ILargeDisplay extends ILogicTile {
	/** when displays join together they create networks which are stored under IDs in the registry, this returns this id */
	public int registryID();

	/** DON'T CALL THIS OUTSIDE OF THE DISPLAY REGISTRY */
	public void setRegistryID(int id);

	/**commonly the meta-data of the display, used for confirming two display can connect together*/
	public int getOrientation();

	/**if this is the handler this will be the size of the display, otherwise this will be null, this shouldn't really be needed outside the class itself*/
	public LargeScreenSizing getSizing();
}
