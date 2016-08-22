package sonar.logistics.api.info;

import com.google.common.base.Objects;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper.SyncType;

/**used to identify info and find the monitor which created it*/
public class InfoUUID implements INBTSyncable {

	public int hashCode;
	public int pos;

	public InfoUUID() {
	}

	public InfoUUID(int hashCode, int pos) {
		this.hashCode = hashCode;
		this.pos = pos;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof InfoUUID) {
			return this.hashCode == ((InfoUUID) obj).hashCode && this.pos == ((InfoUUID) obj).pos;
		}
		return false;
	}

	public int hashCode() {
		return Objects.hashCode(hashCode, pos);
	}

	public boolean valid() {
		if (hashCode == -1 && pos == -1) {
			return false;
		}
		return true;
	}

	public static InfoUUID getUUID(ByteBuf buf) {
		return new InfoUUID(buf.readInt(), buf.readInt());
	}

	public void writeToBuf(ByteBuf buf) {
		buf.writeInt(hashCode);
		buf.writeInt(pos);
	}

	public String toString() {
		return hashCode + ":" + pos;
	}

	public static InfoUUID fromString(String string) {
		String[] ids = string.split(":");
		return new InfoUUID(Integer.valueOf(ids[0]), Integer.valueOf(ids[1]));
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		String[] ids = nbt.getString("uuid").split(":");
		hashCode = Integer.valueOf(ids[0]);
		pos = Integer.valueOf(ids[1]);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setString("uuid", toString());
		return nbt;
	}
}
