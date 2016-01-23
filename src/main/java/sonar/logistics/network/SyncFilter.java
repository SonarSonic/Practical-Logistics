package sonar.logistics.network;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.helpers.InfoHelper;

public class SyncFilter implements ISyncPart {
	private ItemFilter c;
	private ItemFilter last;
	private byte id;

	public SyncFilter(int id) {
		this.id = (byte) id;
	}

	public SyncFilter(int id, ItemFilter def) {
		this.id = (byte) id;
		this.c = def;
		this.last = def;
	}

	@Override
	public boolean equal() {
		if (c == null && last != null) {
			return false;
		}
		if (last == null) {
			return false;
		}
		return last.equalFilter(c);
	}

	public void writeToBuf(ByteBuf buf) {
		Logistics.itemFilters.writeToBuf(buf, c);
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.c = Logistics.itemFilters.readFromBuf(buf);
	}

	public void writeToNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (!equal()) {
				NBTTagCompound infoTag = new NBTTagCompound();
				Logistics.itemFilters.writeToNBT(infoTag, c);
				nbt.setTag(String.valueOf(id), infoTag);
				last = c;
			}
		}
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			Logistics.itemFilters.writeToNBT(infoTag, c);
			nbt.setTag(String.valueOf(id), infoTag);

		}
	}

	public void readFromNBT(NBTTagCompound nbt, SyncType type) {
		if (type == SyncType.SYNC) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = Logistics.itemFilters.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
		if (type == SyncType.SAVE) {
			if (nbt.hasKey(String.valueOf(id))) {
				this.c = Logistics.itemFilters.readFromNBT(nbt.getCompoundTag(String.valueOf(id)));
			}
		}
	}

	public void setInfo(ItemFilter value) {
		c = value;
	}

	public ItemFilter getInfo() {
		return c;
	}

}
