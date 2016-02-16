package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.network.PacketCoords;
import sonar.logistics.api.Info;
import sonar.logistics.common.containers.ContainerItemRouter;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketRouterGui extends PacketCoords<PacketRouterGui> {

	public Info info;
	public int state;

	public PacketRouterGui() {
	}

	public PacketRouterGui(int x, int y, int z, int state) {
		super(x, y, z);
		this.state = state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.state = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(state);

	}

	public static class Handler implements IMessageHandler<PacketRouterGui, IMessage> {

		@Override
		public IMessage onMessage(PacketRouterGui message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			if (player != null) {
				World world = ctx.getServerHandler().playerEntity.worldObj;
				if (player.openContainer != null && player.openContainer instanceof ContainerItemRouter) {
					TileEntity target = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
					if (target != null && target instanceof TileEntityItemRouter) {
						((ContainerItemRouter) player.openContainer).switchState(player.inventory, (TileEntityItemRouter) target, message.state);
					}
				}
			}

			return null;
		}
	}

}
