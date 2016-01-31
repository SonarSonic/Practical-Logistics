package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketCoordsSelection implements IMessage {

	public int xCoord, yCoord, zCoord;
	public IdentifiedCoords coords;

	public PacketCoordsSelection() {
	}

	public PacketCoordsSelection(int xCoord, int yCoord, int zCoord, IdentifiedCoords info) {
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
		this.coords = IdentifiedCoords.readFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		NBTTagCompound tag = new NBTTagCompound();
		IdentifiedCoords.writeToNBT(tag, coords);
		ByteBufUtils.writeTag(buf, tag);

	}

	public static class Handler implements IMessageHandler<PacketCoordsSelection, IMessage> {

		@Override
		public IMessage onMessage(PacketCoordsSelection message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;
			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (te == null) {
				return null;
			}
			if (te != null && te instanceof TileEntityDataReceiver) {
				((TileEntityDataReceiver) te).emitter.setCoords(message.coords);
			} else {
				TileHandler handler = FMPHelper.getHandler(te);
				if (handler != null && handler instanceof ChannelSelectorHandler) {
					((ChannelSelectorHandler) handler).channel.setCoords(message.coords);
				}
			}

			return null;
		}
	}
}
