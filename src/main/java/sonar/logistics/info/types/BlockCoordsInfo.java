package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.ILogicInfo;
import cpw.mods.fml.common.network.ByteBufUtils;

public class BlockCoordsInfo extends ILogicInfo<BlockCoordsInfo> {

	public String name;
	public BlockCoords coords;

	public static ILogicInfo createInfo(String name, BlockCoords coords) {
		BlockCoordsInfo info = new BlockCoordsInfo();
		info.name = name;
		info.coords = coords;
		return info;
	}

	@Override
	public String getName() {
		return "Coords";
	}

	@Override
	public int getProviderID() {
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
		return coords.toString();
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
	public BlockCoordsInfo instance() {
		return new BlockCoordsInfo();
	}

	@Override
	public void writeUpdate(BlockCoordsInfo currentInfo, NBTTagCompound tag) {
		if (!currentInfo.name.equals(name)) {
			this.name = currentInfo.name;
			tag.setString("n", name);
		}
		if (!currentInfo.coords.equals(coords)) {
			this.coords = currentInfo.coords;
			NBTTagCompound coordTag = new NBTTagCompound();
			BlockCoords.writeToNBT(coordTag, coords);
			tag.setTag("cT", coordTag);
		}

	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("n")) {
			this.name = tag.getString("n");
		}
		if (tag.hasKey("cT")) {
			this.coords = BlockCoords.readFromNBT(tag.getCompoundTag("cT"));
		}
	}

	@Override
	public SyncType isMatchingData(BlockCoordsInfo currentInfo) {
		if(!currentInfo.name.equals(name) || !currentInfo.coords.equals(coords)){
			return SyncType.SYNC;
		}
		return null;
	}
}
