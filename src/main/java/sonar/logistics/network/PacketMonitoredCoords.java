package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.connections.CacheRegistry;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.MonitorHelper;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class PacketMonitoredCoords implements IMessage {

	public MonitoredList<MonitoredBlockCoords> list;
	public NBTTagCompound listTag;
	public int registryID;

	public PacketMonitoredCoords() {
	}

	public PacketMonitoredCoords(int registryID, NBTTagCompound listTag) {
		this.listTag = listTag;
		this.registryID = registryID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		registryID = buf.readInt();
		listTag = ByteBufUtils.readTag(buf);
		if (listTag != null)
			list = MonitorHelper.readMonitoredList(listTag, CacheRegistry.coordMap.getOrDefault(registryID, MonitoredList.newMonitoredList()).copyInfo(), SyncType.DEFAULT_SYNC);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(registryID);
		ByteBufUtils.writeTag(buf, listTag);
	}

	public static class Handler implements IMessageHandler<PacketMonitoredCoords, IMessage> {
		@Override
		public IMessage onMessage(PacketMonitoredCoords message, MessageContext ctx) {
			//if (message.list != null)
				//CacheRegistry.coordMap.put(message.registryID, message.list);
			return null;
		}
	}

}
