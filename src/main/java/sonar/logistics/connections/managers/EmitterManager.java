package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.ClientDataEmitter;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.network.PacketClientEmitters;

public class EmitterManager {

	public static ArrayList<IDataEmitter> emitters = new ArrayList<IDataEmitter>();
	public static ArrayList<EntityPlayer> viewers = new ArrayList<EntityPlayer>();
	// client
	public static ArrayList<ClientDataEmitter> clientEmitters = new ArrayList<ClientDataEmitter>();

	private static boolean dirty;

	public static void removeAll() {
		emitters.clear();
	}

	public static void emitterChanged(IDataEmitter emitter) {
		NetworkManager.updateEmitters = true;
		dirty = true;
	}

	public static List<IDataEmitter> getEmitters(UUID uuid) {
		ArrayList<IDataEmitter> emitters = new ArrayList();
		for (IDataEmitter emitter : emitters) {
			if (emitter.canPlayerConnect(uuid)) {
				emitters.add(emitter);
			}
		}
		return emitters;
	}

	public static void addEmitter(IDataEmitter emitter) {
		if (!emitters.contains(emitter)) {
			emitters.add(emitter);
			emitterChanged(emitter);
		}
	}

	public static void removeEmitter(IDataEmitter emitter) {
		emitters.remove(emitter);
		emitterChanged(emitter);
	}

	public static IDataEmitter getEmitter(UUID identity) {
		for (IDataEmitter emitter : emitters) {
			if (emitter.getIdentity().equals(identity)) {
				return emitter;
			}
		}
		return null;
	}

	public static void addViewer(EntityPlayer player) {
		if (!viewers.contains(player)) {
			viewers.add(player);
			getAndSendPacketForViewer(player);
		}
	}

	public static void removeViewer(EntityPlayer player) {
		if (viewers.contains(player)) {
			viewers.remove(player);
		}
	}

	public static void tick() {
		if (dirty) {
			viewers.forEach(player -> getAndSendPacketForViewer(player));
			dirty = false;
		}
	}

	public static void getAndSendPacketForViewer(EntityPlayer player) {
		List<IDataEmitter> emitters = getEmitters(player.getGameProfile().getId());
		ArrayList<ClientDataEmitter> clientEmitters = new ArrayList();
		for (IDataEmitter emitter : EmitterManager.emitters) {
			clientEmitters.add(new ClientDataEmitter(emitter));
		}
		Logistics.network.sendTo(new PacketClientEmitters(clientEmitters), (EntityPlayerMP) player);
	}

}