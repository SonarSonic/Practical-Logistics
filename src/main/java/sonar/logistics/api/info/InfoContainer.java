package sonar.logistics.api.info;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncPartsList;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;

public class InfoContainer implements IInfoContainer, INBTSyncable {

	public SyncPartsList syncParts = new SyncPartsList(InfoContainer.class.getSimpleName());

	public final ArrayList<IDisplayInfo> storedInfo;
	public final IInfoDisplay display;

	public InfoContainer(IInfoDisplay display) {
		this.display = display;
		this.storedInfo = new ArrayList(2);
		for (int i = 0; i < storedInfo.size(); i++) {

		}
	}

	public void updateInfo(InfoUUID id, ByteBuf updateBuf) {
		for (IDisplayInfo info : storedInfo) {
			if (info.getInfoUUID().equals(id)) {
				info.updateInfo(updateBuf.copy());
			}
		}
	}

	@Override
	public ArrayList<IDisplayInfo> getStoredInfo() {
		return storedInfo;
	}

	@Override
	public void addInfo(IDisplayInfo info) {
		storedInfo.add(info);
	}

	@Override
	public void removeInfo(IDisplayInfo info) {
		storedInfo.remove(info);
	}

	@Override
	public void renderContainer() {
		for (IDisplayInfo displayFormat : storedInfo) {
			String[] toDisplay = new String[] { "Burn Time", "200 ticks"};
			// NORMAL DATA!!!!
			// INameableInfo info = (INameableInfo)displayFormat.getCachedInfo();
			GlStateManager.disableLighting();
			GlStateManager.enableCull();
			int width = 1;
			
			float yCentre = -0.5F - 0.0080F; // ==-0.5 - font size probably
			float offset = 0.1F;
			double centre = ((double)(toDisplay.length) / 2)-0.5;
			for (int i = 0; i < toDisplay.length; i++) {
				LogisticsAPI.getInfoRenderer().renderCenteredString(toDisplay[i], -1, (float) (i == centre ? yCentre : i < centre ? yCentre - offset*-(i-centre) : yCentre + offset*(i-centre)), width, (float) 0.0080, -1);

			} 
			GlStateManager.disableCull();
			GlStateManager.enableLighting();
			
			// LogisticsAPI.getInfoRenderer().renderCenteredString(info.getClientObject(), -width, -0.45F, width, (float) 0.0080, -1);

		}

	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt, type, syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.writeSyncParts(nbt, type, syncParts, type == SyncType.SYNC_OVERRIDE);
		return nbt;
	}

}
