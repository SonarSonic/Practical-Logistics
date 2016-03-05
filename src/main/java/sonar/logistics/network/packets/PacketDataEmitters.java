package sonar.logistics.network.packets;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.network.PacketCoords;
import sonar.core.network.PacketTileEntityHandler;
import sonar.logistics.api.ExternalCoords;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;

public class PacketDataEmitters extends PacketCoords {

	public List<IdentifiedCoords> coords;

	public PacketDataEmitters() {
	}

	public PacketDataEmitters(int x, int y, int z, List<IdentifiedCoords> emitters) {
		super(x, y, z);
		this.coords = emitters;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		if (buf.readBoolean()) {
			coords = new ArrayList();
			int size = buf.readInt();
			for (int i = 0; i < size; i++) {
				coords.add(IdentifiedCoords.readFromNBT(ByteBufUtils.readTag(buf)));
			}
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		if (coords != null) {
			buf.writeBoolean(true);
			buf.writeInt(coords.size());
			for (int i = 0; i < coords.size(); i++) {
				NBTTagCompound tag = new NBTTagCompound();
				IdentifiedCoords.writeToNBT(tag, coords.get(i));
				ByteBufUtils.writeTag(buf, tag);
			}
		} else {
			buf.writeBoolean(false);
		}

	}

	public static class Handler extends PacketTileEntityHandler<PacketDataEmitters> {

		@Override
		public IMessage processMessage(PacketDataEmitters message, TileEntity target) {
			if (target != null && target instanceof TileEntityDataReceiver) {
				TileEntityDataReceiver receiver = (TileEntityDataReceiver) target;
				receiver.emitters = message.coords;
			}

			return null;
		}
	}
}
