package sonar.logistics.api.info.monitor;

import java.util.Objects;

import net.minecraft.util.EnumFacing;
import sonar.core.api.IRegistryObject;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.LogisticsASMLoader;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.connections.monitoring.MonitoredList;

public abstract class LogicMonitorHandler<I extends IMonitorInfo> {

	//public static final String INFO = "info", FLUIDS = "fluids", ITEMS = "items", ENERGY = "energy", ENTITY = "entity", CHANNEL = "channel";

	public abstract String id();
	
	/** @param network TODO
	 * @param coords the position to obtain the info from
	 * @return the info found */
	public abstract MonitoredList<I> updateInfo(INetworkCache network, MonitoredList<I> info, BlockCoords coords, EnumFacing side);

	public static LogicMonitorHandler instance(String id){
		return LogisticsASMLoader.monitorHandlers.get(id);
	}
	
	/** @param tag the save to read from
	 * @return the info */
	//public abstract I readInfo(NBTTagCompound tag, SyncType type);

	/** @param info to save
	 * @return tag with the info saved */
	//public abstract NBTTagCompound writeInfo(I info, NBTTagCompound tag, SyncType type);

	/** @param info to check
	 * @return if this info is valid for use with the InfoHandler */
	//public abstract boolean validateInfo(IMonitorInfo info);

	public int hashCode(){
		return Objects.hashCode(id());		
	}
	
}
