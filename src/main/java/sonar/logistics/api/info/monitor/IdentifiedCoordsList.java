package sonar.logistics.api.info.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;

public class IdentifiedCoordsList extends ArrayList<BlockCoords> implements ISyncPart {

	public UUID identity;
	public boolean hasChanged = true;
	public final int tagID;

	public IdentifiedCoordsList(int tagID) {
		super();
		this.tagID = tagID;
		this.identity = UUID.randomUUID();
	}

	public IdentifiedCoordsList(int tagID, UUID identity) {
		super();
		this.tagID = tagID;
		this.identity = identity;
	}

	public IdentifiedCoordsList setIdentity(UUID uuid) {
		identity = uuid;
		return this;
	}

	public UUID getIdentity() {
		return identity;
	}

	public boolean equals(Object object) {
		return object != null && (object instanceof IdentifiedCoordsList) ? ((IdentifiedCoordsList) object).identity.equals(identity) : super.equals(object);
	}

	public void modifyCoords(ChannelType type, BlockCoords coords) {
		if (coords != null) {
			if (!coords.contains(this)) {
				if (type == ChannelType.SINGLE) {
					clear();
				}
				add(coords);
			} else {
				Iterator<BlockCoords> iterator = iterator();
				this.removeIf(coord -> coord.equals(coords));
			}
		}
	}

	public boolean add(BlockCoords coords) {
		boolean add = super.add(coords);
		if (add) {
			setChanged(true);
		}
		return add;
	}

	public boolean addAll(Collection<? extends BlockCoords> coords) {
		boolean addAll = super.addAll(coords);
		if (addAll) {
			setChanged(true);
		}
		return addAll;
	}

	public int hashCode() {
		return identity.hashCode();
	}

	@Override
	public boolean hasChanged() {
		return hasChanged;
	}

	@Override
	public void setChanged(boolean set) {
		hasChanged = set;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, BlockCoords.writeBlockCoords(new NBTTagCompound(), this, getTagName()));
		buf.writeLong(identity.getMostSignificantBits());
		buf.writeLong(identity.getLeastSignificantBits());
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		clear();
		addAll(BlockCoords.readBlockCoords(ByteBufUtils.readTag(buf), getTagName()));
		long msb = buf.readLong();
		long lsb = buf.readLong();
		identity = new UUID(msb, lsb);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setUniqueId("iden", identity);
		return BlockCoords.writeBlockCoords(nbt, this, getTagName());
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		// clear();
		identity = nbt.getUniqueId("iden");
		addAll(BlockCoords.readBlockCoords(nbt, getTagName()));
	}

	@Override
	public boolean canSync(SyncType sync) {
		return sync.isType(SyncType.SAVE, SyncType.DEFAULT_SYNC);
	}

	@Override
	public String getTagName() {
		return String.valueOf(tagID);
	}
}
