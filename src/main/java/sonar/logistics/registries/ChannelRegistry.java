package sonar.logistics.registries;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sonar.core.utils.BlockCoords;
import sonar.logistics.api.DataChannel;
import sonar.logistics.api.DataEmitter;

public class ChannelRegistry {
	private static Map<String, List<DataChannel>> channels = new THashMap<String, List<DataChannel>>();
	private static int currentID = 0;

	public static void removeAll() {
		channels.clear();
	}

	public static List<DataEmitter> getEmitters(String playerName, int channelID) {
		if (playerName == null || playerName.isEmpty()) {
			return null;
		}
		List<DataEmitter> emitterList = new ArrayList();
		if (channels.get(playerName) == null) {
			return emitterList;
		}
		for (DataChannel channel : channels.get(playerName)) {
			if (channel.channelID == channelID) {
				return channel.getEmitters();
			}
		}

		return emitterList;
	}

	public static int createChannel(String channelName, String playerName) {
		if (channelName != null || playerName != null) {
			if (channels.get(playerName) == null) {
				channels.put(playerName, new ArrayList());
			}
			for (DataChannel channel : channels.get(playerName)) {
				if (channel.channelName.equals(channelName)) {
					return channel.channelID;
				}
			}
			currentID++;
			DataChannel newChannel = new DataChannel(channelName, currentID);
			if (!channels.get(playerName).contains(newChannel)) {
				channels.get(playerName).add(newChannel);
				return currentID;
			} else {
				channels.get(playerName).remove(newChannel);
				channels.get(playerName).add(newChannel);
				return currentID;
			}
		}
		return 0;
	}

	public static void removeChannel(String playerName, int channelID) {
		if (channelID != 0 || playerName != null) {
			if (channels.get(playerName) == null) {
				return;
			}
			for (DataChannel channel : channels.get(playerName)) {
				if (channel.channelID == channelID) {
					channels.get(playerName).remove(channel);
					return;
				}
			}
		}
	}

	public static void addEmitter(String playerName, int channelID, BlockCoords emitter) {
		if (channelID != 0 || playerName != null) {
			if (channels.get(playerName) == null) {
				return;
			}
			for (DataChannel channel : channels.get(playerName)) {
				if (channel.channelID == channelID) {
					channel.addEmitter(emitter);
					return;
				}
			}
		}
	}

	public static void removeEmitter(String playerName, int channelID, BlockCoords emitter) {
		if (channelID != 0 || playerName != null) {
			if (channels.get(playerName) == null) {
				return;
			}
			for (DataChannel channel : channels.get(playerName)) {
				if (channel.channelID == channelID) {
					channel.removeEmitter(emitter);
					return;
				}
			}
		}
	}
}