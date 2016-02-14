package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileEntity;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketCoordsSelection extends PacketTileEntity {

	public IdentifiedCoords coords;

	public PacketCoordsSelection() {
	}

	public PacketCoordsSelection(int x, int y, int z, IdentifiedCoords info) {
		super(x, y, z);
		this.coords = info;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		this.coords = IdentifiedCoords.readFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		NBTTagCompound tag = new NBTTagCompound();
		IdentifiedCoords.writeToNBT(tag, coords);
		ByteBufUtils.writeTag(buf, tag);

	}

	public static class Handler extends PacketTileEntityHandler<PacketCoordsSelection> {

		@Override
		public IMessage processMessage(PacketCoordsSelection message, TileEntity target) {
			if (!target.getWorldObj().isRemote) {
				if (target != null && target instanceof TileEntityDataReceiver) {
					((TileEntityDataReceiver) target).emitter.setCoords(message.coords);
				} else {
					TileHandler handler = FMPHelper.getHandler(target);
					if (handler != null && handler instanceof ChannelSelectorHandler) {
						((ChannelSelectorHandler) handler).channel.setCoords(message.coords);
					}
				}
			}
			return null;
		}
	}
}
