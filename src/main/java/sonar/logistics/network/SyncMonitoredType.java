package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncPart;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.helpers.InfoHelper;

public class SyncMonitoredType<T extends IMonitorInfo> extends SyncPart {

	public IMonitorInfo<T> info;

	public SyncMonitoredType(int id) {
		super(id);
	}

	public void setInfo(IMonitorInfo<T> info) {
		this.info = info;
		markDirty();
	}

	public T getMonitoredInfo() {
		return (T) info;
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		if (info != null && info.isValid()) {
			buf.writeBoolean(true);
			ByteBufUtils.writeTag(buf, this.writeData(new NBTTagCompound(), SyncType.SAVE));
		} else {
			buf.writeBoolean(false);
		}
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		if (buf.readBoolean()) {
			readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
		} else {
			info = null;
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		if (info != null && info.isValid()) {
			nbt.setTag(getTagName(), InfoHelper.writeInfoToNBT(new NBTTagCompound(), info, type));
		}
		return nbt;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(getTagName())) {
			info = InfoHelper.readInfoFromNBT(nbt.getCompoundTag(getTagName()));
		}
	}
	/*
	public MonitorHandler handler() {
		return handler == null ? handler = Logistics.monitorHandlers.getRegisteredObject(handlerID) : handler;
	}
	*/
}
