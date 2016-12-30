package sonar.logistics.api.info;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.RenderHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.connections.managers.LogicMonitorManager;
import sonar.logistics.connections.monitoring.MonitoredFluidStack;
import sonar.logistics.connections.monitoring.MonitoredItemStack;
import sonar.logistics.connections.monitoring.MonitoredList;

@LogicInfoType(id = LogicInfoList.id, modid = Logistics.MODID)
public class LogicInfoList extends BaseInfo<LogicInfoList> implements INameableInfo<LogicInfoList>, IClickableInfo {

	public static final String id = "logiclist";
	public SyncUUID monitorUUID = new SyncUUID(0);
	public SyncTagType.STRING infoID = new SyncTagType.STRING(1);

	{
		syncParts.addAll(Lists.newArrayList(monitorUUID, infoID));
	}

	public LogicInfoList() {
	}

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
			Pair<ILogicMonitor, MonitoredList<?>> monitor = LogicMonitorManager.getMonitorFromServer(monitorUUID.getUUID().hashCode());
			if (monitor == null || monitor.b == null)
				return;
			MonitoredList<MonitoredItemStack> list = (MonitoredList<MonitoredItemStack>) monitor.b;
			if (list == null || list.isEmpty()) {
				// new InfoError("NO ITEMS").renderInfo(displayType, width, height, scale, infoPos);
				return;
			}
			int xSlots = (int) Math.round(width) * 2;
			int ySlots = (int) (Math.round(height) * 2);
			int currentSlot = 0;
			double spacing = 22;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPushMatrix();
			GL11.glTranslated(-1 + 0.0625 * 2, -1 + 0.0625 * 6, 0.00);
			GL11.glRotated(180, 0, 1, 0);
			GL11.glScaled(-1, 1, 1);

			GlStateManager.enableDepth();
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
						GlStateManager.translate(0, 0, 0.5);
						GlStateManager.depthMask(false);
						RenderHelper.renderStoredItemStackOverlay(item.item, 0, 0, 0, "" + item.stored, false);
						GlStateManager.depthMask(true);
						GL11.glPopMatrix();
					}
				}
				currentSlot++;
			}
			GlStateManager.enableDepth();
			GL11.glPopMatrix();
		}
		if (infoID.getObject().equals(MonitoredFluidStack.id)) {

		}
	}

	@Override
	public boolean onClicked(BlockInteractionType type, boolean doubleClick, RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (infoID.getObject().equals(MonitoredItemStack.id)) {
			ScreenMultipart part = (ScreenMultipart) hit.partHit;
			DisplayType displayType = part.getDisplayType();

			BlockPos pos = part.getPos();// handler pos...
			BlockPos secondPos = pos;
			int slot = -1;

			int maxH = 1;
			int minH = 0;
			int maxY = 1;
			int minY = 0;
			switch (part.face) {
			case DOWN:
				break;
			case EAST:
				int hSlots = (Math.round(maxH - minH) * 2);
				int yPos = (maxY - (secondPos.getY() - pos.getY())) * 2;
				int hPos = (maxH - (secondPos.getZ() - pos.getZ())) * 2;
				int hSlot = hit.hitVec.zCoord < 0.5 ? hPos + 1 : hPos;
				int ySlot = hit.hitVec.yCoord < 0.5 ? yPos + 1 : yPos;
				slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2);
				break;
			case NORTH:
				hSlots = (Math.round(maxH - minH) * 2);
				yPos = (maxY - (secondPos.getY() - pos.getY())) * 2;
				hPos = (maxH - (secondPos.getX() - pos.getX())) * 2;
				hSlot = hit.hitVec.xCoord < 0.5 ? hPos + 1 : hPos;
				ySlot = hit.hitVec.yCoord < 0.5 ? yPos + 1 : yPos;
				slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2);
				break;
			case SOUTH:
				hSlots = (Math.round(maxH - minH) * 2);
				yPos = (maxY - (secondPos.getY() - pos.getY())) * 2;
				hPos = (maxH - minH + (secondPos.getX() - pos.getX())) * 2;
				hSlot = hit.hitVec.xCoord < 0.5 ? hPos : hPos + 1;
				ySlot = hit.hitVec.yCoord < 0.5 ? yPos + 1 : yPos;
				slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2) - maxH * 2;
				break;
			case UP:
				break;
			case WEST:
				hSlots = (Math.round(maxH - minH) * 2);
				yPos = (maxY - (secondPos.getY() - pos.getY())) * 2;
				hPos = (maxH - minH + (secondPos.getZ() - pos.getZ())) * 2;
				hSlot = hit.hitVec.zCoord < 0.5 ? hPos : hPos + 1;
				ySlot = hit.hitVec.yCoord < 0.5 ? yPos + 1 : yPos;
				slot = ((ySlot * hSlots) + hSlot) + (ySlot * 2) - maxH * 2;
				break;
			default:
				break;

			}

			System.out.println(slot);

		}
		return false;
	}

	@Override
	public String getClientIdentifier() {
		return "List: " + infoID.getObject().toUpperCase();
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