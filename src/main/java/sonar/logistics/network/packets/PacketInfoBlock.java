package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.Info;
import sonar.logistics.common.handlers.EnergyReaderHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.info.types.StoredEnergyInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketInfoBlock extends PacketCoords {

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
			TileHandler handler = FMPHelper.getHandler(te);
			if (handler != null) {
				if (handler instanceof InfoReaderHandler) {
					InfoReaderHandler reader = (InfoReaderHandler) handler;
					if (message.primary) {
						reader.primaryInfo.setObject(message.info);
					} else {
						reader.secondaryInfo.setObject(message.info);
					}
				} else if(handler instanceof EnergyReaderHandler){
					EnergyReaderHandler reader = (EnergyReaderHandler) handler;
					reader.primaryInfo.setObject((StoredEnergyInfo) message.info);
				}
			}

			return null;
		}
	}
}
