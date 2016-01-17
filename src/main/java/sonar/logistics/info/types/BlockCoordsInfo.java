package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import cpw.mods.fml.common.network.ByteBufUtils;

public class BlockCoordsInfo extends Info {

	public boolean emptyData;
	public String name;
	public BlockCoords coords;

	public static Info createInfo(String name, BlockCoords coords) {
		BlockCoordsInfo info = new BlockCoordsInfo();
		info.name = name;
		info.coords = coords;
		return info;
	}

	@Override
	public String getType() {
		return "Coords";
	}

	@Override
	public byte getProviderID() {
		return -1;
	}

	@Override
	public String getCategory() {
		return name;
	}

	@Override
	public String getSubCategory() {
		return name;
	}
	@Override
	public String getData() {
		return coords.getRender();
	}
	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public int getDataType() {
		return 1;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.name = ByteBufUtils.readUTF8String(buf);
		this.coords = BlockCoords.readFromBuf(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		BlockCoords.writeToBuf(buf, coords);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.name = tag.getString("name");
		this.coords = BlockCoords.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("name", name);
		BlockCoords.writeToNBT(tag, coords);
	}

	@Override
	public boolean isEqualType(Info info) {
		if (info != null && info instanceof BlockCoordsInfo) {
			BlockCoordsInfo coordInfo = (BlockCoordsInfo) info;
			return info.getCategory().equals(name) && BlockCoords.equalCoords(coordInfo.coords, coords);
		}
		return false;
	}

	@Override
	public void emptyData() {

	}

	@Override
	public BlockCoordsInfo newInfo() {
		return new BlockCoordsInfo();
	}

}
