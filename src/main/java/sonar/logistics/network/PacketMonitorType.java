package sonar.logistics.network;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.connections.LogicMonitorCache;

public class PacketMonitorType implements IMessage {

	public ILogicMonitor monitor;
	public MonitorType type;

	public PacketMonitorType() {
	}

	public PacketMonitorType(ILogicMonitor monitor, MonitorType type) {
		this.monitor = monitor;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		monitor = LogicMonitorCache.getMonitorFromClient(buf.readInt());
		type = MonitorType.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(monitor.getIdentity().hashCode());
		buf.writeInt(type.ordinal());
	}

	public static class Handler implements IMessageHandler<PacketMonitorType, IMessage> {

		@Override
		public IMessage onMessage(PacketMonitorType message, MessageContext ctx) {
			EntityPlayer player = SonarCore.proxy.getPlayerEntity(ctx);
			if (message.monitor != null && player != null) {
				for (MonitorViewer viewer : (ArrayList<MonitorViewer>) message.monitor.getViewers(true)) {
					if (viewer.player.getGameProfile().getId().equals(player.getGameProfile().getId())) {
						viewer.setMonitorType(message.type);
						break;
					}
				}
			}
			return null;
		}
	}

}
