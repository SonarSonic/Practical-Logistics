package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;

import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IInfoEmitter;

public class CableRegistry {

	private static Map<Integer, ArrayList<BlockCoords>> cables = new THashMap<Integer, ArrayList<BlockCoords>>();
	private static Map<Integer, ArrayList<BlockCoords>> connections = new THashMap<Integer, ArrayList<BlockCoords>>();

	public static void removeAll() {
		connections.clear();
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
		if (registryID == -1) {
			return new ArrayList();
		}
		ArrayList<BlockCoords> coords = cables.get(registryID);
		if (coords == null) {
			return new ArrayList();
		}
		return coords;
	}

	public static ArrayList<BlockCoords> getConnections(int registryID) {
		if (registryID == -1) {
			return new ArrayList();
		}
		ArrayList<BlockCoords> coords = connections.get(registryID);
		if (coords == null) {
			return new ArrayList();
		}
		return coords;
	}

	public static void addCables(int registryID, ArrayList<BlockCoords> cables) {
		for (BlockCoords coords : cables) {
			addCable(registryID, coords);
		}
	}

	public static void addCable(int registryID, BlockCoords cable) {
		if (registryID != -1 && cable != null) {
			Object target = FMPHelper.checkObject(cable.getTileEntity());
			if (target != null && target instanceof IDataCable) {
				if (cables.get(registryID) == null) {
					cables.put(registryID, new ArrayList());
					cables.get(registryID).add(cable);
					((IDataCable) target).setRegistryID(registryID);
					return;

				}
				ArrayList<BlockCoords> removeList = new ArrayList();
				for (BlockCoords coords : cables.get(registryID)) {
					if (BlockCoords.equalCoords(coords, cable)) {
						return;
					}
				}
				cables.get(registryID).add(cable);
				((IDataCable) target).setRegistryID(registryID);
				CacheRegistry.refreshCache(registryID, registryID);
			}
		}
	}

	public static void addConnections(int registryID, ArrayList<BlockCoords> connections) {
		for (BlockCoords coords : connections) {
			addConnection(registryID, coords);
		}
	}

	public static void addConnection(int registryID, BlockCoords connection) {
		if (registryID != -1 && connection != null) {
			if (connections.get(registryID) == null) {
				connections.put(registryID, new ArrayList());
				connections.get(registryID).add(connection);
				CacheRegistry.refreshCache(registryID, registryID);
				return;
			}
			ArrayList<BlockCoords> removeList = new ArrayList();
			for (BlockCoords coords : connections.get(registryID)) {
				if (BlockCoords.equalCoords(coords, connection)) {
					return;
				}
			}
			connections.get(registryID).add(connection);
			CacheRegistry.refreshCache(registryID, registryID);
		}
	}

	public static void removeCable(int registryID, IDataCable cable) {
		if (registryID != -1 && cable.getCoords() != null) {
			if (cables.get(registryID) == null) {
				return;
			}
			ArrayList<BlockCoords> removeList = new ArrayList();
			for (BlockCoords coords : cables.get(registryID)) {
				if (BlockCoords.equalCoords(coords, cable.getCoords())) {
					removeList.add(coords);
				}
			}
			for (BlockCoords remove : removeList) {
				cables.get(registryID).remove(remove);
			}

			ArrayList<BlockCoords> oldCables = new ArrayList();
			if (cables.get(registryID) != null) {
				oldCables.addAll(cables.get(registryID));
				cables.get(registryID).clear();
			}

			ArrayList<BlockCoords> oldConnections = new ArrayList();
			if (connections.get(registryID) != null) {
				oldConnections.addAll(connections.get(registryID));
				connections.get(registryID).clear();
			}

			int newID = getNextAvailableID();
			for (BlockCoords oldCable : oldCables) {
				Object target = FMPHelper.checkObject(oldCable.getTileEntity());
				if (target != null && target instanceof IDataCable) {
					IDataCable tile = (IDataCable) target;
					tile.setRegistryID(-1);
				}
			}
			for (BlockCoords oldCable : oldCables) {
				Object target = FMPHelper.checkObject(oldCable.getTileEntity());
				if (target != null && target instanceof IDataCable) {
					IDataCable tile = (IDataCable) target;
					tile.addCable();
				}
			}
			for (BlockCoords coords : oldConnections) {
				Object target = FMPHelper.checkObject(coords.getTileEntity());
				if (target != null && target instanceof IInfoEmitter) {
					IInfoEmitter tile = (IInfoEmitter) target;
					tile.removeConnections();
					tile.addConnections();
				}
			}
			
			//CacheRegistry.refreshCache(registryID, newID);
		}
	}

	public static void connectNetworks(int newID, int secondaryID) {
		ArrayList<BlockCoords> oldCables = new ArrayList();
		if (cables.get(secondaryID) != null) {
			oldCables.addAll(cables.get(secondaryID));
			cables.get(secondaryID).clear();
		}

		ArrayList<BlockCoords> oldConnections = new ArrayList();
		if (connections.get(secondaryID) != null) {
			oldConnections.addAll(connections.get(secondaryID));
			connections.get(secondaryID).clear();
		}
		addCables(newID, oldCables);
		addConnections(newID, oldConnections);
		CacheRegistry.refreshCache(secondaryID, newID);
	}

	public static void removeConnection(int registryID, BlockCoords connection) {
		if (registryID != -1 && connection != null) {
			if (connections.get(registryID) == null) {
				return;
			}
			ArrayList<BlockCoords> removeList = new ArrayList();
			for (BlockCoords coords : connections.get(registryID)) {
				if (BlockCoords.equalCoords(coords, connection)) {
					removeList.add(coords);
				}
			}
			for (BlockCoords remove : removeList) {
				connections.get(registryID).remove(remove);
			}
			CacheRegistry.refreshCache(registryID, registryID);
		}
	}

}