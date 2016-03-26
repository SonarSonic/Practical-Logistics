package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncPart;
import sonar.logistics.api.utils.IdentifiedCoords;

public class SyncIdentifiedCoords extends SyncPart {
	private IdentifiedCoords c;
	private IdentifiedCoords last;

	public SyncIdentifiedCoords(int id) {
		super(id);
	}
	public SyncIdentifiedCoords(String name) {
		super(name);
	}
	@Override
	public boolean equal() {
		if (c == null && last!=null) {
			return false;
		}
		if (last == null) {
			return false;
		}
		return BlockCoords.equalCoords(c.blockCoords, last.blockCoords) && c.coordString.equals(last.coordString);
	}

	public void setCoords(IdentifiedCoords value) {
		c = value;
	}

	public IdentifiedCoords getCoords() {
		return c;
	}

	@Override
	public boolean canSync(SyncType sync) {
		return false;
	}

	@Override
	public void updateSync() {
		last = c;		
	}

	@Override
	public void writeObject(ByteBuf buf) {
		IdentifiedCoords.writeCoords(buf, c);		
	}

	@Override
	public void readObject(ByteBuf buf) {
		this.c = IdentifiedCoords.readCoords(buf);		
	}

	@Override
	public void writeObject(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound infoTag = new NBTTagCompound();
		IdentifiedCoords.writeToNBT(infoTag, c);
		nbt.setTag(this.getTagName(), infoTag);		
	}

	@Override
	public void readObject(NBTTagCompound nbt, SyncType type) {
		this.c = IdentifiedCoords.readFromNBT(nbt.getCompoundTag(this.getTagName()));		
	}

}
