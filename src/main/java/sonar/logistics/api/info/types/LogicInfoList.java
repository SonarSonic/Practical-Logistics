package sonar.logistics.api.info.types;

import java.util.UUID;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.sync.SyncUUID;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.display.DisplayInfo;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.InfoContainer;
import sonar.logistics.api.display.ScreenInteractionEvent;
import sonar.logistics.api.info.IAdvancedClickableInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.connections.monitoring.MonitoredFluidStack;
import sonar.logistics.connections.monitoring.MonitoredItemStack;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.helpers.InfoRenderer;

@LogicInfoType(id = LogicInfoList.id, modid = Logistics.MODID)
public class LogicInfoList extends BaseInfo<LogicInfoList> implements INameableInfo<LogicInfoList>, IAdvancedClickableInfo {

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
			MonitoredList<MonitoredFluidStack> fluids = (MonitoredList<MonitoredFluidStack>) list.copyInfo();
			double dimension = (14 * 0.0625);
			int xSlots = (int) Math.round(width);
			int ySlots = (int) (Math.round(height));
			int maxSlot = (xSlots * ySlots);

			for (int i = 0; i < Math.min(maxSlot, fluids.size()); i++) {
				MonitoredFluidStack fluid = fluids.get(i);
				GL11.glPushMatrix();
				int xLevel = (int) (i - ((Math.floor((i / xSlots))) * xSlots));
				int yLevel = (int) (Math.floor((i / xSlots)));
				GL11.glTranslated(xLevel, yLevel, 0);

				// fluid.renderInfo(container, displayInfo, dimension, dimension, 0.012, infoPos);

				FluidStack stack = fluid.fluidStack.getObject().fluid;
				if (stack != null) {
					GL11.glPushMatrix();
					GL11.glPushMatrix();
					GlStateManager.disableLighting();
					GL11.glTranslated(-1, -0.0625 * 12, +0.004);
					TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(stack.getFluid().getStill().toString());
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					InfoRenderer.renderProgressBarWithSprite(sprite, dimension, dimension, 0.012, fluid.fluidStack.getObject().stored, fluid.fluidStack.getObject().capacity);
					GlStateManager.enableLighting();
					GL11.glTranslated(0, 0, -0.001);
					GL11.glPopMatrix();
					InfoRenderer.renderNormalInfo(container.display.getDisplayType(), dimension, dimension + 0.0625, 0.012, fluid.getClientIdentifier(), fluid.getClientObject());
					GL11.glPopMatrix();
					GL11.glPopMatrix();
				}
			}
		}
	}

	@Override
	public NBTTagCompound onClientClick(ScreenInteractionEvent event, IDisplayInfo renderInfo, EntityPlayer player, ItemStack stack, InfoContainer container) {
		NBTTagCompound clickTag = new NBTTagCompound();
		if (infoID.getObject().equals(MonitoredItemStack.id) && event.hit != null) {
			ScreenMultipart part = (ScreenMultipart) event.hit.partHit;
			DisplayType displayType = part.getDisplayType();
			double x = part.getPos().getX(), y = part.getPos().getY(), z = part.getPos().getZ();
			Vec3d hitVec = event.hit.hitVec;

			if (container.getDisplay() instanceof ConnectedDisplayScreen) {
				ConnectedDisplayScreen connected = (ConnectedDisplayScreen) container.getDisplay();
				if (connected.getTopLeftScreen() != null && connected.getTopLeftScreen().getCoords() != null) {
					BlockPos leftPos = connected.getTopLeftScreen().getCoords().getBlockPos();
					double[] translation = renderInfo.getRenderProperties().getTranslation();
					switch (part.face) {
					case DOWN:
						break;
					case EAST:
						break;
					case NORTH:
						y = leftPos.getY() - translation[1];
						x = leftPos.getX() - translation[0];
						z = leftPos.getZ();
						break;
					case SOUTH:
						break;
					case UP:
						break;
					case WEST:
						break;
					default:
						break;
					}
				}
			}

			int slot = -1;

			int maxH = (int) Math.ceil(renderInfo.getRenderProperties().getScaling()[0]);
			int minH = 0;
			int maxY = (int) Math.ceil(renderInfo.getRenderProperties().getScaling()[1]);
			int minY = 0;
			int hSlots = (Math.round(maxH - minH) * 2);
			int yPos = (int) ((1 - (event.hit.hitVec.yCoord - y)) * Math.ceil(container.getDisplay().getDisplayType().height *2)), hPos = 0;

			switch (part.face) {
			case DOWN:
				switch (part.rotation) {
				case EAST:
					hPos = (int) ((maxH - minH - (hitVec.zCoord - z)) * 2);
					yPos = (int) ((maxH - minH - (hitVec.xCoord - x)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case NORTH:
					hPos = (int) ((maxH - minH - (hitVec.xCoord - x)) * 2);
					yPos = (int) ((minH + (hitVec.zCoord - z)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case SOUTH:
					hPos = (int) ((minH + (hitVec.xCoord - x)) * 2);
					yPos = (int) ((maxH - minH - (hitVec.zCoord - z)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case WEST:
					hPos = (int) ((minH + (hitVec.zCoord - z)) * 2);
					yPos = (int) ((minH + (hitVec.xCoord - x)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				default:
					break;
				}
				break;
			case EAST:
				hPos = (int) ((1 + minH - (hitVec.zCoord - z)) * 2);
				slot = ((yPos * hSlots) + hPos);
				break;
			case NORTH:
				hPos = (int) ((1 - (hitVec.xCoord - x)) * 2);
				slot = ((yPos * hSlots) + hPos);
				break;
			case SOUTH:
				hPos = (int) ((maxH - minH + (hitVec.xCoord - x)) * 2);
				slot = ((yPos * hSlots) + hPos) - maxH * 2;
				break;
			case UP:
				switch (part.rotation) {
				case EAST:
					hPos = (int) ((maxH - minH - (hitVec.zCoord - z)) * 2);
					yPos = (int) ((minH + (hitVec.xCoord - x)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case NORTH:
					hPos = (int) ((maxH - minH - (hitVec.xCoord - x)) * 2);
					yPos = (int) ((maxH - (hitVec.zCoord - z)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				case SOUTH:
					hPos = (int) ((maxH - minH + (hitVec.xCoord - x)) * 2);
					yPos = (int) ((minH + (hitVec.zCoord - z)) * 2);
					slot = ((yPos * hSlots) + hPos) - maxH * 2;
					break;
				case WEST:
					hPos = (int) ((maxH - minH + (hitVec.zCoord - z)) * 2);
					yPos = (int) ((minH - (hitVec.xCoord - x)) * 2);
					slot = ((yPos * hSlots) + hPos);
					break;
				default:
					break;
				}

				break;
			case WEST:
				hPos = (int) ((maxH - minH + (hitVec.zCoord - z)) * 2);
				slot = ((yPos * hSlots) + hPos) - maxH * 2;
				break;
			default:
				break;
			}

			MonitoredList<?> list = Logistics.getClientManager().getMonitoredList(networkID.getObject(), renderInfo.getInfoUUID());
			if (list != null && slot >= 0 && slot < list.size()) {
				MonitoredItemStack itemStack = (MonitoredItemStack) list.get(slot);
				if (itemStack != null) {
					itemStack.writeData(clickTag, SyncType.SAVE);
				}
				return clickTag;
			}
		}
		return clickTag;
	}

	@Override
	public void onClickEvent(InfoContainer container, IDisplayInfo displayInfo, ScreenInteractionEvent event, NBTTagCompound clickTag) {
		if (infoID.getObject().equals(MonitoredItemStack.id)) {
			MonitoredItemStack clicked = NBTHelper.instanceNBTSyncable(MonitoredItemStack.class, clickTag);
			InfoHelper.screenItemStackClicked(clicked.itemStack.getObject(), networkID.getObject(), event.type, event.doubleClick, displayInfo.getRenderProperties(), event.player, event.hand, event.player.getHeldItem(event.hand), event.hit);
		}
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