package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.MonitorHelper;

public class PacketMonitoredList implements IMessage {

	public MonitoredList list;
	public ILogicMonitor monitor;

	public NBTTagCompound listTag;
	public SyncType type;

	public PacketMonitoredList() {}
	
	public PacketMonitoredList(ILogicMonitor monitor, NBTTagCompound listTag, SyncType type) {
		this.monitor = monitor;
		this.listTag = listTag;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		Pair<ILogicMonitor, MonitoredList<?>> pair = LogicMonitorCache.getMonitorFromServer(buf.readInt());
		if (pair != null) {
			list = pair.b == null ? MonitoredList.newMonitoredList() : pair.b.copyInfo();
			monitor = pair.a;
			type = SyncType.values()[buf.readInt()];
			list = MonitorHelper.readMonitoredList(ByteBufUtils.readTag(buf), list, type);
		} else {
			Logistics.logger.error("Couldn't get monitor for hashcode");
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(monitor.getMonitorUUID().hashCode());
		buf.writeInt(type.ordinal());
		ByteBufUtils.writeTag(buf, listTag);
	}

	public static class Handler implements IMessageHandler<PacketMonitoredList, IMessage> {

		@Override
		public IMessage onMessage(PacketMonitoredList message, MessageContext ctx) {
			if (message.list != null) {
				LogicMonitorCache.monitoredLists.put(message.monitor, message.monitor.sortMonitoredList(message.list));
			}
			return null;
		}

	}

}
