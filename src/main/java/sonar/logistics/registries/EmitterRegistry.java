package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import sonar.core.api.BlockCoords;
import sonar.logistics.api.utils.IdentifiedCoords;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;

public class EmitterRegistry {
	private static Map<String, List<BlockCoords>> privateEmitters = new THashMap<String, List<BlockCoords>>();
	private static ArrayList<BlockCoords> publicEmitters = new ArrayList<BlockCoords>();

	public static void removeAll() {
		privateEmitters.clear();
		publicEmitters.clear();
	}

	public static List<IdentifiedCoords> getEmitters(String playerName, boolean isPrivate) {
		if (playerName == null || playerName.isEmpty()) {
			return null;
		}
		List<IdentifiedCoords> emitterList = new ArrayList();
		ArrayList<BlockCoords> array = (ArrayList<BlockCoords>) publicEmitters.clone();
		if (privateEmitters.get(playerName) != null)
			array.addAll(privateEmitters.get(playerName));

		for (BlockCoords coords : array) {
			if (coords != null) {
				TileEntity tile = coords.getTileEntity();
				if (tile != null && tile instanceof TileEntityDataEmitter) {
					TileEntityDataEmitter dataEmitter = (TileEntityDataEmitter) tile;
					if (tile.getWorldObj() != null && !tile.isInvalid()) {
						EnumChatFormatting format = dataEmitter.isPrivate.getObject() ? EnumChatFormatting.WHITE : EnumChatFormatting.GREEN;
						emitterList.add(new IdentifiedCoords(format + dataEmitter.clientName.getObject(), new ItemStack(BlockRegistry.dataEmitter), coords));
					}
				}
			}
		}

		return emitterList;
	}

	public static void addEmitters(String playerName, BlockCoords emitter, boolean isPrivate) {
		if (emitter != null || playerName != null) {
			if (isPrivate) {
				if (privateEmitters.get(playerName) == null) {
					privateEmitters.put(playerName, new ArrayList());
				}
				if (!privateEmitters.get(playerName).contains(emitter)) {
					privateEmitters.get(playerName).add(emitter);
				} else {
					privateEmitters.get(playerName).remove(emitter);
					privateEmitters.get(playerName).add(emitter);
				}
			} else {
				publicEmitters.add(emitter);
			}
		}
	}

	public static void removeEmitter(String playerName, BlockCoords emitter, boolean isPrivate) {
		if (emitter != null || playerName != null) {
			if (isPrivate) {
				if (privateEmitters.get(playerName) == null) {
					return;
				}
				int i = 0;
				List<BlockCoords> removeList = new ArrayList();
				for (BlockCoords coords : privateEmitters.get(playerName)) {
					if (BlockCoords.equalCoords(coords, emitter)) {
						removeList.add(coords);
					}
					i++;
				}
				for (BlockCoords remove : removeList) {
					privateEmitters.get(playerName).remove(remove);
				}
			} else {
				int i = 0;
				List<BlockCoords> removeList = new ArrayList();
				for (BlockCoords coords : publicEmitters) {
					if (BlockCoords.equalCoords(coords, emitter)) {
						removeList.add(coords);
					}
					i++;
				}
				for (BlockCoords remove : removeList) {
					publicEmitters.remove(remove);
				}
			}
		}
	}

}