package sonar.logistics.api.info.types;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.BlockInteractionType;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.sync.SyncUUID;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.info.IClickableInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.connections.monitoring.MonitoredFluidStack;
import sonar.logistics.connections.monitoring.MonitoredItemStack;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;

@LogicInfoType(id = LogicInfoList.id, modid = Logistics.MODID)
public class LogicInfoList extends BaseInfo<LogicInfoList> implements INameableInfo<LogicInfoList>, IClickableInfo {

	public static final String id = "logiclist";
	public SyncUUID monitorUUID = new SyncUUID(0);
	public SyncTagType.STRING infoID = new SyncTagType.STRING(1);
	public final SyncTagType.INT networkID = (INT) new SyncTagType.INT(2).setDefault(-1);

	{
		syncParts.addParts(monitorUUID, infoID, networkID);
	}

	public LogicInfoList() {
	}

	public LogicInfoList(UUID monitorUUID, String infoID, int networkID) {
		this.monitorUUID.setObject(monitorUUID);
		this.infoID.setObject(infoID);
		this.networkID.setObject(networkID);
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
		return new LogicInfoList(monitorUUID.getUUID(), infoID.getObject(), networkID.getObject());
	}

	@Override
	public void renderInfo(InfoContainer container, IDisplayInfo displayInfo, double width, double height, double scale, int infoPos) {
		MonitoredList<?> list = Logistics.getClientManager().getMonitoredList(networkID.getObject(), displayInfo.getInfoUUID());
		ILogicMonitor monitor = Logistics.getClientManager().monitors.get(monitorUUID.getUUID());

		if (monitor == null || list == null)
			return;
		if (infoID.getObject().equals(MonitoredItemStack.id)) {
			if (list == null || list.isEmpty()) {
				// new InfoError("NO ITEMS").renderInfo(displayType, width, height, scale, infoPos);
				return;
			}
			int xSlots = (int) Math.ceil(width * 2);
			int ySlots = (int) (Math.round(height * 2));
			int currentSlot = 0;
			double spacing = 22.7;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPushMatrix();
			GL11.glTranslated(-1 + (0.0625 * 1.3), -1 + 0.0625 * 5, 0.00);
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
						GL11.glScaled(0.022, 0.022, 0.01);
						GL11.glTranslated(xLevel * spacing, yLevel * spacing, 0);
						GlStateManager.disableLighting();
						GlStateManager.enableCull();
						GlStateManager.enablePolygonOffset();
						GlStateManager.doPolygonOffset(-1, -1);
						RenderHelper.renderItemIntoGUI(item.item, 0, 0);
						GlStateManager.disablePolygonOffset();
						GlStateManager.translate(0, 0, 1);
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
			for (MonitoredFluidStack stack : (MonitoredList<MonitoredFluidStack>) list.copyInfo()) {
				stack.renderInfo(container, displayInfo, width, height, scale, infoPos);
				break;
			}
		}
	}

	@Override
	public boolean onClicked(BlockInteractionType type, boolean doubleClick, IDisplayInfo renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit, InfoContainer container) {
		if (infoID.getObject().equals(MonitoredItemStack.id)) {
			ScreenMultipart part = (ScreenMultipart) hit.partHit;
			DisplayType displayType = part.getDisplayType();
			BlockPos pos = part.getPos();// handler pos...
			// BlockPos secondPos = pos;
			if (container.getDisplay() instanceof ConnectedDisplayScreen) {
				ConnectedDisplayScreen connected = (ConnectedDisplayScreen) container.getDisplay();
				if (connected.getTopLeftScreen() != null && connected.getTopLeftScreen().getCoords() != null){
					pos = connected.getTopLeftScreen().getCoords().getBlockPos();
				}
			}
			int slot = -1;

			int maxH = (int) Math.ceil(renderInfo.getRenderProperties().getScaling()[0]);
			int minH = 0;
			int maxY = (int) Math.ceil(renderInfo.getRenderProperties().getScaling()[1]);
			int minY = 0;
			int hSlots = (Math.round(maxH - minH) * 2);
			int yPos = (int) ((maxY - (hit.hitVec.yCoord - pos.getY())) * 2) - maxY, hPos = 0;
			switch (part.face) {
			case DOWN:
				switch (part.rotation) {
				case EAST:
					hPos = (int) ((maxH - minH - (hit.hitVec.zCoord - pos.getZ())) * 2);
					yPos = (int) ((maxH - minH - (hit.hitVec.xCoord - pos.getX())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case NORTH:
					hPos = (int) ((maxH - minH - (hit.hitVec.xCoord - pos.getX())) * 2);
					yPos = (int) ((minH + (hit.hitVec.zCoord - pos.getZ())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case SOUTH:
					hPos = (int) ((minH + (hit.hitVec.xCoord - pos.getX())) * 2);
					yPos = (int) ((maxH - minH - (hit.hitVec.zCoord - pos.getZ())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case WEST:
					hPos = (int) ((minH + (hit.hitVec.zCoord - pos.getZ())) * 2);
					yPos = (int) ((minH + (hit.hitVec.xCoord - pos.getX())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				default:
					break;
				}
				break;
			case EAST:
				hPos = (int) ((1 + minH - (hit.hitVec.zCoord - pos.getZ())) * 2);
				FontHelper.sendMessage("" + yPos, player.getEntityWorld(), player);
				slot = ((yPos * hSlots) + hPos);
				break;
			case NORTH:
				hPos = (int) ((maxH - minH - (hit.hitVec.xCoord - pos.getX())) * 2);
				slot = ((yPos * hSlots) + hPos);
				break;
			case SOUTH:
				hPos = (int) ((maxH - minH + (hit.hitVec.xCoord - pos.getX())) * 2);
				slot = ((yPos * hSlots) + hPos) - maxH * 2;
				break;
			case UP:
				switch (part.rotation) {
				case EAST:
					hPos = (int) ((maxH - minH - (hit.hitVec.zCoord - pos.getZ())) * 2);
					yPos = (int) ((minH + (hit.hitVec.xCoord - pos.getX())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case NORTH:
					hPos = (int) ((maxH - minH - (hit.hitVec.xCoord - pos.getX())) * 2);
					yPos = (int) ((maxH - (hit.hitVec.zCoord - pos.getZ())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case SOUTH:
					hPos = (int) ((maxH - minH + (hit.hitVec.xCoord - pos.getX())) * 2);
					yPos = (int) ((minH + (hit.hitVec.zCoord - pos.getZ())) * 2);
					slot = ((yPos * hSlots) + hPos) - maxH * 2;
					break;
				case WEST:
					hPos = (int) ((maxH - minH + (hit.hitVec.zCoord - pos.getZ())) * 2);
					yPos = (int) ((minH - (hit.hitVec.xCoord - pos.getX())) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				default:
					break;
				}

				break;
			case WEST:
				hPos = (int) ((maxH - minH + (hit.hitVec.zCoord - pos.getZ())) * 2);
				slot = ((yPos * hSlots) + hPos) - maxH * 2;
				break;
			default:
				break;
			}

			MonitoredList<?> list = Logistics.getInfoManager(player.getEntityWorld().isRemote).getMonitoredList(networkID.getObject(), renderInfo.getInfoUUID());
			boolean isRemote = player.getEntityWorld().isRemote;
			if (list != null && slot >= 0 && slot < list.size()) {
				if (!player.getEntityWorld().isRemote) {
					MonitoredItemStack itemStack = (MonitoredItemStack) list.get(slot);
					if (itemStack != null) {
						InfoHelper.screenItemStackClicked(itemStack.itemStack.getObject(), networkID.getObject(), type, doubleClick, renderInfo.getRenderProperties(), player, hand, stack, hit);
					}
				}
				return true;
			}
		}
		return false;

	}

	@Override
	public String getClientIdentifier() {
		return "List: " + infoID.getObject().toLowerCase();
	}

	@Override
	public String getClientObject() {
		/* Pair<ILogicMonitor, MonitoredList<?>> monitor = LogicMonitorManager.getMonitorFromServer(monitorUUID.getUUID().hashCode()); return "Size: " + (monitor != null && monitor.b != null ? monitor.b.size() : 0); */
		return "LIST";
	}

	@Override
	public String getClientType() {
		return "list";
	}
}