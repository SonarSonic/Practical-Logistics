package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.IdentifiedCoords;

public class SyncIdentifiedCoords implements ISyncPart {
	private IdentifiedCoords c;
	private IdentifiedCoords last;
	private byte id;

	public SyncIdentifiedCoords(int id) {
		this.id = (byte) id;
	}

	public SyncIdentifiedCoords(int id, IdentifiedCoords def) {
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
		return BlockCoords.equalCoords(c.blockCoords, last.blockCoords) && c.suffix.equals(last.suffix);
	}

	public void writeToBuf(ByteBuf buf) {
		if (!equal()) {
			buf.writeBoolean(true);
			IdentifiedCoords.writeCoords(buf, c);
			last = c;
		} else
			buf.writeBoolean(false);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			this.c = IdentifiedCoords.readCoords(buf);
		}
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				NBTTagCompound infoTag = new NBTTagCompound();
				IdentifiedCoords.writeToNBT(infoTag, c);
				nbt.setTag(String.valueOf(id), infoTag);
				last = c;
			}
		}
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			IdentifiedCoords.writeToNBT(infoTag, c);
			nbt.setTag(String.valueOf(id), infoTag);

		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = IdentifiedCoords.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
		if (type == SyncType.SAVE) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = IdentifiedCoords.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
	}

	public void setCoords(IdentifiedCoords value) {
		c = value;
	}

	public IdentifiedCoords getCoords() {
		return c;
	}

}
