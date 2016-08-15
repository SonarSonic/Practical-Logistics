package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import gnu.trove.map.hash.THashMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.LogisticsBlocks;
import sonar.logistics.api.connecting.IDataEmitter;

public class EmitterRegistry {

	private static ArrayList<IDataEmitter> publicEmitters = new ArrayList<IDataEmitter>();

	public static void removeAll() {
		publicEmitters.clear();
	}

	public static List<IDataEmitter> getEmitters(UUID uuid) {
		ArrayList<IDataEmitter> emitters = new ArrayList();
		for (IDataEmitter emitter : publicEmitters) {
			if (emitter.canPlayerConnect(uuid)) {
				emitters.add(emitter);
			}
		}
		return emitters;
	}

	public static void addEmitter(IDataEmitter emitter) {
		if (!publicEmitters.contains(emitter)) {
			publicEmitters.add(emitter);
		}
	}

	public static void removeEmitter(IDataEmitter emitter) {
		publicEmitters.remove(emitter);
	}

}