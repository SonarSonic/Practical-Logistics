package sonar.logistics.api.info;

import com.google.common.collect.Lists;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.FontHelper;
import sonar.core.network.sync.ObjectType;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUnidentifiedObject;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.monitoring.InfoMonitorHandler;
import sonar.logistics.registries.LogicRegistry;
import sonar.logistics.registries.LogicRegistry.RegistryType;

/** default info type, created by the LogicRegistry */
@LogicInfoType(id = LogicInfo.id, modid = Logistics.MODID)
public class LogicInfo extends BaseInfo<LogicInfo> implements INameableInfo<LogicInfo>, IClickableInfo {

	public static final String id = "logic";
	public static final LogicMonitorHandler<LogicInfo> handler = LogicMonitorHandler.instance(InfoMonitorHandler.id);

	private SyncTagType.STRING identifier = new SyncTagType.STRING(1);
	private SyncEnum<RegistryType> registryType = new SyncEnum(RegistryType.values(), 2);
	private SyncUnidentifiedObject obj = new SyncUnidentifiedObject(3);
	private boolean isCategory = false;

	{
		syncParts.addAll(Lists.newArrayList(identifier, registryType, obj));
	}

	public LogicInfo() {
		super();
	}

	public static LogicInfo buildCategoryInfo(RegistryType type) {
		LogicInfo info = new LogicInfo();
		info.registryType.setObject(type);
		info.isCategory = true;
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
		return obj.objectType != null && obj.objectType.equals(info.obj.objectType) && identifier.getObject().equals(info.identifier.getObject()) && registryType.getObject().equals(info.registryType.getObject());
	}

	public RegistryType getRegistryType() {
		return registryType.getObject();
	}

	public String getClientIdentifier() {
		return FontHelper.translate("pl." + identifier);
	}

	public String getClientObject() {
		String prefix = "", suffix = "";
		Pair<String, String> adjustment = LogicRegistry.infoAdjustments.get(identifier.getObject());
		if (identifier.getObject().equals("Block.getUnlocalizedName")) {
			return FontHelper.translate(obj.get().toString() + ".name");
		}
		if (adjustment != null) {
			if (!adjustment.a.isEmpty())
				prefix = adjustment.a + " ";
			if (!adjustment.b.isEmpty())
				suffix = " " + adjustment.b;
		}
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
		return isCategory;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof LogicInfo;
	}

	@Override
	public boolean isValid() {
		return obj.get() != null;
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
	public void renderInfo(DisplayType displayType, double width, double height, double scale, int infoPos) {
		InfoRenderer.renderNormalInfo(displayType, width, height, scale, getClientIdentifier(), getClientObject());
	}

	@Override
	public boolean onClicked(BlockInteractionType type, boolean doubleClick, RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (InfoHelper.canBeClickedStandard(renderInfo, player, hand, stack, hit)) {
			FontHelper.sendMessage(getClientIdentifier() + ": " + obj.objectType + " - " + getClientObject(), player.getEntityWorld(), player);
			return true;
		}
		return false;
	}

}
