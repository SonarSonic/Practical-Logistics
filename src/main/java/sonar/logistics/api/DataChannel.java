package sonar.logistics.api;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.BlockCoords;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;

public class DataChannel {
	public String channelName;
	public int channelID;
	public List<BlockCoords> emitters = new ArrayList();

	public DataChannel(String channelName, int channelID) {
		this.channelName = channelName;
		this.channelID = channelID;
	}

	public void addEmitter(BlockCoords emitter) {
		if (!emitters.contains(emitter)) {
			emitters.add(emitter);
		} else {
			emitters.remove(emitter);
			emitters.add(emitter);
		}
	}

	public void removeEmitter(BlockCoords emitter) {
		int i = 0;
		List<BlockCoords> removeList = new ArrayList();
		for (BlockCoords coords : emitters) {
			if (BlockCoords.equalCoords(coords, emitter)) {
				removeList.add(coords);
			}
			i++;
		}
		for (BlockCoords remove : removeList) {
			emitters.remove(remove);
		}
	}

	public List<DataEmitter> getEmitters() {
		List<DataEmitter> emitterList = new ArrayList();
		for (BlockCoords coords : emitters) {
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

}
