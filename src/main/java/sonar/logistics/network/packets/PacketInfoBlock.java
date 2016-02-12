package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileEntity;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.common.handlers.InfoReaderHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketInfoBlock extends PacketTileEntity {

	public Info info;
	public boolean primary, setNull;

	public PacketInfoBlock() {
	}

	public PacketInfoBlock(int x, int y, int z, boolean primary) {
		super(x, y, z);
		this.primary = primary;
		this.setNull = true;
	}

	public PacketInfoBlock(int x, int y, int z, Info info, boolean primary) {
		super(x, y, z);
		this.info = info;
		this.primary = primary;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.setNull = buf.readBoolean();
		this.primary = buf.readBoolean();
		if (setNull == false) {
			this.info = Logistics.infoTypes.readFromBuf(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeBoolean(setNull);
		buf.writeBoolean(primary);
		if (setNull == false) {
			Logistics.infoTypes.writeToBuf(buf, info);
		}
	}

	public static class Handler extends PacketTileEntityHandler<PacketInfoBlock> {

		@Override
		public IMessage processMessage(PacketInfoBlock message, TileEntity te) {
			Object target = FMPHelper.checkObject(te);
			if (target != null && target instanceof ITileHandler) {
				TileHandler handler = ((ITileHandler) target).getTileHandler();
				;
				if (handler != null && handler instanceof InfoReaderHandler) {
					InfoReaderHandler reader = (InfoReaderHandler) handler;
					if (message.primary) {
						reader.primaryInfo.setObject(message.info);
					} else {
						reader.secondaryInfo.setObject(message.info);
					}
				}
			}
			return null;
		}
	}
}
