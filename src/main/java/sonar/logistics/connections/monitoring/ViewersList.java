package sonar.logistics.connections.monitoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.ILogicViewable;
import sonar.logistics.api.info.monitor.MonitorType;

public class ViewersList {

	// protected ArrayList<MonitorViewer> viewers = new ArrayList<MonitorViewer>();
	protected HashMap<EntityPlayer, ArrayList<MonitorType>> viewers = new HashMap();
	public List<MonitorType> validTypes;
	public ILogicViewable viewable;
	public ArrayList<ILogicViewable> connectedDisplays = new ArrayList();

	public ViewersList(ILogicViewable viewable, List<MonitorType> validTypes) {
		this.viewable = viewable;
		this.validTypes = validTypes;
	}

	public boolean hasViewers() {
		return !viewers.isEmpty();
	}

	public HashMap<EntityPlayer, ArrayList<MonitorType>> getViewers(boolean includeDisplays) {
		if (includeDisplays) {
			ViewersList viewerList = new ViewersList(this.viewable, this.validTypes);
			viewerList.viewers = this.viewers;
			viewerList.connectedDisplays = this.connectedDisplays;
			if (includeDisplays) {
				connectedDisplays.forEach(viewable -> {
					if (viewable != null) {
						viewable.getViewersList().getViewers(false).entrySet().forEach(viewer -> {
							addViewerToMap(viewerList, viewer.getKey(), viewer.getValue());
						});
					}
				});
			}
			return viewerList.viewers;
		}
		return viewers;
	}

	public ArrayList<EntityPlayer> getViewers(boolean includeDisplays, MonitorType... types) {
		ArrayList<EntityPlayer> players = new ArrayList();
		viewers: for (Entry<EntityPlayer, ArrayList<MonitorType>> viewers : getViewers(includeDisplays).entrySet()) {
			for (MonitorType type : types) {
				if (viewers.getValue().contains(type)) {
					players.add(viewers.getKey());
					continue viewers;
				}
			}
		}
		return players;
	}

	public void addViewer(EntityPlayer player, MonitorType type) {
		this.addViewer(player, Lists.newArrayList(type));
	}

	public void addViewer(EntityPlayer player, ArrayList<MonitorType> typesToAdd) {
		if (addViewerToMap(this, player, typesToAdd)) {
			viewable.onViewerAdded(player, viewers.get(player));
		}
	}

	public void removeViewer(EntityPlayer player, MonitorType type) {
		this.removeViewer(player, Lists.newArrayList(type));
	}

	public void removeViewer(EntityPlayer player, ArrayList<MonitorType> typesToRemove) {
		if (removeViewerFromMap(this, player, typesToRemove)) {
			viewable.onViewerRemoved(player, viewers.get(player));
		}
	}

	public static boolean addViewerToMap(ViewersList viewerList, EntityPlayer player, ArrayList<MonitorType> types) {
		ArrayList<MonitorType> playerTypes = viewerList.viewers.get(player);
		boolean shouldAdd = playerTypes != null;
		if (!shouldAdd) {
			playerTypes = types;
			//shouldAdd = false;
		}
		for (MonitorType type : (ArrayList<MonitorType>) playerTypes.clone()) {
			if (!viewerList.validTypes.contains(type)) {
				playerTypes.remove(type);
			}
		}
		if (playerTypes.isEmpty()) {
			return false;
		}
		boolean added = false;
		if (shouldAdd) {
			for (MonitorType type : types) {
				if (!playerTypes.contains(type)) {
					playerTypes.add(type);
					added = true;
				}
			}
		} else {
			viewerList.viewers.put(player, playerTypes);
			added=true;
		}
		return added;

	}

	public static boolean removeViewerFromMap(ViewersList viewerList, EntityPlayer player, ArrayList<MonitorType> types) {
		ArrayList<MonitorType> playerTypes = viewerList.viewers.get(player);
		if (playerTypes != null) {
			types.forEach(playerTypes::remove);
			if (playerTypes.isEmpty()) {
				viewerList.viewers.remove(player);
			}
			return true;
		}
		return false;

	}

}
