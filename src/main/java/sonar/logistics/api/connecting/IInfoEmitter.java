package sonar.logistics.api.connecting;

/** implemented on Tile Entities or FMP Parts which can provide data */
public interface IInfoEmitter extends IInfoTile {

	/**
	 * use CableHelper.addConnection for each side that can connect to cables,
	 * this should be called when the Tile Entity is validated
	 */
	public void addConnections();

	/**
	 * use CableHelper.removeConnection for each side that can connect to
	 * cables, this should be called when the Tile Entity is invalidated
	 */
	public void removeConnections();
}
