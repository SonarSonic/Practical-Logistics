package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Lists;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.ConnectableType;
import sonar.logistics.api.connecting.INetworkConnectable;

public abstract class AbstractConnectionManager<T extends INetworkConnectable> {

	private Map<Integer, ArrayList<T>> connections = new ConcurrentHashMap<Integer, ArrayList<T>>();
	private NetworkManager NetworkManager;
	
	public void removeAll() {
		connections.clear();
	}
	
	public NetworkManager NetworkManager(){
		if(NetworkManager==null){
			NetworkManager = Logistics.instance.networkManager;
		}
		return NetworkManager;
	}

	public int getNextAvailableID() {
		for (int i = 0; i < connections.size(); i++) {
			if (connections.get(i) == null || connections.get(i).isEmpty() || connections.get(i).size() == 0) {
				return i;
			}
		}
		return connections.size();
	}

	public ArrayList<T> getConnections(int registryID) {
		ArrayList<T> coords;
		return (registryID == -1 || (coords = connections.get(registryID)) == null) ? Lists.newArrayList() : coords;
	}

	public void addConnections(int registryID, ArrayList<T> connections) {
		connections.forEach(connection -> addConnection(registryID, connection, false));
	}

	public int addConnection(T cable) {
		ArrayList<Pair<ConnectableType, Integer>> connections = new ArrayList();
		int cableID = -1;
		int lastSize = -1;
		BlockCoords coords = cable.getCoords();
		for (EnumFacing dir : EnumFacing.values()) {
			if (cable.canConnectOnSide(dir)) {
				Pair<ConnectableType, Integer> connection = getConnectionType(cable, coords.getWorld(), coords.getBlockPos(), dir, cable.getCableType());
				if (connection.a != ConnectableType.NONE && connection.b != -1) {
					List<T> cables = getConnections(connection.b);
					if (cables.size() > lastSize) {
						cableID = connection.b;
						lastSize = cables.size();
					}
					connections.add(connection);
				}
			}
		}
		addConnection(cableID == -1 ? cableID = getNextAvailableID() : cableID, cable, true);
		for (Pair<ConnectableType, Integer> connection : connections) {
			if (connection.b != cableID) {
				connectNetworks(cableID, connection.b);
			}
		}
		return cableID;
	}

	public void addConnection(int registryID, T connection, boolean refreshCache) {
		if (registryID != -1 && connection != null) {
			ArrayList<T> network = connections.get(registryID);
			connection.setRegistryID(-1);
			if (network == null) {
				connections.put(registryID, new ArrayList());
				network = connections.get(registryID);
			}
			if (network != null && network.contains(connection))
				return;
			connection.setRegistryID(registryID);
			network.add(connection);
			if (refreshCache) {
				onConnectionAdded(registryID, connection);
			}
		}
	}

	public void removeConnection(int registryID, T connection) {
		if (registryID != -1 && connection.getCoords() != null) {
			ArrayList<T> allConnections = connections.get(registryID);
			if (allConnections == null) {
				return;
			}
			allConnections.remove(connection);
			int newID = getNextAvailableID();
			allConnections = (ArrayList<T>) allConnections.clone(); // save all the current cables.
			connections.get(registryID).clear(); // clear all cables currently connected

			ArrayList<Integer> newNetworks = new ArrayList();
			allConnections.forEach(oldCable -> oldCable.setRegistryID(-1));
			allConnections.forEach(oldCable -> {
				oldCable.addToNetwork();
				newNetworks.add(oldCable.getRegistryID());
			});
			this.onConnectionRemoved(registryID, connection);
		}
	}

	public void refreshConnections(T cable) {
		BlockCoords coords = cable.getCoords();
		for (EnumFacing dir : EnumFacing.values()) {
			Pair<ConnectableType, Integer> connection = getConnectionType(cable, coords.getWorld(), coords.getBlockPos(), dir, cable.getCableType());
			boolean canConnect = cable.canConnectOnSide(dir);
			if ((!canConnect && connection.a.canConnect(cable.getCableType()))) {
				cable.removeFromNetwork();
				cable.addToNetwork();
			} else if ((canConnect && connection.a.canConnect(cable.getCableType()) && connection.b != cable.getRegistryID())) {
				connectNetworks(cable.getRegistryID(), connection.b);
			}
		}
	}
	
	public abstract Pair<ConnectableType, Integer> getConnectionType(T source, World world, BlockPos pos, EnumFacing dir, ConnectableType cableType);

	public void connectNetworks(int newID, int secondaryID) {
		ArrayList<T> oldConnections = connections.getOrDefault(secondaryID, new ArrayList());
		addConnections(newID, oldConnections);
		oldConnections.clear();
		this.onNetworksConnected(newID, secondaryID);
	}


	//public abstract Pair<ConnectableType, Integer> getConnectionTypeFromObject(T source, Object connection, EnumFacing dir, ConnectableType cableType);

	public abstract void onNetworksConnected(int newID, int oldID);

	public abstract void onConnectionAdded(int registryID, T added);

	public abstract void onConnectionRemoved(int registryID, T added);

}