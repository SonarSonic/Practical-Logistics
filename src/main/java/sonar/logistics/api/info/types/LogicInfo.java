package sonar.logistics.api.info.types;

import java.util.Map;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.ObjectType;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUnidentifiedObject;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.ISuffixable;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.display.ScreenInteractionEvent;
import sonar.logistics.api.info.IAdvancedClickableInfo;
import sonar.logistics.api.info.IComparableInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.connections.monitoring.InfoMonitorHandler;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.info.LogicInfoRegistry;
import sonar.logistics.info.LogicInfoRegistry.RegistryType;

/** default info type, created by the LogicRegistry */
@LogicInfoType(id = LogicInfo.id, modid = Logistics.MODID)
public class LogicInfo extends BaseInfo<LogicInfo> implements INameableInfo<LogicInfo>,  ISuffixable, IComparableInfo<LogicInfo> {

	public static final String id = "logic";
	public static final LogicMonitorHandler<LogicInfo> handler = LogicMonitorHandler.instance(InfoMonitorHandler.id);
	private String suffix, prefix;
	private SyncTagType.STRING identifier = new SyncTagType.STRING(1);
	private SyncEnum<RegistryType> registryType = new SyncEnum(RegistryType.values(), 2);
	private SyncUnidentifiedObject obj = new SyncUnidentifiedObject(3);
	private SyncTagType.BOOLEAN isCategory = new SyncTagType.BOOLEAN(4);

	{
		syncParts.addParts(identifier, registryType, obj, isCategory);
	}

	public LogicInfo() {
		super();
	}

	public static LogicInfo buildCategoryInfo(RegistryType type) {
		LogicInfo info = new LogicInfo();
		info.registryType.setObject(type);
		info.isCategory.setObject(true);
		return info;
	}

	public static LogicInfo buildDirectInfo(String identifier, RegistryType type, Object obj) {
		LogicInfo info = new LogicInfo();
		info.obj.set(obj, ObjectType.getInfoType(obj));
		info.registryType.setObject(type);
		info.identifier.setObject(identifier);
		if (info.obj.objectType == ObjectType.NONE) {
			Logistics.logger.error(String.format("Invalid Info: %s with object %s", identifier, obj));
			return null;
		}
		return info;
	}

	@Override
	public LogicMonitorHandler<LogicInfo> getHandler() {
		return handler;
	}

	@Override
	public boolean isIdenticalInfo(LogicInfo info) {
		return isMatchingInfo(info) && obj.get().equals(info.obj.get());
	}

	@Override
	public boolean isMatchingInfo(LogicInfo info) {
		if (this.isCategory.getObject()) {
			return info.isCategory.getObject() && registryType.getObject().equals(info.registryType.getObject());
		}
		return obj.objectType != null && obj.objectType.equals(info.obj.objectType) && identifier.getObject().equals(info.identifier.getObject()) && registryType.getObject().equals(info.registryType.getObject());
	}

	public RegistryType getRegistryType() {
		return registryType.getObject();
	}

	public String getClientIdentifier() {
		return FontHelper.translate("pl." + identifier);
	}

	public Pair<String, String> updateAdjustments(boolean forceUpdate) {
		if (forceUpdate || (prefix == null || suffix == null)) {
			prefix = "";
			suffix = "";
			Pair<String, String> adjustment = LogicInfoRegistry.infoAdjustments.get(identifier.getObject());
			/// if (identifier.getObject().equals("Block.getUnlocalizedName")) {
			// return FontHelper.translate(obj.get().toString() + ".name");
			// }
			if (adjustment != null) {
				if (!adjustment.a.isEmpty())
					prefix = adjustment.a + " ";
				if (!adjustment.b.isEmpty())
					suffix = " " + adjustment.b;
			}
		}
		return new Pair(prefix, suffix);
	}

	public String getClientObject() {
		if (identifier.getObject().equals("Block.getUnlocalizedName")) {
			return FontHelper.translate(obj.get().toString() + ".name");
		}
		updateAdjustments(false);
		return prefix + obj.get().toString() + suffix;
	}

	public String getClientType() {
		return obj.objectType.toString().toLowerCase();
	}

	public Object getInfo() {
		return obj.get();
	}

	public ObjectType getInfoType() {
		return obj.objectType;
	}

	@Override
	public boolean isHeader() {
		return isCategory.getObject();
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof LogicInfo;
	}

	@Override
	public boolean isValid() {
		return this.isCategory.getObject() ? registryType != null : obj.get() != null && obj.objectType != null;
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public LogicInfo copy() {
		return buildDirectInfo(identifier.getObject(), registryType.getObject(), obj.get());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		// InfoRenderer.renderNormalInfo(displayType, width, height, scale, getClientIdentifier(), getClientObject());
		InfoRenderer.renderNormalInfo(container.display.getDisplayType(), width, height, scale, displayInfo.getFormattedStrings());
	}

	@Override
	public String getSuffix() {
		updateAdjustments(false);
		return suffix;
	}

	@Override
	public String getPrefix() {
		updateAdjustments(false);
		return prefix;
	}

	@Override
	public String getRawData() {
		if (identifier.getObject().equals("Block.getUnlocalizedName")) {
			return FontHelper.translate(obj.get().toString() + ".name");
		}
		return obj.get().toString();
	}

	@Override
	public void getComparableObjects(Map<String, Object> objects) {
		objects.put("raw info", obj.get());
		objects.put("object type", obj.objectType);
		objects.put("identifier", identifier.getObject());
	}

}
