package sonar.logistics.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;

public class PacketInfoList implements IMessage {

	public NBTTagCompound tag;
	public SyncType type;

	public PacketInfoList() {}

	public PacketInfoList(NBTTagCompound tag, SyncType type) {
		this.tag = tag;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tag = ByteBufUtils.readTag(buf);
		type = SyncType.values()[buf.readInt()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, tag);
		buf.writeInt(type.ordinal());
	}

	public static class Handler implements IMessageHandler<PacketInfoList, IMessage> {

		@Override
		public IMessage onMessage(PacketInfoList message, MessageContext ctx) {
			Logistics.getClientManager().onInfoPacket(message.tag, message.type);
			return null;
		}

	}

}
