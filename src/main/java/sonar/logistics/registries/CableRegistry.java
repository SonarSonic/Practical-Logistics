package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;
import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.IBufManager;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;

public class CableRegistry {
	private static Map<Integer, List<BlockCoords>> connections = new THashMap<Integer, List<BlockCoords>>();

	public static void removeAll() {
		connections.clear();
	}

	public int getAvailableID() {
		return 0;
	}

	public static List<BlockCoords> getEmitters(int networkID) {
		if (networkID == -1) {
			return null;
		}
		List<BlockCoords> coords = connections.get(networkID);
		if (coords == null) {
			return Collections.EMPTY_LIST;
		}
		return coords;
	}

	public static void addConnections(int networkID, BlockCoords emitter) {
		if (networkID != -1 && emitter != null) {
			if (connections.get(networkID) == null) {
				connections.put(networkID, new ArrayList());
			}
			if (!connections.get(networkID).contains(emitter)) {
				connections.get(networkID).add(emitter);
			} else {
				connections.get(networkID).remove(emitter);
				connections.get(networkID).add(emitter);
			}
		}
	}

	public static void removeConnections(int networkID, BlockCoords emitter) {
		if (networkID != -1 && emitter != null) {
			if (connections.get(networkID) == null) {
				return;
			}
			int i = 0;
			List<BlockCoords> removeList = new ArrayList();
			for (BlockCoords coords : connections.get(networkID)) {
				if (BlockCoords.equalCoords(coords, emitter)) {
					removeList.add(coords);
				}
				i++;
			}
			for (BlockCoords remove : removeList) {
				connections.get(networkID).remove(remove);
			}
		}
	}

}