package sonar.logistics.api.info;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.ICategoryProvider;
import cpw.mods.fml.common.network.ByteBufUtils;

public class LogicInfo<T extends LogicInfo> extends ILogicInfo<T> {

	public boolean emptyData;
	public boolean entity = false;
	public String category = "ERROR", subCategory = "ERROR", data = "", suffix = "";
	public int dataType;
	public int providerID = -1, catID = -1, subCatID = -1;

	public LogicInfo() {
	}

	public LogicInfo(int providerID, int category, int subCategory, Object data) {
		this.providerID = providerID;
		this.catID = category;
		this.subCatID = subCategory;
		this.data = data.toString();
		this.dataType = data instanceof Long || data instanceof Integer || data instanceof Short ? 0 : 1;
	}

	public LogicInfo(int providerID, String category, String subCategory, Object data) {
		this.providerID = providerID;
		this.category = category;
		this.subCategory = subCategory;
		this.data = data.toString();
		this.dataType = data instanceof Long || data instanceof Integer || data instanceof Short ? 0 : 1;
	}

	public LogicInfo addSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public LogicInfo isEntityData(boolean bool) {
		this.entity = bool;
		return this;
	}

	@Override
	public String getName() {
		return "Standard";
	}

	@Override
	public int getProviderID() {
		return providerID;
	}

	@Override
	public String getCategory() {
		ICategoryProvider provider = getRegistryObject();
		if (providerID != -1 && provider == null) {
			return "UNLOADED MOD";
		}
		return (catID == -1 || providerID == -1) ? category : provider.getCategory(catID);
	}

	@Override
	public String getSubCategory() {
		ICategoryProvider provider = getRegistryObject();
		if (providerID != -1 && provider == null) {
			return "ERROR";
		}
		return (subCatID == -1 || providerID == -1) ? subCategory : provider.getSubCategory(subCatID);
	}

	public ICategoryProvider getRegistryObject() {
		if (entity) {
			return LogisticsAPI.getRegistry().getEntityProvider(providerID);
		}
		return LogisticsAPI.getRegistry().getTileProvider(providerID);
	}

	@Override
	public String getData() {
		return !emptyData ? data : this.dataType == 1 ? "NO DATA" : String.valueOf(0);
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
		this.entity = buf.readBoolean();
		this.providerID = buf.readInt();
		if (buf.readBoolean()) {
			this.catID = buf.readInt();
		} else {
			this.category = ByteBufUtils.readUTF8String(buf);
		}
		if (buf.readBoolean()) {
			this.subCatID = buf.readInt();
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
		buf.writeBoolean(entity);
		buf.writeInt(providerID);
		if (catID != -1) {
			buf.writeBoolean(true);
			buf.writeInt(catID);
		} else {
			buf.writeBoolean(false);
			ByteBufUtils.writeUTF8String(buf, category);
		}
		if (subCatID != -1) {
			buf.writeBoolean(true);
			buf.writeInt(subCatID);
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
		this.entity = tag.getBoolean("e");
		this.providerID = tag.getInteger("prov");
		if (tag.getBoolean("BcatID")) {
			this.catID = tag.getInteger("catID");
		} else {
			this.category = tag.getString("category");
		}
		if (tag.getBoolean("BsubCatID")) {
			this.subCatID = tag.getInteger("subCatID");
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
		tag.setBoolean("e", entity);
		tag.setInteger("prov", providerID);
		if (catID != -1) {
			tag.setBoolean("BcatID", true);
			tag.setInteger("catID", catID);
		} else {
			tag.setBoolean("BcatID", false);
			tag.setString("category", category);
		}
		if (subCatID != -1) {
			tag.setBoolean("BsubCatID", true);
			tag.setInteger("subCatID", subCatID);
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
	public LogicInfo instance() {
		return new LogicInfo();
	}

	public void setData(String string) {
		data = string;
	}

	@Override
	public void writeUpdate(LogicInfo currentInfo, NBTTagCompound tag) {
		if (currentInfo.entity != this.entity) {
			entity = currentInfo.entity;
			tag.setBoolean("e", entity);
		}
		if (currentInfo.providerID != this.providerID) {
			providerID = currentInfo.providerID;
			tag.setInteger("id", providerID);
		}
		if (currentInfo.dataType != this.dataType) {
			dataType = currentInfo.dataType;
			tag.setInteger("dT", dataType);
		}
		if (currentInfo.catID == -1 && !currentInfo.category.equals(this.category)) {
			category = currentInfo.category;
			tag.setString("c", category);
		} else if (currentInfo.catID != -1 && currentInfo.catID != this.catID) {
			catID = currentInfo.catID;
			tag.setInteger("cI", catID);
		}
		if (currentInfo.subCatID == -1 && !currentInfo.subCategory.equals(this.subCategory)) {
			subCategory = currentInfo.subCategory;
			tag.setString("sC", subCategory);

		} else if (currentInfo.subCatID != -1 && currentInfo.subCatID != this.subCatID) {
			subCatID = currentInfo.subCatID;
			tag.setInteger("sCI", subCatID);
		}
		if (!currentInfo.data.equals(this.data)) {
			data = currentInfo.data;
			tag.setString("d", data);
		}
		if (currentInfo.suffix != null && !currentInfo.suffix.equals(this.suffix)) {
			suffix = currentInfo.suffix;
			tag.setString("s", suffix);
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("e")) {
			entity = tag.getBoolean("e");
		}
		if (tag.hasKey("id")) {
			providerID = tag.getInteger("id");
		}
		if (tag.hasKey("dT")) {
			dataType = tag.getInteger("dT");
		}
		if (tag.hasKey("c")) {
			category = tag.getString("c");
		}
		if (tag.hasKey("cI")) {
			catID = tag.getInteger("cI");
		}
		if (tag.hasKey("sC")) {
			subCategory = tag.getString("sC");
		}
		if (tag.hasKey("sCI")) {
			subCatID = tag.getInteger("sCI");
		}
		if (tag.hasKey("d")) {
			data = tag.getString("d");
		}
		if (tag.hasKey("s")) {
			suffix = tag.getString("s");
		}
	}

	@Override
	public SyncType isMatchingData(T currentInfo) {	
		if(currentInfo.getProviderID() != this.providerID || currentInfo.dataType != dataType || !currentInfo.category.equals(category) || !currentInfo.subCategory.equals(subCategory) || currentInfo.catID != catID || currentInfo.subCatID != subCatID){
			return SyncType.SAVE;
		}
		if(!currentInfo.data.equals(data) || !currentInfo.suffix.equals(suffix)){
			return SyncType.SYNC;
		}
		return null;
	}
}
