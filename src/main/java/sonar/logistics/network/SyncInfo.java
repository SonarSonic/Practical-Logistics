package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.helpers.InfoHelper;

public class SyncInfo implements ISyncPart {
	private Info c;
	private Info last;
	private byte id;

	public SyncInfo(int id) {
		this.id = (byte) id;
	}

	public SyncInfo(int id, Info def) {
		this.id = (byte) id;
		this.c = def;
		this.last = def;
	}

	@Override
	public boolean equal() {
		if (c == null && last!=null) {
			return false;
		}
		if (last == null) {
			return false;
		}
		return c.isEqualType(last) && c.isDataEqualType(last);
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			Logistics.infoTypes.writeToBuf(buf, c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = Logistics.infoTypes.readFromBuf(buf);
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				NBTTagCompound infoTag = new NBTTagCompound();
				Logistics.infoTypes.writeToNBT(infoTag, c);
				nbt.setTag(String.valueOf(id), infoTag);
				last = c;
			}
		}
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			Logistics.infoTypes.writeToNBT(infoTag, c);
			nbt.setTag(String.valueOf(id), infoTag);

		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
		if (type == SyncType.SAVE) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
	}

	public void setInfo(Info value) {
		c = value;
	}

	public Info getInfo() {
		return c;
	}

}
