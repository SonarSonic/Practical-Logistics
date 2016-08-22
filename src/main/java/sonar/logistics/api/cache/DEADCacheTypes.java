package sonar.logistics.api.cache;

import java.util.ArrayList;

import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.monitor.ILogicMonitor;

public enum DEADCacheTypes {

	/** any type of {@link IDataCable} */
	CABLE(IDataCable.class),
	/** a multipart cable which has other types connected to it **/
	CONNECTED_CABLE(IDataCable.class),
	/** for all external blocks, this is normally not applicable to Machines, unless they provide storage, energy or fluid */
	BLOCK(IDataCable.class),
	/** for all {@link IInfoEmitter}s and other custom blocks which can provide Info */
	/** for all {@link ILogicTile}s and other custom blocks which should be seen as a part of the Logistics Network */
	NETWORK(ILogicTile.class),
	/** for all {@link IChannelProvider}s and other custom blocks which can provide connections to other blocks/networks */
	MONITOR(ILogicMonitor.class);

	public Class<? extends ILogicTile> logicType;

	CacheTypes(Class<? extends ILogicTile> type) {
		this.logicType = type;
	}

	public static boolean checkType(CacheTypes type, Object tile) {
		if (tile == null || !type.logicType.isInstance(tile)) {
			return false;
		}
		switch (type) {
		case CONNECTED_CABLE:
			return ((IDataCable) tile).hasConnections();
		case CABLE:
			return !((IDataCable) tile).hasConnections();
		default:
			return true;
		}
	}

	public static ArrayList<CacheTypes> getTypesForTile(Object tile) {
		ArrayList<CacheTypes> tileTypes = new ArrayList<CacheTypes>();
		for (CacheTypes type : CacheTypes.values()) {
			if (checkType(type, tile)) {
				tileTypes.add(type);
			}
		}
		return tileTypes;
	}

}
