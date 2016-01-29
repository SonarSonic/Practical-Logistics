package sonar.logistics.api.connecting;

import sonar.logistics.api.Info;

/**implemented by any TileEntity which can provide info, blocks which connect to cables should implement this.*/
public interface IInfoTile extends ILogicTile {
	
	/**if the block provides no special function this should be a bit of Info saying what the current block is*/
	public Info currentInfo();
	
}
