package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.Info;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketInventoryReaderGui implements IMessage {

	public int xCoord, yCoord, zCoord;
	public Info info;
	public boolean state;

	public PacketInventoryReaderGui() {
	}

	public PacketInventoryReaderGui(int xCoord, int yCoord, int zCoord, boolean state) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.state = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		buf.writeBoolean(state);

	}

	public static class Handler implements IMessageHandler<PacketInventoryReaderGui, IMessage> {

		@Override
		public IMessage onMessage(PacketInventoryReaderGui message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			if (player != null) {
				World world = ctx.getServerHandler().playerEntity.worldObj;
				if (player.openContainer != null && player.openContainer instanceof ContainerInventoryReader) {
					TileEntity target = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
					TileHandler handler = FMPHelper.getHandler(target);
					if (handler != null && handler instanceof InventoryReaderHandler) {
						((ContainerInventoryReader) player.openContainer).addSlots((InventoryReaderHandler) handler, player.inventory, message.state);
					}
				}
			}

			return null;
		}
	}
}
