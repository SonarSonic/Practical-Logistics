package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.api.Info;
import sonar.logistics.helpers.InfoHelper;

public class SyncEmitter implements ISyncPart {
	private DataEmitter c;
	private DataEmitter last;
	private byte id;

	public SyncEmitter(int id) {
		this.id = (byte) id;
	}

	public SyncEmitter(int id, DataEmitter def) {
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
		return BlockCoords.equalCoords(c.coords, last.coords) && c.name.equals(last.name);
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			DataEmitter.writeInfo(buf, c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = DataEmitter.readInfo(buf);
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				NBTTagCompound infoTag = new NBTTagCompound();
				DataEmitter.writeToNBT(infoTag, c);
				nbt.setTag(String.valueOf(id), infoTag);
				last = c;
			}
		}
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			DataEmitter.writeToNBT(infoTag, c);
			nbt.setTag(String.valueOf(id), infoTag);

		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = DataEmitter.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
		if (type == SyncType.SAVE) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = DataEmitter.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
	}

	public void setEmitter(DataEmitter value) {
		c = value;
	}

	public DataEmitter getEmitter() {
		return c;
	}

}
