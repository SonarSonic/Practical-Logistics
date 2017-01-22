package sonar.logistics.api.info;

import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

/**used to identify info and find the monitor which created it*/
public class InfoUUID implements INBTSyncable {

	public int hashCode;
	public int channelID;

	public InfoUUID() {}

	public InfoUUID(int hashCode, int pos) {
		this.hashCode = hashCode;
		this.channelID = pos;
	}
	
	public int getChannelID(){
		return channelID;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof InfoUUID) {
			return this.hashCode == ((InfoUUID) obj).hashCode && this.channelID == ((InfoUUID) obj).channelID;
		}
		return false;
	}

	public int hashCode() {
		return Objects.hashCode(hashCode, channelID);
	}

	public boolean valid() {
		if (hashCode == -1 && channelID == -1) {
			return false;
		}
		return true;
	}

	public static InfoUUID getUUID(ByteBuf buf) {
		return new InfoUUID(buf.readInt(), buf.readInt());
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(hashCode);
		buf.writeInt(channelID);
	}

	public String toString() {
		return hashCode + ":" + channelID;
	}

	public static InfoUUID fromString(String string) {
		String[] ids = string.split(":");
		return new InfoUUID(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]));
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {		
		hashCode = nbt.getInteger("hash");
		channelID = nbt.getInteger("pos");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setInteger("hash", hashCode);
		nbt.setInteger("pos", channelID);		
		return nbt;
	}
}
