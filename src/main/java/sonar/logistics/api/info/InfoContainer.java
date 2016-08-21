package sonar.logistics.api.info;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.display.DisplayInfo;
import sonar.logistics.api.display.IDisplayInfo;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.monitoring.MonitoredItemStack;

public class InfoContainer implements IInfoContainer, INBTSyncable {

	public final ArrayList<SyncNBTAbstract<DisplayInfo>> storedInfo = new ArrayList();
	public final IInfoDisplay display;
	public ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();

	public InfoContainer(IInfoDisplay display) {
		this.display = display;
		for (int i = 0; i < display.maxInfo(); i++) {
			SyncNBTAbstract<DisplayInfo> syncPart = new SyncNBTAbstract<DisplayInfo>(DisplayInfo.class, i);
			syncPart.setObject(new DisplayInfo());
			storedInfo.add(syncPart);
		}
		syncParts.addAll(storedInfo);
	}

	@Override
	public InfoUUID getInfoUUID(int pos) {
		return storedInfo.get(pos).getObject().getInfoUUID();
	}

	@Override
	public void setUUID(InfoUUID id, int pos) {
		storedInfo.get(pos).getObject().setUUID(id);
	}

	@Override
	public void renderContainer() {
		for (int p = 0; p < this.getMaxCapacity(); p++) {
			IDisplayInfo info = storedInfo.get(p).getObject();
			IMonitorInfo cachedInfo = info.getCachedInfo();
			if (cachedInfo != null) {
				if (cachedInfo instanceof INameableInfo) {
					INameableInfo nInfo = (INameableInfo) cachedInfo;
					String[] toDisplay = new String[] { nInfo.getClientIdentifier(), nInfo.getClientObject() };
					GlStateManager.disableLighting();
					GlStateManager.enableCull();
					int width = 1;

					float yCentre = -0.5F - 0.0080F;
					float offset = 0.1F;
					double centre = ((double) (toDisplay.length) / 2) - 0.5;
					for (int i = 0; i < toDisplay.length; i++) {
						LogisticsAPI.getInfoRenderer().renderCenteredString(toDisplay[i], -1, (float) (i == centre ? yCentre : i < centre ? yCentre - offset * -(i - centre) : yCentre + offset * (i - centre)), width, (float) 0.0080, -1);

					}
					GlStateManager.disableCull();
					GlStateManager.enableLighting();
				} else if (cachedInfo instanceof MonitoredItemStack) {
					MonitoredItemStack monitorStack = (MonitoredItemStack) cachedInfo;
					if (monitorStack.itemStack.getObject() != null) {
						StoredItemStack stack = monitorStack.itemStack.getObject();
						ItemStack item = stack.item;
						GL11.glTranslated(-0.665, -0.67, 0.00);
						GL11.glRotated(180, 0, 1, 0);
						GL11.glScaled(-1, 1, 1);
						GL11.glScaled(1.0 / 48, 1.0 / 48, 1.0 / 48);
						GlStateManager.disableLighting();

						GlStateManager.enablePolygonOffset();
						GlStateManager.doPolygonOffset(-1, -1);
						RenderHelper.renderItemIntoGUI(item, 0, 0);						
						GlStateManager.disablePolygonOffset();
						GlStateManager.enableCull();
						RenderHelper.renderStoredItemStackOverlay(item, 0, 0, 0, "" + stack.stored);
					}
				}
			}

		}

	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt.getCompoundTag("parts"), type, syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setTag("parts", NBTHelper.writeSyncParts(new NBTTagCompound(), type, syncParts, type == SyncType.SYNC_OVERRIDE));
		return nbt;
	}

	@Override
	public int getMaxCapacity() {
		return display.maxInfo();
	}

}
