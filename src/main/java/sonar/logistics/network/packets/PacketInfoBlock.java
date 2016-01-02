package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.SonarAPI;
import sonar.logistics.api.Info;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.integration.multipart.InfoReaderPart;
import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
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
			this.info = InfoHelper.readInfo(buf);
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
			InfoHelper.writeInfo(buf, info);
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
			if (te != null && te instanceof TileEntityInfoReader) {
				TileEntityInfoReader node = (TileEntityInfoReader) te;
				if (message.primary) {
					node.primaryInfo = message.info;
				} else {
					node.secondaryInfo = message.info;
				}
			}
			if (SonarAPI.forgeMultipartLoaded() && te != null && te instanceof TileMultipart) {
				TMultiPart part = ((TileMultipart) te).jPartList().get(0);
				if (part != null && part instanceof InfoReaderPart) {
					InfoReaderPart node = (InfoReaderPart) part;
					if (message.primary) {
						node.primaryInfo = message.info;
					} else
						node.secondaryInfo = message.info;
				}
			}
			return null;
		}
	}
}
