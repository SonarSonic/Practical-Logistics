package sonar.logistics.api.display;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.core.network.sync.SyncPart;
import sonar.core.network.sync.SyncTagTypeList;
import sonar.core.network.sync.SyncableList;
import sonar.core.utils.CustomColour;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;

/** default implementation of the Display Info used on displays */
public class DisplayInfo extends SyncPart implements IDisplayInfo, ISyncableListener {

	public RenderInfoProperties renderInfo;
	public IMonitorInfo cachedInfo = null;
	public SyncTagTypeList<String> formatList = new SyncTagTypeList(NBT.TAG_STRING, 0);
	public SyncNBTAbstract<InfoUUID> uuid = new SyncNBTAbstract<InfoUUID>(InfoUUID.class, 1);
	public SyncNBTAbstract<CustomColour> textColour = new SyncNBTAbstract<CustomColour>(CustomColour.class, 2), backgroundColour = new SyncNBTAbstract<CustomColour>(CustomColour.class, 3);
	public InfoContainer container;
	public SyncableList syncParts;
	{
		textColour.setObject(LogisticsColours.white_text);
		backgroundColour.setObject(LogisticsColours.grey_base);
	}

	public DisplayInfo(InfoContainer container, int id) {
		super(id);
		syncParts = new SyncableList(this);
		syncParts.addParts(formatList, uuid, textColour, backgroundColour);
		this.container = container;

	}

	public RenderInfoProperties setRenderInfoProperties(RenderInfoProperties renderInfo) {
		this.renderInfo = renderInfo;
		return renderInfo;
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
		return Logistics.getClientManager().info.get(id);
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
		NBTTagCompound tag = nbt.getCompoundTag(this.getTagName());
		if (!tag.hasNoTags())
			NBTHelper.readSyncParts(tag, type, syncParts);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = NBTHelper.writeSyncParts(new NBTTagCompound(), type, syncParts, type == SyncType.SYNC_OVERRIDE);
		if (!tag.hasNoTags())
			nbt.setTag(this.getTagName(), tag);
		return nbt;
	}

	@Override
	public RenderInfoProperties getRenderProperties() {
		return renderInfo;
	}

	@Override
	public void setFormatStrings(ArrayList<String> strings) {
		formatList.setObjects(strings);
	}

	@Override
	public ArrayList<String> getUnformattedStrings() {
		return formatList.getObjects();
	}

	@Override
	public ArrayList<String> getFormattedStrings() {
		ArrayList<String> format = new ArrayList();
		boolean empty = true;
		for (String string : formatList.getObjects()) {
			if (!string.isEmpty()) {
				empty = false;
				format.add(DisplayConstants.formatText(string, this));
			}
		}

		if (empty) {
			if (getCachedInfo() != null && getCachedInfo() instanceof INameableInfo) {
				INameableInfo cachedInfo = (INameableInfo) getCachedInfo();
				format.add(cachedInfo.getClientIdentifier());
				format.add(cachedInfo.getClientObject());
			}
		}
		return format;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		syncParts.markSyncPartChanged(part);
		container.markChanged(this);

	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
	}

}
