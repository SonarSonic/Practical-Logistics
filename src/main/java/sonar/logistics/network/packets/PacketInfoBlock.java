package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.helpers.NBTHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.helpers.InfoHelper;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketInfoBlock implements IMessage {

	public int xCoord, yCoord, zCoord;
	public Info info;
	public boolean primary, setNull;

	public PacketInfoBlock() {
	}

	public PacketInfoBlock(int xCoord, int yCoord, int zCoord, boolean primary) {
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.primary = primary;
		this.setNull = true;
	}

	public PacketInfoBlock(int xCoord, int yCoord, int zCoord, Info info, boolean primary) {
		this.info = info;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
		this.primary = primary;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		this.setNull = buf.readBoolean();
		this.primary = buf.readBoolean();
		if (setNull == false) {
			this.info = Logistics.infoTypes.readFromBuf(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
		buf.writeBoolean(setNull);
		buf.writeBoolean(primary);
		if (setNull == false) {
			Logistics.infoTypes.writeToBuf(buf, info);
		}

	}

	public static class Handler implements IMessageHandler<PacketInfoBlock, IMessage> {

		@Override
		public IMessage onMessage(PacketInfoBlock message, MessageContext ctx) {
			World world = ctx.getServerHandler().playerEntity.worldObj;

			TileEntity te = world.getTileEntity(message.xCoord, message.yCoord, message.zCoord);
			if (te == null) {
				return null;
			}
			Object target = FMPHelper.checkObject(te);
			if(target!=null && target instanceof ITileHandler){
				TileHandler handler = ((ITileHandler) target).getTileHandler();;
				if(handler!=null && handler instanceof InfoReaderHandler){
					InfoReaderHandler reader = (InfoReaderHandler) handler;					
					if (message.primary) {
						reader.primaryInfo.setInfo(message.info);
					} else {
						reader.secondaryInfo.setInfo(message.info);
					}
				}
			}
			return null;
		}
	}
}
