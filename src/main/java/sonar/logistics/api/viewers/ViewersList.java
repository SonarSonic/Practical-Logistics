package sonar.logistics.api.viewers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.info.monitor.ILogicViewable;

public class ViewersList implements IViewersList {

	// protected ArrayList<MonitorViewer> viewers = new ArrayList<MonitorViewer>();
	public HashMap<EntityPlayer, ArrayList<ViewerTally>> viewers = new HashMap();
	public List<ViewerType> validTypes;
	public ILogicViewable viewable;
	public ArrayList<ILogicViewable> connectedDisplays = new ArrayList();
	public ViewersList origin;

	public ViewersList(ViewersList origin, ILogicViewable viewable, List<ViewerType> validTypes) {
		this.viewable = viewable;
		this.validTypes = validTypes;
		this.origin = origin;
	}

	public ViewersList(ILogicViewable viewable, List<ViewerType> validTypes) {
		this.viewable = viewable;
		this.validTypes = validTypes;
		this.origin = this;
	}

	public void cloneFrom(ViewersList list) {
		viewers = list.viewers;
		validTypes = list.validTypes;
		connectedDisplays = list.connectedDisplays;
	}

	public boolean hasViewers() {
		return !viewers.isEmpty();
	}

	public HashMap<EntityPlayer, ArrayList<ViewerTally>> getViewers(boolean includeDisplays) {

		if (includeDisplays) {
			ViewersList viewerList = new ViewersList(this, this.viewable, this.validTypes);

			HashMap<EntityPlayer, ArrayList<ViewerTally>> viewerCopy = new HashMap<EntityPlayer, ArrayList<ViewerTally>>();
			for (Map.Entry<EntityPlayer, ArrayList<ViewerTally>> entry : viewers.entrySet()) {
				viewerCopy.put(entry.getKey(), new ArrayList<ViewerTally>(entry.getValue()));
			}
			viewerList.viewers = viewerCopy;			
			connectedDisplays.forEach(viewable -> {
				if (viewable != null) {
					viewable.getViewersList().getViewers(false).entrySet().forEach(viewer -> {
						addTalliesToMap(viewerList, viewer.getKey(), viewer.getValue());
					});
				}
			});
			return viewerList.viewers;
		}

		return viewers;
	}

	public ArrayList<EntityPlayer> getViewers(boolean includeDisplays, ViewerType... types) {
		ArrayList<EntityPlayer> players = new ArrayList();
		viewers: for (Entry<EntityPlayer, ArrayList<ViewerTally>> viewers : getViewers(includeDisplays).entrySet()) {
			for (ViewerType type : types) {
				for (ViewerTally tally : viewers.getValue()) {
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
		ArrayList<ViewerTally> playerTypes = viewerList.viewers.get(player);
		if (playerTypes == null) {
			ArrayList<ViewerTally> newTallies = new ArrayList();
			for (ViewerType type : currentTypes) {
				if (viewerList.validTypes.contains(type)) {
					newTallies.add(new ViewerTally(viewerList.origin, type, 1));
				}
			}
			viewerList.viewers.put(player, newTallies);
		} else {
			ArrayList<ViewerTally> toAdd = new ArrayList();
			for (ViewerType type : (ArrayList<ViewerType>) currentTypes.clone()) {
				if (viewerList.validTypes.contains(type)) {
					boolean added = false;
					for (ViewerTally tally : playerTypes) {
						if (tally.type == type) {
							tally.value += 1;
							currentTypes.remove(type);
							added = true;
							break;
						}
					}
					if (!added) {
						toAdd.add(new ViewerTally(viewerList.origin, type, 1));
					}
				}
			}
			toAdd.forEach(tally -> viewerList.viewers.get(player).add(tally));
		}
		return currentTypes.size() < types.size();
	}

	public static void addTalliesToMap(ViewersList viewerList, EntityPlayer player, ArrayList<ViewerTally> tallies) {
		ArrayList<ViewerTally> playerTypes = viewerList.viewers.get(player);
		if (playerTypes == null) {
			viewerList.viewers.put(player, (ArrayList<ViewerTally>) tallies.clone());
		} else {
			for (ViewerTally tally : tallies) {
				boolean found = false;
				for (ViewerTally currentTally : playerTypes) {
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
		ArrayList<ViewerTally> playerTypes = viewerList.viewers.get(player);
		if (playerTypes != null) {
			types: for (ViewerType type : types) {
				for (ViewerTally tally : playerTypes) {
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
