package sonar.logistics.api.info;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.helpers.InfoRenderer;

@LogicInfoType(id = InfoError.id, modid = Logistics.MODID)
public class InfoError implements IMonitorInfo<InfoError> {

	public static final InfoError noData = new InfoError("NO DATA");
	public static final InfoError noMonitor = new InfoError("NO MONITOR");
	
	public static final String id = "error";
	public String error;

	public InfoError(){}
	
	public InfoError(String error) {
		this.error = FontHelper.translate(error);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public boolean isIdenticalInfo(InfoError info) {
		return info.error.equals(error);
	}

	@Override
	public boolean isMatchingInfo(InfoError info) {
		return true;
	}

	@Override
	public boolean isMatchingType(IMonitorInfo info) {
		return info instanceof InfoError;
	}

	@Override
	public boolean isHeader() {
		return false;
	}

	@Override
	public LogicMonitorHandler<InfoError> getHandler() {
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public InfoError copy() {
		return new InfoError(error);
	}

	@Override
	public void renderInfo(DisplayType displayType, double width, double height, double scale, int infoPos) {
		InfoRenderer.renderNormalInfo(displayType, width, height, scale, error);
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		return nbt;
	}

}
