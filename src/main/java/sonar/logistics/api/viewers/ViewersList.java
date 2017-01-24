package sonar.logistics.api.viewers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.ILogicViewable;

public class ViewersList implements IViewersList {

	// protected ArrayList<MonitorViewer> viewers = new ArrayList<MonitorViewer>();
	protected HashMap<EntityPlayer, ArrayList<MonitorTally>> viewers = new HashMap();
	public List<ViewerType> validTypes;
	public ILogicViewable viewable;
	public ArrayList<ILogicViewable> connectedDisplays = new ArrayList();



	public ViewersList(ILogicViewable viewable, List<ViewerType> validTypes) {
		this.viewable = viewable;
		this.validTypes = validTypes;
	}

	public void cloneFrom(ViewersList list) {
		viewers = list.viewers;
		validTypes = list.validTypes;
		connectedDisplays = list.connectedDisplays;
	}

	public boolean hasViewers() {
		return !viewers.isEmpty();
	}

	public HashMap<EntityPlayer, ArrayList<MonitorTally>> getViewers(boolean includeDisplays) {
		if (includeDisplays) {
			ViewersList viewerList = new ViewersList(this.viewable, this.validTypes);
			viewerList.viewers = (HashMap<EntityPlayer, ArrayList<MonitorTally>>) this.viewers.clone();
			viewerList.connectedDisplays = (ArrayList<ILogicViewable>) this.connectedDisplays.clone();
			if (includeDisplays) {
				connectedDisplays.forEach(viewable -> {
					if (viewable != null) {
						viewable.getViewersList().getViewers(false).entrySet().forEach(viewer -> {
							addTalliesToMap(viewerList, viewer.getKey(), viewer.getValue());
						});
					}
				});
			}
			return viewerList.viewers;
		}
		return viewers;
	}

	public ArrayList<EntityPlayer> getViewers(boolean includeDisplays, ViewerType... types) {
		ArrayList<EntityPlayer> players = new ArrayList();
		viewers: for (Entry<EntityPlayer, ArrayList<MonitorTally>> viewers : getViewers(includeDisplays).entrySet()) {
			for (ViewerType type : types) {
				for (MonitorTally tally : viewers.getValue()) {
					if (tally.type == type) {
						players.add(viewers.getKey());
						continue viewers;
					}
				}
			}
		}
		return players;
	}

	public void addViewer(EntityPlayer player, ViewerType type) {
		this.addViewer(player, Lists.newArrayList(type));
	}

	public void addViewer(EntityPlayer player, ArrayList<ViewerType> typesToAdd) {
		if (addViewerToMap(this, player, typesToAdd)) {
			viewable.onViewerAdded(player, viewers.get(player));
		}
	}

	public void removeViewer(EntityPlayer player, ViewerType type) {
		this.removeViewer(player, Lists.newArrayList(type));
	}

	public void removeViewer(EntityPlayer player, ArrayList<ViewerType> typesToRemove) {
		if (removeViewerFromMap(this, player, typesToRemove)) {
			viewable.onViewerRemoved(player, viewers.get(player));
		}
	}

	public static boolean addViewerToMap(ViewersList viewerList, EntityPlayer player, ArrayList<ViewerType> types) {
		ArrayList<ViewerType> currentTypes = (ArrayList<ViewerType>) types.clone();
		ArrayList<MonitorTally> playerTypes = viewerList.viewers.get(player);
		if (playerTypes == null) {
			ArrayList<MonitorTally> newTallies = new ArrayList();
			for (ViewerType type : currentTypes) {
				if (viewerList.validTypes.contains(type)) {
					newTallies.add(new MonitorTally(type, 1));
				}
			}
			viewerList.viewers.put(player, newTallies);
		} else {
			ArrayList<MonitorTally> toAdd = new ArrayList();
			for (ViewerType type : (ArrayList<ViewerType>) currentTypes.clone()) {
				if (viewerList.validTypes.contains(type)) {
					boolean added = false;
					for (MonitorTally tally : playerTypes) {
						if (tally.type == type) {
							tally.value += 1;
							currentTypes.remove(type);
							added = true;
							break;
						}
					}
					if (!added) {
						toAdd.add(new MonitorTally(type, 1));
					}
				}
			}
			toAdd.forEach(tally -> viewerList.viewers.get(player).add(tally));
		}
		return currentTypes.size() < types.size();
	}

	public static void addTalliesToMap(ViewersList viewerList, EntityPlayer player, ArrayList<MonitorTally> tallies) {
		ArrayList<MonitorTally> playerTypes = viewerList.viewers.get(player);
		if (playerTypes == null) {
			viewerList.viewers.put(player, (ArrayList<MonitorTally>) tallies.clone());
		} else {
			for (MonitorTally tally : tallies) {
				boolean found = false;
				for (MonitorTally currentTally : playerTypes) {
					if (tally.type == currentTally.type) {
						// currentTally.value += tally.value;
						found = true;
						break;
					}
				}
				if (!found) {
					playerTypes.add(tally);
				}
			}
		}
	}

	public static boolean removeViewerFromMap(ViewersList viewerList, EntityPlayer player, ArrayList<ViewerType> types) {
		ArrayList<MonitorTally> playerTypes = viewerList.viewers.get(player);
		if (playerTypes != null) {
			types: for (ViewerType type : types) {
				for (MonitorTally tally : playerTypes) {
					if (tally.type == type) {
						tally.value -= 1;
						if (tally.value <= 0) {
							playerTypes.remove(tally);
							continue types;
						}
					}
				}
			}
			// types.forEach(playerTypes::remove);
			if (playerTypes.isEmpty()) {
				viewerList.viewers.remove(player);
			}
			return playerTypes.size() < types.size();
		}
		return false;

	}

	@Override
	public ArrayList<ILogicViewable> getConnectedDisplays() {
		return connectedDisplays;
	}

}
