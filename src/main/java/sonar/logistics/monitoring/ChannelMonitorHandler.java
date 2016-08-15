package sonar.logistics.monitoring;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.energy.EnergyHandler;
import sonar.core.api.energy.StoredEnergyStack;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.MonitorHelper;

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

}
