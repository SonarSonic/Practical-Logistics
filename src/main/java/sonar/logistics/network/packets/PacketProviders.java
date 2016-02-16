package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.info.types.CategoryInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketProviders extends PacketCoords {

	public List<Info> info;

	public PacketProviders() {
	}

	public PacketProviders(int x, int y, int z, List<Info> info) {
		super(x, y, z);
		this.info = info;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		if (buf.readBoolean()) {
			info = new ArrayList();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				info.add(Logistics.infoTypes.readFromBuf(buf));
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		if (info != null) {
			buf.writeBoolean(true);
			buf.writeInt(info.size());
			for (int i = 0; i < info.size(); i++) {
				Logistics.infoTypes.writeToBuf(buf, info.get(i));
			}
		} else {
			buf.writeBoolean(false);
		}

	}

	public static class Handler extends PacketTileEntityHandler<PacketProviders> {

		@Override
		public IMessage processMessage(PacketProviders message, TileEntity te) {
			Object target = FMPHelper.checkObject(te);
			if (target != null && target instanceof ITileHandler) {
				TileHandler handler = ((ITileHandler) target).getTileHandler();
				if (handler != null && handler instanceof InfoReaderHandler) {
					InfoReaderHandler reader = (InfoReaderHandler) handler;
					if (message.info != null) {
						List<Info> newInfo = new ArrayList();
						Info lastInfo = null;
						for (Info blockInfo : message.info) {
							if (lastInfo == null || !lastInfo.getCategory().equals(blockInfo.getCategory())) {
								newInfo.add(CategoryInfo.createInfo(blockInfo.getCategory()));
							}
							newInfo.add(blockInfo);
							lastInfo = blockInfo;
						}
						reader.clientInfo = newInfo;
					} else {
						reader.clientInfo = null;
					}
				}

			}
			return null;
		}
	}
}
