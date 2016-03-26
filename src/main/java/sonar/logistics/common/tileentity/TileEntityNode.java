package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;

public class TileEntityNode extends TileEntitySonar {

	public String playerName = "NO NAME";
	public SyncTagType.BOOLEAN isPrivate = new SyncTagType.BOOLEAN(1);

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			this.playerName = nbt.getString("playerName");
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			if (playerName == null || playerName.isEmpty()) {
				playerName = " ";
			}
			nbt.setString("playerName", playerName);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.add(isPrivate);
	}
}
