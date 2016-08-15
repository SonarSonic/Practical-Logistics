package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gnu.trove.map.hash.THashMap;
import sonar.core.api.utils.BlockCoords;
import sonar.core.integration.fmp.OLDMultipartHelper;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.helpers.DisplayHelper;

public class DisplayRegistry {

	private static Map<Integer, List<BlockCoords>> screens = new THashMap<Integer, List<BlockCoords>>();

	private static ArrayList<Integer> needUpdate = new ArrayList();

	public static void removeAll() {
		screens.clear();
	}

	public static boolean hasChanged(int registryID) {
		Integer inte = Integer.valueOf(registryID);
		return needUpdate.contains(inte);
	}

	public static void setChanged(int registryID) {
		Integer inte = Integer.valueOf(registryID);
		if (!needUpdate.contains(inte)) {
			needUpdate.add(inte);
		}
	}

	public static void onUpdate(int registryID) {
		Integer inte = Integer.valueOf(registryID);
		if (needUpdate.contains(inte)) {
			needUpdate.remove(inte);
		}
	}

	public static int getNextAvailableID() {
		for (int i = 0; i < screens.size(); i++) {
			if (screens.get(i) == null || screens.get(i).isEmpty() || screens.get(i).size() == 0) {
				return i;
			}
		}
		return screens.size();
	}

	public static List<BlockCoords> getScreens(int registryID) {
		if (registryID == -1) {
			return new ArrayList();
		}
		List<BlockCoords> coords = screens.get(registryID);
		if (coords == null) {
			return new ArrayList();
		}
		return coords;
	}

	public static void addScreens(int registryID, List<BlockCoords> cables) {
		for (BlockCoords coords : cables) {
			addScreen(registryID, coords);
		}
	}

	public static void addScreen(int registryID, BlockCoords cable) {
		if (registryID != -1 && cable != null) {
			Object target = OLDMultipartHelper.checkObject(cable.getTileEntity());
			if (target != null && target instanceof ILargeDisplay) {
				if (screens.get(registryID) == null) {
					screens.put(registryID, new ArrayList());
					screens.get(registryID).add(cable);
					((ILargeDisplay) target).setRegistryID(registryID);
					setHandler(registryID);
					return;

				}
				List<BlockCoords> removeList = new ArrayList();
				for (BlockCoords coords : screens.get(registryID)) {
					if (BlockCoords.equalCoords(coords, cable)) {
						setHandler(registryID);
						return;
					}
				}
				screens.get(registryID).add(cable);
				((ILargeDisplay) target).setRegistryID(registryID);
				setHandler(registryID);
			}
		}
	}

	public static void removeScreen(int registryID, ILargeDisplay cable) {
		if (registryID != -1 && cable.getCoords() != null) {
			if (screens.get(registryID) == null) {
				return;
			}
			List<BlockCoords> removeList = new ArrayList();
			for (BlockCoords coords : screens.get(registryID)) {
				if (BlockCoords.equalCoords(coords, cable.getCoords())) {
					removeList.add(coords);
				}
			}
			for (BlockCoords remove : removeList) {
				screens.get(registryID).remove(remove);
			}

			List<BlockCoords> oldCables = new ArrayList();
			if (screens.get(registryID) != null) {
				oldCables.addAll(screens.get(registryID));
				screens.get(registryID).clear();
			}

			int newID = getNextAvailableID();
			for (BlockCoords oldScreens : oldCables) {
				Object target = OLDMultipartHelper.checkObject(oldScreens.getTileEntity());
				if (target != null && target instanceof ILargeDisplay) {
					ILargeDisplay tile = (ILargeDisplay) target;
					tile.setRegistryID(-1);
				}
			}
			for (BlockCoords oldCable : oldCables) {
				Object target = OLDMultipartHelper.checkObject(oldCable.getTileEntity());
				if (target != null && target instanceof ILargeDisplay) {
					ILargeDisplay tile = (ILargeDisplay) target;
					DisplayHelper.addScreen(tile);
				}
			}
			setHandler(newID);
		}
	}

	public static void connectScreens(int newID, int secondaryID) {
		List<BlockCoords> oldCables = new ArrayList();
		if (screens.get(secondaryID) != null) {
			oldCables.addAll(screens.get(secondaryID));
			screens.get(secondaryID).clear();
		}
		addScreens(newID, oldCables);
		setHandler(newID);
	}

	public static void setHandler(int id) {
		List<BlockCoords> coords = getScreens(id);
		int y = 1000;
		BlockCoords handler = null;
		for (BlockCoords coord : coords) {
			if (coord.getY() < y) {
				handler = coord;
				y = coord.getY();
			}
		}
		if (handler != null && y != 1000) {
			for (BlockCoords coord : coords) {
				Object object = OLDMultipartHelper.getHandler(coord.getTileEntity());
				if (object != null && object instanceof LargeDisplayScreenHandler) {
					LargeDisplayScreenHandler screen = (LargeDisplayScreenHandler) object;
					if (BlockCoords.equalCoords(handler, coord)) {
						screen.isHandler.setObject(true);
						screen.resetHandler = true;
					} else {
						if (screen.isHandler.getObject()) {
							screen.isHandler.setObject(false);
							screen.resetHandler = true;
						}
					}
				}
			}
		}
		setChanged(id);
	}
}