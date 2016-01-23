package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.helpers.NBTHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.CategoryInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

public class PacketProviders implements IMessage {

	public int xCoord, yCoord, zCoord;
	public List<Info> info;

	public PacketProviders() {
	}

	public PacketProviders(int xCoord, int yCoord, int zCoord, List<Info> info) {
		this.info = info;
		this.xCoord = xCoord;
		this.yCoord = yCoord;
		this.zCoord = zCoord;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.xCoord = buf.readInt();
		this.yCoord = buf.readInt();
		this.zCoord = buf.readInt();
		if (buf.readBoolean()) {
			info = new ArrayList();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				info.add(Logistics.infoTypes.readFromBuf(buf));
			}
		}
		TileEntity tile = Minecraft.getMinecraft().thePlayer.worldObj.getTileEntity(xCoord, yCoord, zCoord);
		Object target = FMPHelper.checkObject(tile);
		if (target != null && target instanceof ITileHandler) {
			TileHandler handler = ((ITileHandler) target).getTileHandler();
			if (handler != null && handler instanceof InfoReaderHandler) {
				InfoReaderHandler reader = (InfoReaderHandler) handler;
				if (info != null) {
					List<Info> newInfo = new ArrayList();
					Info lastInfo = null;
					for (Info blockInfo : info) {
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

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(xCoord);
		buf.writeInt(yCoord);
		buf.writeInt(zCoord);
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

	public static class Handler implements IMessageHandler<PacketProviders, IMessage> {

		@Override
		public IMessage onMessage(PacketProviders message, MessageContext ctx) {
			return null;
		}
	}
}
