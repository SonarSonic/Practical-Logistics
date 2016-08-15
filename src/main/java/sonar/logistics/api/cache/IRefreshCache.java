package sonar.logistics.api.cache;

/** implemented on {@link INetworkCache}s which should be updated on every world tick */
public interface IRefreshCache extends INetworkCache {

	/**called when a new block is added/removed to the network, or if the last network ID doesn't match the current ID
	 * this should be used to update caches with relevant data
	 * @param networkID the latest NetworkID of this network 
	 * @param fullRefresh should all network blocks be reconfigured? NOTE: if false Data Cables won't update their connections!*/
	public void refreshCache(int networkID, boolean fullRefresh);

	/**called on every World Tick
	 * this should be used to update caches with relevant data, that needs to be constantly updated e.g. Items, Fluids, Energy etc
	 * @param networkID the latest NetworkID of this network */	 
	public void updateNetwork(int networkID);
}

