package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.SonarCore;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.viewers.ViewerType;
import sonar.logistics.helpers.CableHelper;

public class PacketMonitorType implements IMessage {

	public ILogicMonitor monitor;
	public ViewerType type;

	public PacketMonitorType() {
	}

	public PacketMonitorType(ILogicMonitor monitor, ViewerType type) {
		this.monitor = monitor;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		monitor = CableHelper.getMonitorFromHashCode(buf.readInt(), false);
		type = ViewerType.values()[buf.readInt()];
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
				message.monitor.getViewersList().addViewer(player, message.type);
			}
			return null;
		}
	}

}
