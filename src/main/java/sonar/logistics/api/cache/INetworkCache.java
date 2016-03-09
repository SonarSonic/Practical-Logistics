package sonar.logistics.api.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

/** implemented on Logistics Network Caches, used for retrieving info about  */
public interface INetworkCache {

	/** used to get the first external block connected to the network.
	 * @param includeChannels normally true, false if you are retrieving blocks from multiple connected networks, which have already been logged
	 * @return a {@link Entry} of {@link BlockCoords} paired with {@link ForgeDirection} */
	public Entry<BlockCoords, ForgeDirection> getExternalBlock(boolean includeChannels);

	/** @param includeChannels normally true, false if you are retrieving blocks from multiple connected networks, which have already been logged
	 * @return the {@link LinkedHashMap} of {@link BlockCoords} paired with {@link ForgeDirection} */
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks(boolean includeChannels);

	/** gets the full list of Cached Coordinates for a given {@link CacheType}.
	 * @param type the {@link CacheType} you wish to retrieve.
	 * @return the {@link ArrayList}<{@link BlockCoords}> requested */
	public ArrayList<BlockCoords> getConnections(CacheTypes type);

	/** convenience method for getting the BlockCoords of the first connection from the given {@link CacheType}
	 * @param type the {@link CacheType} you wish to retrieve.
	 * @return the {@link BlockCoords} requested, may be null */
	public BlockCoords getFirstConnection(CacheTypes type);

	/** convenience method for getting the {@link Block} of the first connection from the given {@link CacheType}
	 * @param type the {@link CacheType} you wish to retrieve.
	 * @return the {@link Block} requested, may be null */
	public Block getFirstBlock(CacheTypes type);

	/** convenience method for getting the {@link TileEntity} of the first connection from the given {@link CacheType}
	 * @param type the {@link CacheType} you wish to retrieve.
	 * @return the {@link TileEntity} requested, may be null */
	public TileEntity getFirstTileEntity(CacheTypes type);

	/** @return the networkID, related to the id of the Cable Network */
	public int getNetworkID();
	
	/**used when getting full list of networks associated with this one, via {@link IChannelProvider}}
	 * @param networks an ArrayList containing all current network IDs, IDs shouldn't be removed - unless in very rare circumstances
	 * @return same ArrayList with the Networks associated with this network added
	 */
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks);
	
}
