package sonar.logistics.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ExternalCoords {
	public String coordString = "";
	public ItemStack block;
	public BlockCoords blockCoords;
	public ForgeDirection dir;

	public ExternalCoords(String name, ItemStack block, BlockCoords coords, ForgeDirection dir) {
		if (name == null)
			coordString = "";
		else
			this.coordString = name;
		this.block = block;
		this.blockCoords = coords;
		this.dir = dir;
	}

	public ExternalCoords(IdentifiedCoords coords, ForgeDirection dir) {
		this.coordString = coords.coordString;
		this.block = coords.block;
		this.blockCoords = coords.blockCoords;
		this.dir = dir;
	}

	public IdentifiedCoords getIdentifiedCoords() {
		return new IdentifiedCoords(coordString, block, blockCoords);
	}

	public static ExternalCoords readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean("b")) {
			return new ExternalCoords(tag.getString("clientName"), tag.hasKey("block") ? ItemStack.loadItemStackFromNBT(tag.getCompoundTag("block")) : null, BlockCoords.readFromNBT(tag), ForgeDirection.valueOf(tag.getString("dir")));
		} else {
			return null;
		}
	}

	public static void writeToNBT(NBTTagCompound tag, ExternalCoords coords) {
		if (coords != null) {
			tag.setBoolean("b", true);
			tag.setString("clientName", coords.coordString);
			if (coords.block != null) {
				NBTTagCompound block = new NBTTagCompound();
				coords.block.writeToNBT(block);
				tag.setTag("block", block);
			}
			BlockCoords.writeToNBT(tag, coords.blockCoords);
			tag.setString("dir", coords.dir.name());
		} else {
			tag.setBoolean("b", false);
		}
	}

	public static ExternalCoords readCoords(ByteBuf buf) {
		if (buf.readBoolean()) {
			String name = ByteBufUtils.readUTF8String(buf);
			ItemStack block = null;
			if (buf.readBoolean()) {
				block = ByteBufUtils.readItemStack(buf);
			}
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			ForgeDirection dir = ForgeDirection.valueOf(ByteBufUtils.readUTF8String(buf));
			return new ExternalCoords(name, block, coords, dir);

		} else {
			return null;
		}
	}

	public static void writeCoords(ByteBuf buf, ExternalCoords coords) {
		if (coords != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, coords.coordString);
			if (coords.block != null) {
				buf.writeBoolean(true);
				ByteBufUtils.writeItemStack(buf, coords.block);
			} else {
				buf.writeBoolean(false);
			}
			BlockCoords.writeToBuf(buf, coords.blockCoords);
			ByteBufUtils.writeUTF8String(buf, coords.dir.name());
		} else {
			buf.writeBoolean(false);
		}
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof ExternalCoords) {
			ExternalCoords coords = (ExternalCoords) obj;
			return coordString.equals(coords.coordString) && coords.blockCoords.equals(blockCoords) && coords.dir.name().equals(dir.name());
		}
		return false;
	}
}
