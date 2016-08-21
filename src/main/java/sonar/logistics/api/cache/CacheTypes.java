package sonar.logistics.api.cache;

import java.util.ArrayList;

import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.monitor.ILogicMonitor;

public enum CacheTypes {

	/** any type of {@link IDataCable} */
	CABLE,
	/** a multipart cable which has other types connected to it **/
	CONNECTED_CABLE,
	/** for all external blocks, this is normally not applicable to Machines, unless they provide storage, energy or fluid */
	BLOCK,
	/** for all {@link IInfoEmitter}s and other custom blocks which can provide Info */
	EMITTER,
	/** for all {@link ILogicTile}s and other custom blocks which should be seen as a part of the Logistics Network */
	NETWORK,
	/** for all {@link IChannelProvider}s and other custom blocks which can provide connections to other blocks/networks */
	CHANNELLED,
	MONITOR;

	public static boolean checkType(CacheTypes type, Object tile) {
		switch (type) {
		//case BLOCK:
		//	break;
		case CONNECTED_CABLE:
			return tile instanceof IDataCable && ((IDataCable)tile).hasConnections();
		case CABLE:				
			return tile instanceof IDataCable && !((IDataCable)tile).hasConnections();
		case CHANNELLED:
			return false; //tile instanceof IChannelProvider;
		case EMITTER:
			return false;// tile instanceof IInfoEmitter;
		case NETWORK:
			return tile instanceof ILogicTile;
		case MONITOR:
			return tile instanceof ILogicMonitor;
		default:
			return false;
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
