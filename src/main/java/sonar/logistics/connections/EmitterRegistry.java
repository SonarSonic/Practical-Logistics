package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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