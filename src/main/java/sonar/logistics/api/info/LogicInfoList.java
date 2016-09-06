package sonar.logistics.api.info;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.RenderHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.monitoring.MonitoredItemStack;

@LogicInfoType(id = LogicInfoList.id, modid = Logistics.MODID)
public class LogicInfoList extends BaseInfo<LogicInfoList> implements INameableInfo<LogicInfoList>, IClickableInfo {

	public static final String id = "logiclist";
	public SyncUUID monitorUUID = new SyncUUID(0);
	public SyncTagType.STRING infoID = new SyncTagType.STRING(1);

	{
		syncParts.addAll(Lists.newArrayList(monitorUUID, infoID));
	}

	public LogicInfoList(){}
	
	public LogicInfoList(UUID monitorUUID, String infoID) {
		this.monitorUUID.setObject(monitorUUID);
		this.infoID.setObject(infoID);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean isIdenticalInfo(LogicInfoList info) {
		return monitorUUID.getUUID().equals(info.monitorUUID.getUUID());
	}

	@Override
	public boolean isMatchingInfo(LogicInfoList info) {
		return infoID.getObject() == info.infoID.getObject();
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof LogicInfoList;
	}

	@Override
	public LogicMonitorHandler<LogicInfoList> getHandler() {
		return null;
	}

	@Override
	public boolean isValid() {
		return monitorUUID.getUUID() != null;
	}

	@Override
	public LogicInfoList copy() {
		return new LogicInfoList(monitorUUID.getUUID(), infoID.getObject());
	}

	@Override
	public void renderInfo(DisplayType displayType, double width, double height, double scale, int infoPos) {
		if (infoID.getObject().equals(MonitoredItemStack.id)) {
			Pair<ILogicMonitor, MonitoredList<?>> monitor = LogicMonitorCache.getMonitorFromServer(monitorUUID.getUUID().hashCode());
			if(monitor==null || monitor.b==null) return;
			MonitoredList<MonitoredItemStack> list = (MonitoredList<MonitoredItemStack>) monitor.b;
			if (list == null || list.isEmpty()) {
				//new InfoError("NO ITEMS").renderInfo(displayType, width, height, scale, infoPos);
				return;
			}
			int xSlots = (int) Math.round(width) * 3;
			int ySlots = (int) (Math.round(height) * 3);
			int currentSlot = 0;
			double spacing = 19;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPushMatrix();
			GL11.glTranslated(-1+0.0625*1, -1+0.0625*5, 0.00);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glScaled(-1, 1, 1);
			
			for (MonitoredItemStack stack : (MonitoredList<MonitoredItemStack>) list.copyInfo()) {
				if (stack.isValid()) {
					if (currentSlot < (xSlots * (ySlots))) {
						StoredItemStack item = stack.itemStack.getObject();
						int xLevel = (int) (currentSlot - ((Math.floor((currentSlot / xSlots))) * xSlots));
						int yLevel = (int) (Math.floor((currentSlot / xSlots)));
						GL11.glPushMatrix();
						GL11.glScaled(scale * 2, scale * 2, scale * 2);
						GL11.glTranslated(xLevel * spacing, yLevel * spacing, 0);
						GlStateManager.disableLighting();
						GlStateManager.enableCull();
						GlStateManager.enablePolygonOffset();
						GlStateManager.doPolygonOffset(-1, -1);
						RenderHelper.renderItemIntoGUI(item.item, 0, 0);
						GlStateManager.disablePolygonOffset();
						GlStateManager.enableDepth();
						RenderHelper.renderStoredItemStackOverlay(item.item, 0, 0, 0, "" + item.stored);
						GL11.glPopMatrix();
					}
				}
				currentSlot++;
			}
		}
		GL11.glPopMatrix();
	}

	@Override
	public boolean onClicked(BlockInteractionType type, boolean doubleClick, RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		return false;
	}

	@Override
	public String getClientIdentifier() {
		return "" + infoID;
	}

	@Override
	public String getClientObject() {
		return monitorUUID.getUUID().toString();
	}

	@Override
	public String getClientType() {
		return "List";
	}
}