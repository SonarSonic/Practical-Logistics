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
import sonar.logistics.api.cache.EmptyNetworkCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.cache.NetworkCache;

public final class CacheRegistry {

	public static final EmptyNetworkCache EMPTY_CACHE = new EmptyNetworkCache();

	private static Map<Integer, INetworkCache> cache = new THashMap<Integer, INetworkCache>();

	public static void removeAll() {
		cache.clear();
	}

	public static Entry<BlockCoords, ForgeDirection> getFirstConnection(int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null) {
			return network.getExternalBlock();
		} else
			return null;
	}

	public static ArrayList<BlockCoords> getCacheList(CacheTypes type, int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null)
			return network.getConnections(type);
		else
			return new ArrayList();
	}

	public static LinkedHashMap<BlockCoords, ForgeDirection> getChannelArray(int networkID) {
		INetworkCache network = getCache(networkID);
		if (network != null)
			return network.getExternalBlocks();
		else
			return (LinkedHashMap<BlockCoords, ForgeDirection>) Collections.EMPTY_MAP;
	}

	public static INetworkCache getCache(int networkID) {
		INetworkCache networkCache = cache.get(networkID);
		if (networkCache == null) {
			return EMPTY_CACHE;
		}
		return networkCache;
	}

	public static void refreshCache(int networkID) {
		INetworkCache networkCache = cache.get(networkID);
		if (networkCache != null && networkCache instanceof INetworkCache) {
			((NetworkCache) networkCache).refreshCache(networkID);
		}
		NetworkCache network = new NetworkCache();
		network.refreshCache(networkID);
		cache.put(networkID, network);
		Logistics.logger.debug("refreshed cache");
	}
}
