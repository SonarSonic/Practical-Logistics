package sonar.logistics.api.connecting;

/** used to distinguish the different types of Cable Connections */
public enum CableType {
	/** for standard Data Cables which are limited to one channel */
	DATA_CABLE,
	/** for Channelled Data Cables which can unlimited channels */
	CHANNELLED_CABLE,
	/** for {@link ILogicTile} which can connect to a network */
	BLOCK_CONNECTION,
	/** for when there is no type of connection whatsoever, null should not be used! */
	NONE;

	/** @param type given CableType
	 * @return if the given CableType can connect to the current one. */
	public boolean canConnect(CableType type) {
		if (this == BLOCK_CONNECTION) {
			return true;
		}
		switch (type) {
		case NONE:
			return false;
		case BLOCK_CONNECTION:
			return true;
		default:
			return type == this;
		}
	}

	/** @return if this Cable Type has unlimited connections, will be false for normal Data Cables or if there are no connections */
	public boolean hasUnlimitedConnections() {
		if (this == DATA_CABLE || this == NONE) {
			return false;
		}
		return true;
	}
}
