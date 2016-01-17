package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.logistics.api.Info;
import cpw.mods.fml.common.network.ByteBufUtils;

public class CategoryInfo extends Info {

	public String category;

	public static CategoryInfo createInfo(String category) {
		CategoryInfo info = new CategoryInfo();
		info.category = category;
		return info;
	}

	@Override
	public String getType() {
		return "Header";
	}

	@Override
	public byte getProviderID() {
		return -1;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getSubCategory() {
		return category;
	}

	@Override
	public String getData() {
		return category;
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
		category = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, category);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.category = tag.getString("category");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setString("category", category);
	}

	@Override
	public boolean isEqualType(Info info) {
		if (info != null && info.getType() == this.getType()) {
			return info.getCategory().equals(category);
		}
		return false;
	}

	@Override
	public void emptyData() {

	}

	@Override
	public CategoryInfo newInfo() {
		return new CategoryInfo();
	}

}
