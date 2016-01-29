package sonar.logistics.api.connecting;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.render.ICableRenderer;

/**implemented on Tile Entities and Forge Multipart parts which are cables*/
public interface IMultiDataCable extends ICableRenderer, IMultiTile {
	/**
	 * checks if the cable is blocked in a given direction (e.g. other FMP part is blocking the connection
	 */
	public boolean isBlocked(ForgeDirection dir);

	/**when cables join together they create networks which are stored under IDs in the registry, this returns this id*/
	public int registryID();

	/**DON'T CALL THIS OUTSIDE OF THE CABLE REGISTRY - Once this is called the Registry will assume the id was successfully changed.*/
	public void setRegistryID(int id);
	
	/**is the cable limited by the number of channels, true for Channelled Cables, false for Data Cables*/
	public boolean unlimitedChannels();
}
