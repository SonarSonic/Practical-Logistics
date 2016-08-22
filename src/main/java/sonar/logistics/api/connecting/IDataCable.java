package sonar.logistics.api.connecting;

import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.render.ICableRenderer;

/** implemented on Tile Entities and Forge Multipart parts which are cables */
public interface IDataCable extends ICableRenderer, ILogicTile {
	
	/** when cables join together they create networks which are stored under IDs in the registry, this returns this id */
	public int registryID();

	/** DON'T CALL THIS OUTSIDE OF THE CABLE REGISTRY - Once this is called the Registry will assume the id was successfully changed*/
	public void setRegistryID(int id);

	/** is the cable limited by the number of channels, true for Channelled Cables, false for Data Cables */
	public CableType getCableType();

	/** called when the cable is added to the world */
	public void addCable();

	/** called when the cable is removed to the world */
	public void removeCable();	

	/** the cable should check it's connections and see if it is connected to the correct ones */
	public void refreshConnections();
	
	/**ensures all connections are connected to the same network*/
	public void configureConnections(INetworkCache network);	
	
	/**if this cable has connected ILogicTiles within it's multipart*/
	public boolean hasConnections();	
}
