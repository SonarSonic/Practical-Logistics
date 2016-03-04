package sonar.logistics.api.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

public class EmptyNetworkCache implements INetworkCache {

	@Override
	public Entry<BlockCoords, ForgeDirection> getExternalBlock() {
		return (Entry<BlockCoords, ForgeDirection>) Collections.EMPTY_SET;
	}

	@Override
	public LinkedHashMap<BlockCoords, ForgeDirection> getExternalBlocks() {
		return new LinkedHashMap();
	}

	@Override
	public ArrayList<BlockCoords> getConnections(CacheTypes type) {
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

}
