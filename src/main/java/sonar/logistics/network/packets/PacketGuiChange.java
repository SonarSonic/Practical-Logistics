package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.common.handlers.FluidReaderHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import sonar.logistics.network.LogisticsGui;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketGuiChange extends PacketCoords {

	public ILogicInfo info;
	public boolean state;
	public int guiType;

	public PacketGuiChange() {
	}

	public PacketGuiChange(int x, int y, int z, boolean state, int guiType) {
		super(x, y, z);
		this.state = state;
		this.guiType = guiType;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.state = buf.readBoolean();
		this.guiType = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeBoolean(state);
		buf.writeInt(guiType);
	}

	public static class Handler implements IMessageHandler<PacketGuiChange, IMessage> {

		@Override
		public IMessage onMessage(PacketGuiChange message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().playerEntity;
			if (player != null) {
				World world = ctx.getServerHandler().playerEntity.worldObj;
				if (player.openContainer != null) {
					if (LogisticsGui.inventoryReader == message.guiType && player.openContainer instanceof ContainerInventoryReader) {
						TileEntity target = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
						TileHandler handler = FMPHelper.getHandler(target);
						if (handler != null && handler instanceof InventoryReaderHandler) {
							((ContainerInventoryReader) player.openContainer).addSlots((InventoryReaderHandler) handler, player.inventory, message.state);
						}
					} else if (LogisticsGui.fluidReader == message.guiType && player.openContainer instanceof ContainerFluidReader) {
						TileEntity target = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
						TileHandler handler = FMPHelper.getHandler(target);
						if (handler != null && handler instanceof FluidReaderHandler) {
							((ContainerFluidReader) player.openContainer).addSlots((FluidReaderHandler) handler, player.inventory, message.state);
						}
					}
				}
			}

			return null;
		}
	}
}
