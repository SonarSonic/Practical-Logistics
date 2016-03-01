package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.NetworkCache;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IInfoEmitter;

public final class CacheRegistry {

	private static Map<Integer, INetworkCache> cache = new THashMap<Integer, INetworkCache>();

	public static void removeAll() {
		cache.clear();
	}

	public static Entry<BlockCoords, ForgeDirection> getFirstConnection(int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null) {
			return network.getFirstConnection();
		} else
			return (Entry<BlockCoords, ForgeDirection>) Collections.EMPTY_SET;
	}

	public static ArrayList<BlockCoords> getCacheList(CacheTypes type, int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null)
			return network.getCacheList(type);
		else
			return (ArrayList<BlockCoords>) Collections.EMPTY_LIST;
	}

	public static LinkedHashMap<BlockCoords, ForgeDirection> getChannelArray(int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null)
			return network.getChannelArray();
		else
			return (LinkedHashMap<BlockCoords, ForgeDirection>) Collections.EMPTY_MAP;
	}

	public static INetworkCache getCache(int networkID) {
		return cache.get(networkID);
	}

	public static void refreshCache(int networkID) {
		INetworkCache networkCache = cache.get(networkID);
		if (networkCache != null) {
			networkCache.refreshCache(networkID);
		}
		NetworkCache network = new NetworkCache();
		network.refreshCache(networkID);
		cache.put(networkID, network);
		Logistics.logger.debug("refreshed cache");
	}
}
