package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.common.handlers.InventoryReaderHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketInventoryReader implements IMessage {

	public int xCoord, yCoord, zCoord;
	public ItemStack selected;

	public PacketInventoryReader() {
	}

	public PacketInventoryReader(int xCoord, int yCoord, int zCoord, ItemStack selected) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.selected = selected;
	}


	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.selected = ByteBufUtils.readItemStack(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		ByteBufUtils.writeItemStack(buf, selected);
	}

	public static class Handler implements IMessageHandler<PacketInventoryReader, IMessage> {

		@Override
		public IMessage onMessage(PacketInventoryReader message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;

			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (te == null) {
				return null;
			}
			Object target = FMPHelper.checkObject(te);
			if(target!=null && target instanceof ITileHandler){
				TileHandler handler = ((ITileHandler) target).getTileHandler();;
				if(handler!=null && handler instanceof InventoryReaderHandler){
					InventoryReaderHandler reader = (InventoryReaderHandler) handler;			
					reader.slots[0]=message.selected;				
				}
			}
			return null;
		}
	}
}
