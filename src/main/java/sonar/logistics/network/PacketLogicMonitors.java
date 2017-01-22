package sonar.logistics.network;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.connecting.ClientLogicMonitor;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.helpers.CableHelper;

public class PacketLogicMonitors implements IMessage {

	public ArrayList<ClientLogicMonitor> monitors;
	public UUID screenID;

	public PacketLogicMonitors() {
	}

	public PacketLogicMonitors(ArrayList<ClientLogicMonitor> monitors, UUID screenID) {
		this.monitors = monitors;
		this.screenID = screenID;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		long msb = buf.readLong();
		long lsb = buf.readLong();
		screenID = new UUID(msb, lsb);
		NBTTagCompound tag = ByteBufUtils.readTag(buf);
		monitors = new ArrayList();
		if (tag.hasKey("monitors")) {
			NBTTagList tagList = tag.getTagList("monitors", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < tagList.tagCount(); i++) {
				monitors.add(NBTHelper.instanceNBTSyncable(ClientLogicMonitor.class, tagList.getCompoundTagAt(i)));
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(screenID.getMostSignificantBits());
		buf.writeLong(screenID.getLeastSignificantBits());
		NBTTagCompound tag = new NBTTagCompound();
		NBTTagList tagList = new NBTTagList();
		monitors.forEach(emitter -> tagList.appendTag(emitter.writeData(new NBTTagCompound(), SyncType.SAVE)));
		if (!tagList.hasNoTags()) {
			tag.setTag("monitors", tagList);
		}
		ByteBufUtils.writeTag(buf, tag);

	}

	public static class Handler implements IMessageHandler<PacketLogicMonitors, IMessage> {
		@Override
		public IMessage onMessage(PacketLogicMonitors message, MessageContext ctx) {
			if (ctx.side == Side.CLIENT) {

				Map<UUID, ArrayList<ClientLogicMonitor>> monitors = Logistics.getClientManager().clientLogicMonitors;
				if (monitors.get(message.screenID) == null) {
					monitors.put(message.screenID, message.monitors);
				} else {
					monitors.get(message.screenID).clear();
					monitors.get(message.screenID).addAll(message.monitors);
				}
				ArrayList<Object> cache = new ArrayList();
				for (ClientLogicMonitor clientMonitor : message.monitors) {
					ILogicMonitor monitor = CableHelper.getMonitorFromHashCode(clientMonitor.uuid.getUUID().hashCode(), true);
					if (monitor != null) {
						int hashCode = monitor.getIdentity().hashCode();
						cache.add(monitor);
						for (int i = 0; i < monitor.getMaxInfo(); i++) {
							cache.add(new InfoUUID(hashCode, i));
						}
					}
				}

				Map<UUID, ArrayList<Object>> sortedMonitors = Logistics.getClientManager().sortedLogicMonitors;
				if (sortedMonitors.get(message.screenID) == null) {
					sortedMonitors.put(message.screenID, cache);
				} else {
					sortedMonitors.get(message.screenID).clear();
					sortedMonitors.get(message.screenID).addAll(cache);
				}
			}
			return null;
		}
	}

}
