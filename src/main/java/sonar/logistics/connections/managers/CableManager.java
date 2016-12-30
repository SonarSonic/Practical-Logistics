package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import gnu.trove.map.hash.THashMap;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack.Consumable;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IDataCable;

/** FIX MEEE */
public class CableManager {

	private static Map<Integer, ArrayList<IDataCable>> cables = new ConcurrentHashMap<Integer, ArrayList<IDataCable>>();

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

	public static ArrayList<IDataCable> getCables(int registryID) {
		ArrayList<IDataCable> coords;
		return (registryID == -1 || (coords = cables.get(registryID)) == null) ? Lists.newArrayList() : coords;
	}

	public static void addCables(int registryID, ArrayList<IDataCable> cables) {
		cables.forEach(cable -> addCable(registryID, cable, false));
		NetworkManager.markNetworkDirty(registryID, RefreshType.FULL);
	}

	public static void addCable(int registryID, IDataCable cable, boolean refreshCache) {
		if (registryID != -1 && cable != null) {
			ArrayList<IDataCable> network = cables.get(registryID);
			cable.setRegistryID(-1);
			if (network == null) {
				cables.put(registryID, new ArrayList());
				network = cables.get(registryID);
			}
			if (network!=null && network.contains(cable))
				return;
			cable.setRegistryID(registryID);
			network.add(cable);
			if (refreshCache) {
				NetworkManager.markNetworkDirty(registryID, RefreshType.FULL);
			}
		}
	}

	public static void removeCable(int registryID, IDataCable cable) {
		if (registryID != -1 && cable.getCoords() != null) {
			ArrayList<IDataCable> allCables = cables.get(registryID);
			if (allCables == null) {
				return;
			}
			allCables.remove(cable);
			int newID = getNextAvailableID();
			allCables = (ArrayList<IDataCable>) allCables.clone(); // save all the current cables.
			cables.get(registryID).clear(); // clear all cables currently connected

			ArrayList<Integer> newNetworks = new ArrayList();
			allCables.forEach(oldCable -> oldCable.setRegistryID(-1));
			allCables.forEach(oldCable -> {
				oldCable.addCable();
				newNetworks.add(oldCable.getNetworkID());
			});

		}
	}

	public static void connectNetworks(int newID, int secondaryID) {
		ArrayList<IDataCable> oldCables = cables.getOrDefault(secondaryID, new ArrayList());
		addCables(newID, oldCables);
		oldCables.clear();
		NetworkManager.connectNetworks(secondaryID, newID);
	}

}