package sonar.logistics.api.cache;

public enum CacheTypes {
	
	/**any type of {@link IDataCable}*/
	CABLE,	
	/**for all external blocks, this is normally not applicable to Machines,
	 * unless they provide storage, energy or fluid*/
	BLOCK,
	/** for all {@link IEntityNode}s or anything else which can provider Entities*/
	ENTITY_NODES, 
	/**for all {@link IInfoEmitter}s and other custom blocks which can provide Info*/
	EMITTER, 
	/**for all {@link ILogicTile}s and other custom blocks which should be seen as a part of the Logistics Network*/
	NETWORK, 
	/**for all {@link IChannelProvider}s and other custom blocks which can provide connections to other blocks/networks*/
	CHANNELLED;
	
}
