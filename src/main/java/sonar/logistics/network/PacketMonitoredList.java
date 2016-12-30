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
import sonar.logistics.connections.managers.LogicMonitorManager;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.helpers.LogisticsHelper;

public class PacketMonitoredList implements IMessage {

	public int networkID;

	public MonitoredList list;
	public ILogicMonitor monitor;

	public NBTTagCompound listTag;
	public SyncType type;

	public PacketMonitoredList() {
	}

	public PacketMonitoredList(ILogicMonitor monitor, int networkID, NBTTagCompound listTag, SyncType type) {
		this.monitor = monitor;
		this.networkID = networkID;
		this.listTag = listTag;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int hashCode = buf.readInt();
		networkID = buf.readInt();
		Pair<ILogicMonitor, MonitoredList<?>> pair = LogicMonitorManager.getMonitorFromServer(hashCode);
		if (pair != null) {
			monitor = pair.a;
			if (monitor.getNetworkID() == networkID) {

				list = pair.b == null ? MonitoredList.newMonitoredList(networkID) : pair.b.copyInfo();
				list.networkID = networkID;
				type = SyncType.values()[buf.readInt()];
				list = InfoHelper.readMonitoredList(ByteBufUtils.readTag(buf), list, type);
			}else{
				list = MonitoredList.newMonitoredList(monitor.getNetworkID());
			}
		} else {
			Logistics.logger.error("Couldn't get monitor for hashcode");
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(monitor.getIdentity().hashCode());
		buf.writeInt(networkID);
		buf.writeInt(type.ordinal());
		ByteBufUtils.writeTag(buf, listTag);
	}

	public static class Handler implements IMessageHandler<PacketMonitoredList, IMessage> {

		@Override
		public IMessage onMessage(PacketMonitoredList message, MessageContext ctx) {
			if (message.list != null) {
				LogicMonitorManager.monitoredLists.put(message.monitor, message.monitor.sortMonitoredList(message.list));
			}
			return null;
		}

	}

}
