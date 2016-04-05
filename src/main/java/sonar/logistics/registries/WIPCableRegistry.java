package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.integration.fmp.FMPHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.IDataCable;

public class WIPCableRegistry {

	private static ArrayList<Integer> empty = new ArrayList();
	private static Map<Integer, ArrayList<BlockCoords>> cables = new THashMap<Integer, ArrayList<BlockCoords>>();
	private static Map<Integer, ArrayList<BlockCoords>> connections = new THashMap<Integer, ArrayList<BlockCoords>>();

	public static int createNetwork() {
		for (Integer integer : (ArrayList<Integer>) empty.clone()) {
			cables.put(integer, new ArrayList());
			connections.put(integer, new ArrayList());
			return integer;
		}
		int size = cables.size();
		cables.put(size, new ArrayList());
		connections.put(size, new ArrayList());
		return size;
	}

	public static void validateArray(Map<Integer, ArrayList<BlockCoords>> all, int array) {
		if (all.get(array) == null) {
			all.put(array, new ArrayList());
		}
	}

	public static void addConnection(int registryID, BlockCoords coords) {
		if (registryID != -1 && coords != null) {
			try {
				validateArray(connections, registryID);
				if (!coords.contains(connections.get(registryID))) {
					connections.get(registryID).add(coords);
				}
				CacheRegistry.refreshCache(registryID, registryID);
			} catch (Exception exception) {
				Logistics.logger.error("Failed to add connection to : " + registryID + " : " + exception.getLocalizedMessage());
			}
		}
	}

	public static void removeConnection(int registryID, BlockCoords coords) {
		if (registryID != -1 && coords != null) {
			try {
				validateArray(connections, registryID);
				for (BlockCoords coord : (ArrayList<BlockCoords>) connections.get(registryID).clone()) {
					if (coords.equals(coord)) {
						connections.get(registryID).remove(coord);
					}
				}
				CacheRegistry.refreshCache(registryID, registryID);
			} catch (Exception exception) {
				Logistics.logger.error("Failed to remove connection from : " + registryID + " : " + exception.getLocalizedMessage());
			}
		}
	}

	public static void addCable(int registryID, IDataCable cable) {
		if (registryID != -1 && cable.getCoords() != null) {
			try {
				validateArray(cables, registryID);
				if (!cable.getCoords().contains(cables.get(registryID))) {
					cables.get(registryID).add(cable.getCoords());
				}
				boolean updateNetwork = false;
				for (int i = 0; i < 6; i++) {
					ForgeDirection dir = ForgeDirection.getOrientation(i);
					if (!cable.isBlocked(dir.getOpposite())) {
						BlockCoords adj = BlockCoords.translateCoords(cable.getCoords(), dir);
						Object target = FMPHelper.getTile(adj.getTileEntity());
						if (target != null && target instanceof IDataCable) {
							IDataCable adjCable = (IDataCable) target;
							if (!adjCable.isBlocked(dir) && adjCable.registryID() != cable.registryID()) {
								int networkID = adjCable.registryID();
								cables.get(registryID).addAll(getCables(networkID));
								cables.get(networkID).clear();
								updateNetwork = true;
							}
						}
					}
				}
				if (updateNetwork) {
					updateNetwork(registryID);
				}
				CacheRegistry.refreshCache(registryID, registryID);
			} catch (Exception exception) {
				Logistics.logger.error("Failed to add cable to : " + registryID + " : " + exception.getLocalizedMessage());
			}
		}
	}

	public static void removeCable(int registryID, IDataCable cable) {
		if (registryID != -1 && cable.getCoords() != null) {
			try {
				validateArray(connections, registryID);
				for (BlockCoords coord : (ArrayList<BlockCoords>) connections.get(registryID).clone()) {
					if (cable.getCoords().equals(coord)) {
						connections.get(registryID).remove(coord);
					}
				}
				CacheRegistry.refreshCache(registryID, registryID);
			} catch (Exception exception) {
				Logistics.logger.error("Failed to remove cable from : " + registryID + " : " + exception.getLocalizedMessage());
			}
		}
	}

	public static void updateNetwork(int registryID) {
		validateArray(cables, registryID);
		ArrayList<Integer> toClear = new ArrayList();
		for (BlockCoords coords : (ArrayList<BlockCoords>) cables.get(registryID).clone()) {
			Object target = FMPHelper.getTile(coords.getTileEntity());
			if (target != null && target instanceof IDataCable) {
				IDataCable cable = (IDataCable) target;
				if (cable.getCableType().canConnect(cable.getCableType()) && cable.registryID() != registryID) {
					//toClear.add(cable.registryID());
					//cable.removeCable();
					//cable.addCable();
					cable.setRegistryID(registryID);
					cable.refreshConnections();
					
					//cable.refreshConnections();
				}
			}
		}
		for (Integer id : toClear) {
			connections.remove(id);
		}
	}

	public static ArrayList<BlockCoords> getCables(int registryID) {
		return cables.get(registryID) == null ? new ArrayList() : cables.get(registryID);
	}

	public static ArrayList<BlockCoords> getConnections(int registryID) {
		return connections.get(registryID) == null ? new ArrayList() : connections.get(registryID);
	}

	public static void removeAll() {
		cables.clear();
		connections.clear();
	}

}
