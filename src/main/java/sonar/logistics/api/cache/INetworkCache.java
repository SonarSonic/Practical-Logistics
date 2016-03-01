package sonar.logistics.api.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;

public interface INetworkCache {

	public Entry<BlockCoords, ForgeDirection> getFirstConnection();
	
	public ArrayList<BlockCoords> getCacheList(CacheTypes type);
	
	public LinkedHashMap<BlockCoords, ForgeDirection> getChannelArray();

	public void refreshCache(int networkID);
}
