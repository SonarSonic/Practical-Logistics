package sonar.logistics.api.display;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.connections.LogicMonitorCache;

/**default implementation of the Display Info used on displays*/
public class DisplayInfo implements IDisplayInfo {

	public IMonitorInfo cachedInfo = null;
	public SyncNBTAbstract<InfoUUID> uuid = new SyncNBTAbstract<InfoUUID>(InfoUUID.class, 1);
	public SyncNBTAbstract<CustomColour> textColour = new SyncNBTAbstract<CustomColour>(CustomColour.class, 2), backgroundColour = new SyncNBTAbstract<CustomColour>(CustomColour.class, 3);
	public ArrayList<ISyncPart> syncParts = new ArrayList<ISyncPart>();
	{
		syncParts.addAll(Lists.newArrayList(uuid, textColour, backgroundColour));
		textColour.setObject(LogisticsColours.white_text);
		backgroundColour.setObject(LogisticsColours.grey_base);
	}

	public DisplayInfo() {
	}

	public void setUUID(InfoUUID infoUUID) {
		uuid.setObject(infoUUID);
	}

	@Override
	public IMonitorInfo getCachedInfo() {
		InfoUUID id = getInfoUUID();
		if (id == null) {
			return null;
		}
		return LogicMonitorCache.info.get(id);
	}

	@Override
	public CustomColour getTextColour() {
		return textColour.getObject();
	}

	@Override
	public CustomColour getBackgroundColour() {
		return backgroundColour.getObject();
	}

	@Override
	public InfoUUID getInfoUUID() {
		return uuid.getObject();
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		NBTHelper.readSyncParts(nbt.getCompoundTag("display"), type, syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		nbt.setTag("display", NBTHelper.writeSyncParts(new NBTTagCompound(), type, syncParts, type == SyncType.SYNC_OVERRIDE));
		return nbt;
	}

}
