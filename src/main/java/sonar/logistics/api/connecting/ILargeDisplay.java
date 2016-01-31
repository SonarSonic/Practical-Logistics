package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.render.ICableRenderer;

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
}
