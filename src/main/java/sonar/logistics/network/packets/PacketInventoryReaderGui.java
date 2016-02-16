package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.logistics.api.Info;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketInventoryReaderGui extends PacketCoords {

	public Info info;
	public boolean state;

	public PacketInventoryReaderGui() {
	}

	public PacketInventoryReaderGui(int x, int y, int z, boolean state) {
		super(x,y,z);
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.state = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
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
