package sonar.logistics.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.logistics.Logistics;
import cpw.mods.fml.common.network.ByteBufUtils;

public class StandardInfo extends Info {

	public boolean emptyData;
	public String category = "ERROR", subCategory = "ERROR", data, suffix;
	public int dataType;
	public byte providerID = -1, catID = -1, subCatID = -1;

	public StandardInfo() {

	}

	public StandardInfo(byte providerID, int category, int subCategory, Object data, String suffix) {
		this(providerID, category, subCategory, data);
		this.suffix = suffix;
	}

	public StandardInfo(byte providerID, String category, String subCategory, Object data, String suffix) {
		this(providerID, category, subCategory, data);
		this.suffix = suffix;
	}

	public StandardInfo(int providerID, int category, int subCategory, Object data) {
		this.providerID = (byte) providerID;
		this.catID = (byte) category;
		this.subCatID = (byte) subCategory;
		this.data = data.toString();
		this.dataType = data instanceof Integer ? 0 : 1;
	}

	public StandardInfo(int providerID, String category, String subCategory, Object data) {
		this.providerID = (byte) providerID;
		this.category = category;
		this.subCategory = subCategory;
		this.data = data.toString();
		this.dataType = data instanceof Integer ? 0 : 1;
	}

	@Override
	public String getName() {
		return "Standard";
	}

	@Override
	public byte getProviderID() {
		return providerID;
	}

	@Override
	public String getCategory() {
		
		return (catID==-1 ||providerID==-1)  ? category : Logistics.tileProviders.getRegisteredObject(providerID).getCategory(catID);
	}

	@Override
	public String getSubCategory() {
		return (subCatID==-1||providerID==-1)  ? subCategory : Logistics.tileProviders.getRegisteredObject(providerID).getSubCategory(subCatID);
	}

	@Override
	public String getData() {
		return !emptyData ? data : this.dataType==1? "NO DATA" : String.valueOf(0);
	}

	@Override
	public String getDisplayableData() {
		if (suffix != null) {
			return getData() + " " + suffix;
		} else {
			return getData();
		}
	}

	@Override
	public int getDataType() {
		return dataType;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.providerID = buf.readByte();
		
		if (buf.readBoolean()) {
			this.catID = buf.readByte();
		} else {
			this.category = ByteBufUtils.readUTF8String(buf);
		}
		if (buf.readBoolean()) {
			this.subCatID = buf.readByte();
		} else {
			this.subCategory = ByteBufUtils.readUTF8String(buf);
		}
		
		this.data = ByteBufUtils.readUTF8String(buf);
		this.dataType = buf.readInt();
		this.emptyData = buf.readBoolean();
		if (buf.readBoolean()) {
			this.suffix = ByteBufUtils.readUTF8String(buf);
		}

	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeByte(providerID);
		
		if (catID != -1) {
			buf.writeBoolean(true);
			buf.writeByte(catID);
		} else {
			buf.writeBoolean(false);
			ByteBufUtils.writeUTF8String(buf, category);
		}
		if (subCatID != -1) {
			buf.writeBoolean(true);
			buf.writeByte(subCatID);
		} else {
			buf.writeBoolean(false);
			ByteBufUtils.writeUTF8String(buf, subCategory);
		}
	
		ByteBufUtils.writeUTF8String(buf, data);
		buf.writeInt(dataType);
		buf.writeBoolean(emptyData);
		if (this.suffix != null) {
			buf.writeBoolean(true);
			ByteBufUtils.writeUTF8String(buf, suffix);
		} else {
			buf.writeBoolean(false);
		}
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.providerID = tag.getByte("prov");
		if (tag.getBoolean("BcatID")) {
			this.catID = tag.getByte("catID");
		} else {
			this.category = tag.getString("category");
		}
		if (tag.getBoolean("BsubCatID")) {
			this.subCatID = tag.getByte("subCatID");
		} else {
			this.subCategory = tag.getString("subCategory");
		}
		
		this.data = tag.getString("data");
		this.dataType = tag.getInteger("dataType");
		this.emptyData = tag.getBoolean("emptyData");
		if (tag.getBoolean("hasSuffix")) {
			this.suffix = tag.getString("suffix");
		}
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setByte("prov", providerID);
		if (catID != -1) {
			tag.setBoolean("BcatID", true);
			tag.setByte("catID", catID);
		} else {
			tag.setBoolean("BcatID", false);
			tag.setString("category", category);
		}
		if (subCatID != -1) {
			tag.setBoolean("BsubCatID", true);
			tag.setByte("subCatID", subCatID);
		} else {
			tag.setBoolean("BsubCatID", false);
			tag.setString("subCategory", subCategory);
		}
		tag.setString("data", data);
		tag.setInteger("dataType", dataType);
		tag.setBoolean("emptyData", emptyData);
		if (this.suffix != null) {
			tag.setBoolean("hasSuffix", true);
			tag.setString("suffix", suffix);
		} else {
			tag.setBoolean("hasSuffix", false);
		}
	}


	@Override
	public boolean isEqualType(Info info) {
		if (info != null && ((info.getName().equals("Fluid-Info") || info.getName().equals(this.getName())))) {
			return info.getProviderID() == this.providerID && info.getCategory().equals(this.getCategory()) && info.getSubCategory().equals(getSubCategory());
		}
		return false;
	}

	@Override
	public void emptyData() {
		if (!this.emptyData) {
			emptyData = true;
		}
	}

	@Override
	public StandardInfo newInfo() {
		return new StandardInfo();
	}

	public void setData(String string) {
		data = string;
	}
}
