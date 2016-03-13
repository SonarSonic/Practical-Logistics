package sonar.logistics.api.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

/** an implementation of {@link INetworkCache} that acts as an Empty Network, when working with networks an instance of this should be returned instead of null */
public class EmptyNetworkCache implements INetworkCache {

	@Override
	public Entry<BlockCoords, ForgeDirection> getExternalBlock(boolean includeChannels) {
		return null;
	}

	@Override
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks(boolean includeChannels) {
		return new LinkedHashMap();
	}

	@Override
	public ArrayList<BlockCoords> getConnections(CacheTypes type, boolean includeChannels) {
		return new ArrayList();
	}

	@Override
	public BlockCoords getFirstConnection(CacheTypes type) {
		return null;
	}

	@Override
	public Block getFirstBlock(CacheTypes type) {
		return null;
	}

	@Override
	public TileEntity getFirstTileEntity(CacheTypes type) {
		return null;
	}

	@Override
	public int getNetworkID() {
		return -1;
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks(ArrayList<Integer> networks) {
		return networks;
	}

}
