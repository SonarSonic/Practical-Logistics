package sonar.logistics.monitoring;

import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;

public class ChannelMonitorHandler extends MonitorHandler<MonitoredBlockCoords> {

	@Override
	public String getName() {
		return MonitorHandler.CHANNEL;
	}

	@Override
	public MonitoredList<MonitoredBlockCoords> updateInfo(MonitoredList<MonitoredBlockCoords> previousList, BlockCoords coords, EnumFacing side) {
		//unused
		return previousList;
	}
	/*
	@Override
	public MonitoredBlockCoords readInfo(NBTTagCompound tag, SyncType type) {
		return new MonitoredBlockCoords(BlockCoords.readFromNBT(tag), tag.getString("name"));
	}

	@Override
	public NBTTagCompound writeInfo(MonitoredBlockCoords info, NBTTagCompound tag, SyncType type) {
		tag.setString("name", info.unlocalizedName);
		return BlockCoords.writeToNBT(tag, info.coords);
	}

	@Override
	public boolean validateInfo(IMonitorInfo info) {
		return info instanceof MonitoredBlockCoords;
	}
	*/

}
