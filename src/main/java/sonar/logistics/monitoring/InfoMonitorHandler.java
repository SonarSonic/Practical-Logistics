package sonar.logistics.monitoring;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.registries.LogicRegistry;

public class InfoMonitorHandler extends MonitorHandler<LogicInfo> {

	@Override
	public boolean isLoadable() {
		return true;
	}

	@Override
	public String getName() {
		return MonitorHandler.INFO;
	}

	@Override
	public MonitoredList<LogicInfo> updateInfo(MonitoredList<LogicInfo> previousList, BlockCoords coords, EnumFacing dir) {
		MonitoredList<LogicInfo> list = MonitoredList.<LogicInfo>newMonitoredList();
		World world =coords.getWorld(); IBlockState state = coords.getBlockState(world); BlockPos pos = coords.getBlockPos(); Block block = coords.getBlock(world); TileEntity tile = coords.getTileEntity(world);
		LogicRegistry.getTileInfo(list.info, world, state, pos, dir, block, tile);		
		for(ICustomTileHandler handler : LogicRegistry.customTileHandlers){
			if(handler.canProvideInfo(world, state, pos, dir, tile, block)){
				handler.addInfo(list.info, world, state, pos, dir, tile, block);
			}
		}		
		return list;
	}

	@Override
	public LogicInfo readInfo(NBTTagCompound tag, SyncType type) {
		return LogicInfo.readFromNBT(tag);
	}

	@Override
	public NBTTagCompound writeInfo(LogicInfo info, NBTTagCompound tag, SyncType type) {
		return info.writeToNBT(tag);
	}

	@Override
	public boolean validateInfo(IMonitorInfo info) {
		return info instanceof LogicInfo;
	}

}
