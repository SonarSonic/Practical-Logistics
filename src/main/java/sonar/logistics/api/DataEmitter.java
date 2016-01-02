package sonar.logistics.api;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.BlockCoords;

public class DataEmitter {
	public String name;
	public BlockCoords coords;
	
	public DataEmitter(String name, BlockCoords coords){
		this.name = name;
		this.coords = coords;
	}	
	public static DataEmitter readFromNBT(NBTTagCompound tag) {
		
		return new DataEmitter(tag.getString("clientName"), BlockCoords.readFromNBT(tag));
	}

	public static void writeToNBT(NBTTagCompound tag, DataEmitter info) {
		
		tag.setString("clientName", info.name);
		BlockCoords.writeToNBT(tag, info.coords);
		
	}
}
