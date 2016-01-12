package sonar.logistics.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.BlockCoords;
import cpw.mods.fml.common.network.ByteBufUtils;

public class DataEmitter {
	public String name;
	public BlockCoords coords;
	
	public DataEmitter(String name, BlockCoords coords){
		this.name = name;
		this.coords = coords;
	}	
	
	
	public static DataEmitter readFromNBT(NBTTagCompound tag) {
		if (tag.getBoolean("b")) {
			return new DataEmitter(tag.getString("clientName"), BlockCoords.readFromNBT(tag));
		} else {
			return null;
		}
				
	}

	public static void writeToNBT(NBTTagCompound tag, DataEmitter info) {
		if (info != null) {
			tag.setBoolean("b", true);	
			tag.setString("clientName", info.name);
			BlockCoords.writeToNBT(tag, info.coords);
		} else {
			tag.setBoolean("b", false);				
		}
	}
	
	public static DataEmitter readInfo(ByteBuf buf) {
		if (buf.readBoolean()) {			
			String name = ByteBufUtils.readUTF8String(buf);
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			return new DataEmitter(name,coords);

		} else {
			return null;
		}
	}

	public static void writeInfo(ByteBuf buf, DataEmitter info) {
		if (info != null) {
			buf.writeBoolean(true);			
			ByteBufUtils.writeUTF8String(buf, info.name);
			BlockCoords.writeToBuf(buf, info.coords);
		} else {
			buf.writeBoolean(false);
		}
	}
}
