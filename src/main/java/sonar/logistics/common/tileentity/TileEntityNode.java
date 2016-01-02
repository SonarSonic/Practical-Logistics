package sonar.logistics.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.helpers.NBTHelper.SyncType;

public class TileEntityNode extends TileEntitySonar {

	public String playerName = "NO NAME";	

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			this.playerName = nbt.getString("playerName");
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			if(playerName==null || playerName.isEmpty()){
				playerName=" ";
			}
			nbt.setString("playerName", playerName);
		}
	}
	
}
