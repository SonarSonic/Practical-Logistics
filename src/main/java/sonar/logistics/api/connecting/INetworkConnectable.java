package sonar.logistics.api.connecting;

import net.minecraft.util.EnumFacing;
import sonar.core.utils.IWorldPosition;

public interface INetworkConnectable extends IWorldPosition {

	/** is the cable limited by the number of channels, true for Channelled Cables, false for Data Cables */
	public ConnectableType getCableType();
	
	/** called when the cable is added to the world */
	public void addToNetwork();

	/** called when the cable is removed to the world */
	public void removeFromNetwork();	
	
	public int getRegistryID();

	public void setRegistryID(int id);
	
	/** can the Tile connect to cables on the given direction */
	public boolean canConnectOnSide(EnumFacing dir);
}
