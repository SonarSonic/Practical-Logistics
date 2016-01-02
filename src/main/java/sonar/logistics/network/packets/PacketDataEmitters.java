package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketDataEmitters implements IMessage {

	public int xCoord, yCoord, zCoord;
	public List<DataEmitter> coords;

	public PacketDataEmitters() {
	}

	public PacketDataEmitters(int xCoord, int yCoord, int zCoord, List<DataEmitter> emitters) {
		this.coords = emitters;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		if (buf.readBoolean()) {
			coords = new ArrayList();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				coords.add(DataEmitter.readFromNBT(ByteBufUtils.readTag(buf)));
			}
		}
		TileEntity tile = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(xCoord, yCoord, zCoord);
		if (tile != null && tile instanceof TileEntityDataReceiver) {
			TileEntityDataReceiver receiver = (TileEntityDataReceiver) tile;
			receiver.emitters = this.coords;
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		if (coords != null) {
			buf.writeBoolean(true);
			buf.writeInt(coords.size());
			for (int i = 0; i < coords.size(); i++) {
				NBTTagCompound tag = new NBTTagCompound();
				DataEmitter.writeToNBT(tag, coords.get(i));
				ByteBufUtils.writeTag(buf, tag);
			}
		} else {
			buf.writeBoolean(false);
		}

	}

	public static class Handler implements IMessageHandler<PacketDataEmitters, IMessage> {

		@Override
		public IMessage onMessage(PacketDataEmitters message, MessageContext ctx) {
			return null;
		}
	}
}
