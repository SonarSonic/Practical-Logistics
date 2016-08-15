package sonar.logistics.api.info;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.LogicRegistry.RegistryType;

public class LogicInfo implements IMonitorInfo<LogicInfo>, INBTSyncable, INameableInfo<LogicInfo> {

	public InfoType infoType;
	public String identifier;
	public RegistryType registryType;
	public Object obj;
	public boolean isCategory = false;

	public static LogicInfo buildCategoryInfo(RegistryType type) {
		LogicInfo info = new LogicInfo();
		info.registryType = type;
		info.isCategory = true;
		return info;
	}

	public static LogicInfo buildDirectInfo(String identifier, RegistryType type, Object obj) {
		LogicInfo info = new LogicInfo();
		info.infoType = InfoType.getInfoType(obj);
		info.obj = obj;
		info.registryType = type;
		info.identifier = identifier;

		if (info.infoType == InfoType.NONE) {
			Logistics.logger.error(String.format("Invalid Info: %s with object %s", identifier, obj));
			return null;
		}
		return info;
	}

	@Override
	public boolean isIdenticalInfo(LogicInfo info) {
		return isMatchingInfo(info) && obj.equals(info.obj);
	}

	@Override
	public boolean isMatchingInfo(LogicInfo info) {
		return infoType.equals(info.infoType) && identifier.equals(info.identifier) && registryType.equals(info.registryType);
	}

	@Override
	public void updateFrom(LogicInfo info) {
		obj = info.obj;
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		infoType = InfoType.values()[nbt.getInteger("type")];
		registryType = RegistryType.values()[nbt.getInteger("registryType")];
		identifier = nbt.getString("id");
		obj = NBTHelper.readNBTBase(nbt, infoType.tagType, "obj");
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setInteger("type", infoType.ordinal());
		nbt.setInteger("registryType", registryType.ordinal());
		nbt.setString("id", identifier);
		NBTHelper.writeNBTBase(nbt, infoType.tagType, obj, "obj");
		return nbt;
	}

	public static LogicInfo readFromNBT(NBTTagCompound nbt) {
		LogicInfo info = new LogicInfo();
		info.readData(nbt, SyncType.SAVE);
		return info;

	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		return writeData(tag, SyncType.SAVE);
	}

	public String getClientIdentifier() {
		return FontHelper.translate("pl." + identifier);
	}

	public String getClientObject() {
		String prefix = "", suffix = "";
		Pair<String, String> adjustment = LogicRegistry.infoAdjustments.get(identifier);
		if (identifier.equals("Block.getUnlocalizedName")) {
			return FontHelper.translate(obj.toString() + ".name");
		}
		if (adjustment != null) {
			if (!adjustment.a.isEmpty())
				prefix = adjustment.a + " ";
			if (!adjustment.b.isEmpty())
				suffix = " " + adjustment.b;
		}
		return prefix + obj.toString() + suffix;
	}

	public String getClientType() {
		return infoType.toString().toLowerCase();
	}

	public enum InfoType {
		BOOLEAN(Constants.NBT.TAG_END, Boolean.class), BYTE(Constants.NBT.TAG_BYTE, Byte.class), SHORT(Constants.NBT.TAG_SHORT, Short.class), INTEGER(Constants.NBT.TAG_INT, Integer.class), LONG(Constants.NBT.TAG_LONG, Long.class), FLOAT(Constants.NBT.TAG_FLOAT, Float.class), DOUBLE(Constants.NBT.TAG_DOUBLE, Double.class), STRING(Constants.NBT.TAG_STRING, String.class), NONE(-1, null);
		public int tagType;
		public Class<?> classType;

		InfoType(int tagType, Class<?> classType) {
			this.tagType = tagType;
			this.classType = classType;
		}

		public static InfoType getInfoType(Object obj) {
			for (InfoType type : values()) {
				if (type.classType != null && (type.classType.isInstance(obj) || type.classType.isAssignableFrom(obj.getClass()))) {
					return type;
				}
			}
			return NONE;
		}
	}

	@Override
	public boolean isHeader() {
		return isCategory;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof LogicInfo;
	}

}
