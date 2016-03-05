package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.SyncPart;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.ExternalCoords;
import sonar.logistics.api.IdentifiedCoords;

public class SyncExternalCoords extends SyncPart {
	private ExternalCoords c;
	private ExternalCoords last;

	public SyncExternalCoords(int id) {
		super(id);
	}
	public SyncExternalCoords(String name) {
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

	public void setCoords(ExternalCoords value) {
		c = value;
	}

	public ExternalCoords getCoords() {
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
		ExternalCoords.writeCoords(buf, c);		
	}

	@Override
	public void readObject(ByteBuf buf) {
		this.c = ExternalCoords.readCoords(buf);		
	}

	@Override
	public void writeObject(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound infoTag = new NBTTagCompound();
		ExternalCoords.writeToNBT(infoTag, c);
		nbt.setTag(this.getTagName(), infoTag);		
	}

	@Override
	public void readObject(NBTTagCompound nbt, SyncType type) {
		this.c = ExternalCoords.readFromNBT(nbt.getCompoundTag(this.getTagName()));		
	}

}
