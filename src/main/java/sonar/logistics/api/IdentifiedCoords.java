package sonar.logistics.api;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.utils.BlockCoords;
import cpw.mods.fml.common.network.ByteBufUtils;

public class IdentifiedCoords {
	public String name;
	public BlockCoords coords;

	public IdentifiedCoords(String name, BlockCoords coords) {
		this.name = name;
		this.coords = coords;
	}

	public static IdentifiedCoords readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean("b")) {
			return new IdentifiedCoords(tag.getString("clientName"), BlockCoords.readFromNBT(tag));
		} else {
			return null;
		}

	}

	public static void writeToNBT(NBTTagCompound tag, IdentifiedCoords coords) {
		if (coords != null) {
			tag.setBoolean("b", true);
			tag.setString("clientName", coords.name);
			BlockCoords.writeToNBT(tag, coords.coords);
		} else {
			tag.setBoolean("b", false);
		}
	}

	public static IdentifiedCoords readCoords(ByteBuf buf) {
		if (buf.readBoolean()) {
			String name = ByteBufUtils.readUTF8String(buf);
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			return new IdentifiedCoords(name, coords);

		} else {
			return null;
		}
	}

	public static void writeCoords(ByteBuf buf, IdentifiedCoords coords) {
		if (coords != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, coords.name);
			BlockCoords.writeToBuf(buf, coords.coords);
		} else {
			buf.writeBoolean(false);
		}
	}


}
