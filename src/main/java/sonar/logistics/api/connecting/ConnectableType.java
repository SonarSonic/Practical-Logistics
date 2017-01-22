package sonar.logistics.api.connecting;

/** used to distinguish the different types of Cable Connections */
public enum ConnectableType {
	/** for standard Data Cables which are limited to one channel */
	CONNECTION,
	/** for Channelled Data Cables which can unlimited channels */
	/** for {@link ILogicTile} which can connect to a network */
	BLOCK_CONNECTION,
	/** for when there is no type of connection whatsoever, null should not be used! */
	NONE;

	/** @param type given CableType
	 * @return if the given CableType can connect to the current one. */
	public boolean canConnect(ConnectableType type) {
		if(type==NONE){
			return false;
		}		
		switch (this) {
		case NONE:
			return false;
		case BLOCK_CONNECTION:
			return true;
		default:
			if(type==BLOCK_CONNECTION){
				return true;
			}
			return type == this;
		}
	}
}
