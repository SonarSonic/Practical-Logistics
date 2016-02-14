package sonar.logistics.api;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import cpw.mods.fml.common.network.ByteBufUtils;

public class IdentifiedCoords {
	public String suffix;
	public ItemStack block;
	public BlockCoords blockCoords;

	public IdentifiedCoords(String name, ItemStack block, BlockCoords coords) {
		this.suffix = name;
		this.block = block;
		this.blockCoords = coords;
	}

	public static IdentifiedCoords readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean("b")) {
			return new IdentifiedCoords(tag.getString("clientName"), tag.hasKey("block") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("block")) : null, BlockCoords.readFromNBT(tag));
		} else {
			return null;
		}
	}

	public static void writeToNBT(NBTTagCompound tag, IdentifiedCoords coords) {
		if (coords != null) {
			tag.setBoolean("b", true);
			tag.setString("clientName", coords.suffix);
			if (coords.block != null) {
				NBTTagCompound block = new NBTTagCompound();
				coords.block.writeToNBT(block);
				tag.setTag("block", block);
			}
			BlockCoords.writeToNBT(tag, coords.blockCoords);
		} else {
			tag.setBoolean("b", false);
		}
	}

	public static IdentifiedCoords readCoords(ByteBuf buf) {
		if (buf.readBoolean()) {
			String name = ByteBufUtils.readUTF8String(buf);
			ItemStack block = null;
			if (buf.readBoolean()) {
				block = ByteBufUtils.readItemStack(buf);
			}
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			return new IdentifiedCoords(name, block, coords);

		} else {
			return null;
		}
	}

	public static void writeCoords(ByteBuf buf, IdentifiedCoords coords) {
		if (coords != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, coords.suffix);
			if (coords.block != null) {
				buf.writeBoolean(true);
				ByteBufUtils.writeItemStack(buf, coords.block);
			} else {
				buf.writeBoolean(false);
			}

			BlockCoords.writeToBuf(buf, coords.blockCoords);
		} else {
			buf.writeBoolean(false);
		}
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof IdentifiedCoords) {
			IdentifiedCoords coords = (IdentifiedCoords) obj;
			return suffix.equals(coords.suffix);
		}
		return false;
	}
}
