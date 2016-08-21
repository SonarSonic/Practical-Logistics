package sonar.logistics.api.info.monitor;

import net.minecraft.util.EnumFacing;
import sonar.core.api.IRegistryObject;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.connections.MonitoredList;

public abstract class MonitorHandler<I extends IMonitorInfo> implements IRegistryObject {

	public static final String INFO = "info", FLUIDS = "fluids", ITEMS = "items", ENERGY = "energy", ENTITY = "entity", CHANNEL = "channel";

	@Override
	public boolean isLoadable() {
		return true;
	}

	/** @param coords the position to obtain the info from
	 * @return the info found */
	public abstract MonitoredList<I> updateInfo(MonitoredList<I> info, BlockCoords coords, EnumFacing side);

	/** @param tag the save to read from
	 * @return the info */
	//public abstract I readInfo(NBTTagCompound tag, SyncType type);

	/** @param info to save
	 * @return tag with the info saved */
	//public abstract NBTTagCompound writeInfo(I info, NBTTagCompound tag, SyncType type);

	/** @param info to check
	 * @return if this info is valid for use with the InfoHandler */
	//public abstract boolean validateInfo(IMonitorInfo info);

}
