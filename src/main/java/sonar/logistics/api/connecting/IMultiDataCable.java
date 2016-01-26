package sonar.logistics.api.connecting;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.render.ICableRenderer;

/**
 * Used by Data Cables both for Tiles and Forge Multipart parts Cables don't
 * store any data but instead store the the Coordinates of the block they are
 * connected to. Therefore cables can be used as a universal transfer method for
 * any type of data
 */
public interface IMultiDataCable extends ICableRenderer, IMultiTile {

	/** the Coordinates of the currently connected Tile Entity */
	public List<BlockCoords> getCoords();

	public void setCoords(List<BlockCoords> coords);

	public void addCoords(BlockCoords coords);

	public void removeCoords(BlockCoords coords);

	/**
	 * checks if the cable can connect in a given direction It will return true
	 * if a FMP part is blocking it's connection
	 */
	public boolean isBlocked(ForgeDirection dir);

}
