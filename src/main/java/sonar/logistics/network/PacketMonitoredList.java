package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.IMultipart;
import mcmultipart.multipart.IMultipartContainer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketMultipart;
import sonar.core.network.PacketMultipartHandler;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;

public class PacketMonitoredList extends PacketMultipart {

	public InfoUUID id;
	public int networkID;
	public MonitoredList list;
	public NBTTagCompound listTag;
	public SyncType type;

	public PacketMonitoredList() {}

	public PacketMonitoredList(ILogicMonitor monitor, InfoUUID id, int networkID, NBTTagCompound listTag, SyncType type) {
		super(monitor.getUUID(), monitor.getCoords().getBlockPos());
		this.id = id;
		this.networkID = networkID;
		this.listTag = listTag;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		networkID = buf.readInt();
		id = InfoUUID.getUUID(buf);
		type = SyncType.values()[buf.readInt()];
		list = InfoHelper.readMonitoredList(ByteBufUtils.readTag(buf), Logistics.getClientManager().getMonitoredList(networkID, id).copyInfo(), type);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(networkID);
		id.writeToBuf(buf);
		buf.writeInt(type.ordinal());
		ByteBufUtils.writeTag(buf, listTag);
	}

	public static class Handler extends PacketMultipartHandler<PacketMonitoredList> {

		@Override
		public IMessage processMessage(PacketMonitoredList message, IMultipartContainer target, IMultipart part, MessageContext ctx) {
			if (message.list != null && part instanceof ILogicMonitor) {
				Logistics.getClientManager().monitoredLists.put(message.id, ((ILogicMonitor) part).sortMonitoredList(message.list, message.id.channelID));
			}
			return null;
		}

	}

}
