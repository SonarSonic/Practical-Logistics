package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import gnu.trove.map.hash.THashMap;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.Consumable;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IDataCable;

public class CableRegistry {

	private static Map<Integer, ArrayList<BlockCoords>> cables = new THashMap<Integer, ArrayList<BlockCoords>>();

	public static void removeAll() {
		cables.clear();
	}

	public static int getNextAvailableID() {
		for (int i = 0; i < cables.size(); i++) {
			if (cables.get(i) == null || cables.get(i).isEmpty() || cables.get(i).size() == 0) {
				return i;
			}
		}
		return cables.size();
	}

	public static ArrayList<BlockCoords> getCables(int registryID) {
		ArrayList<BlockCoords> coords;
		return (registryID == -1 || (coords = cables.get(registryID)) == null) ? Lists.newArrayList() : coords;
	}

	public static void addCables(int registryID, ArrayList<IDataCable> cables) {
		cables.forEach(cable -> addCable(registryID, cable, false));
		CacheRegistry.refreshCache(registryID, registryID);
	}

	public static void addCable(int registryID, IDataCable cable, boolean refreshCache) {
		if (registryID != -1 && cable != null) {
			ArrayList<BlockCoords> network = cables.get(registryID) == null ? cables.put(registryID, new ArrayList()) : cables.get(registryID);

			cables.get(registryID).iterator().forEachRemaining(coords -> {
				if (BlockCoords.equalCoords(coords, cable.getCoords())) {
					return;
				}
			});

			cables.get(registryID).add(cable.getCoords());
			cable.setRegistryID(registryID);
			if (refreshCache)
				CacheRegistry.refreshCache(registryID, registryID);
		}
	}

	public static void removeCable(int registryID, IDataCable cable) {
		if (registryID != -1 && cable.getCoords() != null) {
			if (cables.get(registryID) == null) {
				return;
			}
			Iterator<BlockCoords> iterator = cables.get(registryID).iterator();
			while (iterator.hasNext()) {
				BlockCoords coords = iterator.next();
				if (BlockCoords.equalCoords(coords, cable.getCoords())) {
					iterator.remove();
				}
			}

			ArrayList<BlockCoords> oldCables = new ArrayList();
			if (cables.get(registryID) != null) {
				oldCables.addAll(cables.get(registryID));
				cables.get(registryID).clear();
			}

			int newID = getNextAvailableID();
			oldCables.forEach(oldCable -> {
				IDataCable target = LogisticsAPI.getCableHelper().getCableFromCoords(oldCable);
				if (target != null)
					target.setRegistryID(-1);
			});

			oldCables.forEach(oldCable -> {
				IDataCable target = LogisticsAPI.getCableHelper().getCableFromCoords(oldCable);
				if (target != null)
					target.addCable();
			});
		}
	}

	public static void connectNetworks(int newID, int secondaryID) {
		ArrayList<IDataCable> oldCables = new ArrayList();
		if (cables.get(secondaryID) != null) {
			cables.get(secondaryID).forEach(coords -> {
				IDataCable cable = LogisticsAPI.getCableHelper().getCableFromCoords(coords);
				if (cable != null) {
					oldCables.add(cable);
				}
			});
		}
		addCables(newID, oldCables);
		CacheRegistry.refreshCache(secondaryID, newID);
	}

}