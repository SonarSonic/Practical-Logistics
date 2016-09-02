package sonar.logistics.api.cache;

/** not fully implemented yet, will be used to make refreshing networks more efficient by removing the need to refresh everything everytime */
public enum RefreshType {
	/** when a cable is changed */
	FULL,
	/** when a node is changed */
	CONNECTED_BLOCKS,
	/** when an emitter is changed */
	CONNECTED_NETWORKS, 	
	NONE;

	public boolean shouldRefreshCables() {
		return this == FULL;
	}

	public boolean shouldRefreshNetworks() {
		return this == FULL || this == CONNECTED_NETWORKS;
	}

	public boolean shouldRefreshConnections() {
		return this == FULL || this == CONNECTED_BLOCKS || this == CONNECTED_NETWORKS;
	}
}
