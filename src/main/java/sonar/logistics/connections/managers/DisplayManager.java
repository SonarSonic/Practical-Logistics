package sonar.logistics.connections.managers;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.ConnectableType;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ILargeDisplay;

public class DisplayManager extends AbstractConnectionManager<ILargeDisplay> {
	
	

	@Override
	public void onNetworksConnected(int newID, int oldID) {
		ConnectedDisplayScreen screen = Logistics.getServerManager().getConnectedDisplays().get(newID);
		Logistics.getServerManager().getConnectedDisplays().remove(oldID);
		if (screen != null) {
			screen.setHasChanged();
		} else
			Logistics.logger.error("CONNECTED DISPLAY SCREEN SHOULD NOT BE NULL!");
	}

	@Override
	public void onConnectionAdded(int registryID, ILargeDisplay added) {
		ConnectedDisplayScreen screen = Logistics.getServerManager().getConnectedDisplays().get(registryID);
		if (screen == null) {
			Logistics.getServerManager().getConnectedDisplays().put(registryID, screen = new ConnectedDisplayScreen(added));
		}
		screen.setHasChanged();
	}

	public Pair<ConnectableType, Integer> getConnectionType(ILargeDisplay source, World world, BlockPos pos, EnumFacing dir, ConnectableType cableType) {
		IInfoDisplay display = LogisticsAPI.getCableHelper().getDisplayScreen(new BlockCoords(pos.offset(dir)), source.getFace());
		if (display != null && display instanceof ILargeDisplay) {
			ILargeDisplay largeDisplay = (ILargeDisplay) display;
			if (largeDisplay.getFace().equals(source.getFace()) && largeDisplay.canConnectOnSide(dir)) {
				return new Pair(ConnectableType.CONNECTION, largeDisplay.getRegistryID());
			}
		}
		return new Pair(ConnectableType.NONE, -1);
	}

	public Pair<ConnectableType, Integer> getConnectionTypeFromObject(ILargeDisplay source, Object connection, EnumFacing dir, ConnectableType cableType) {
		return new Pair(ConnectableType.NONE, -1);
	}

	public void tick() {
		Logistics.getServerManager().getConnectedDisplays().entrySet().forEach(entry -> entry.getValue().update(entry.getKey()));
	}

	@Override
	public void onConnectionRemoved(int registryID, ILargeDisplay added) {
		Logistics.getServerManager().removeDisplay(added);
		if (this.getConnections(registryID).isEmpty()) {
			Logistics.getServerManager().getConnectedDisplays().remove(registryID);
		} else {
			ConnectedDisplayScreen screen = Logistics.getServerManager().getConnectedDisplays().get(registryID);
			screen.setHasChanged();
		}

	}

}
