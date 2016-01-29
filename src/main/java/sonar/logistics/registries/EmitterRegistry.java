package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;

public class EmitterRegistry {
	private static Map<String, List<BlockCoords>> emitters = new THashMap<String, List<BlockCoords>>();

	public static void removeAll() {
		emitters.clear();
	}

	public static List<DataEmitter> getEmitters(String playerName) {
		if (playerName == null || playerName.isEmpty()) {
			return null;
		}
		List<DataEmitter> emitterList = new ArrayList();
		if (emitters.get(playerName) == null) {
			return emitterList;
		}
		for (BlockCoords coords : emitters.get(playerName)) {
			if (coords != null) {
				TileEntity tile = coords.getTileEntity();
				if (tile != null && tile instanceof TileEntityDataEmitter) {
					TileEntityDataEmitter dataEmitter = (TileEntityDataEmitter) tile;
					if (tile.getWorldObj() != null && !tile.isInvalid()) {
						emitterList.add(new DataEmitter(dataEmitter.clientName.getString(), coords));
					}
				}
			}
		}

		return emitterList;
	}

	public static void addEmitters(String playerName, BlockCoords emitter) {
		if (emitter != null || playerName != null) {
			if (emitters.get(playerName) == null) {
				emitters.put(playerName, new ArrayList());
			}
			if (!emitters.get(playerName).contains(emitter)) {
				emitters.get(playerName).add(emitter);
			} else {
				emitters.get(playerName).remove(emitter);
				emitters.get(playerName).add(emitter);
			}
		}
	}

	public static void removeEmitter(String playerName, BlockCoords emitter) {
		if (emitter != null || playerName != null) {
			if (emitters.get(playerName) == null) {
				return;
			}
			int i = 0;
			List<BlockCoords> removeList = new ArrayList();
			for (BlockCoords coords : emitters.get(playerName)) {
				if (BlockCoords.equalCoords(coords, emitter)) {
					removeList.add(coords);
				}
				i++;
			}
			for (BlockCoords remove : removeList) {
				emitters.get(playerName).remove(remove);
			}
		}
	}

}