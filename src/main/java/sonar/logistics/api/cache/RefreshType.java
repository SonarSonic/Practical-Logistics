package sonar.logistics.api.cache;

/**not fully implemented yet, will be used to make refreshing networks more efficient by removing the need to refresh everything everytime*/
public enum RefreshType {
	FULL, CONNECTED_BLOCKS;

	public boolean shouldRefreshConnections() {
		return this == FULL;
	}
}
