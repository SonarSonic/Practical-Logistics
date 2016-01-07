package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketDataReceiver implements IMessage {

	public int xCoord, yCoord, zCoord;
	public DataEmitter coords;

	public PacketDataReceiver() {
	}

	public PacketDataReceiver(int xCoord, int yCoord, int zCoord, DataEmitter info) {
		this.coords = info;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.coords = DataEmitter.readFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		NBTTagCompound tag = new NBTTagCompound();
		DataEmitter.writeToNBT(tag, coords);
		ByteBufUtils.writeTag(buf, tag);
		
	}

	public static class Handler implements IMessageHandler<PacketDataReceiver, IMessage> {

		@Override
		public IMessage onMessage(PacketDataReceiver message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (te == null) {
				return null;
			}
			if (te != null && te instanceof TileEntityDataReceiver) {
				TileEntityDataReceiver receiver = (TileEntityDataReceiver) te;
				receiver.emitter.setEmitter(message.coords);
			}

			return null;
		}
	}
}
