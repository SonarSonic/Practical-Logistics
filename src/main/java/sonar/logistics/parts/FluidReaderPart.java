package sonar.logistics.parts;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.core.utils.Pair;
import sonar.core.utils.SortingDirection;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.readers.FluidReader;
import sonar.logistics.client.gui.GuiFluidReader;
import sonar.logistics.client.gui.GuiInventoryReader;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.FluidHelper;
import sonar.logistics.monitoring.MonitoredFluidStack;
import sonar.logistics.network.SyncMonitoredType;

public class FluidReaderPart extends ReaderMultipart<MonitoredFluidStack> implements IByteBufTile {

	public SyncMonitoredType<MonitoredFluidStack> selected = new SyncMonitoredType<MonitoredFluidStack>(1);
	public SyncEnum<FluidReader.Modes> setting = (SyncEnum) new SyncEnum(FluidReader.Modes.values(), 2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncEnum<SortingDirection> sortingOrder = (SyncEnum) new SyncEnum(SortingDirection.values(), 5).addSyncType(SyncType.SPECIAL);
	public SyncEnum<FluidReader.SortingType> sortingType = (SyncEnum) new SyncEnum(FluidReader.SortingType.values(), 6).addSyncType(SyncType.SPECIAL);
	{
		syncParts.addAll(Lists.newArrayList(setting, targetSlot, posSlot, sortingOrder, sortingType, selected));
	}

	public FluidReaderPart() {
		super(MonitorHandler.FLUIDS);
	}

	public FluidReaderPart(EnumFacing face) {
		super(MonitorHandler.FLUIDS, face);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.fluidReaderPart);
	}

	@Override
	public MonitoredList<MonitoredFluidStack> sortMonitoredList(MonitoredList<MonitoredFluidStack> updateInfo) {
		FluidHelper.sortFluidList(updateInfo, sortingOrder.getObject(), sortingType.getObject());
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.UNLIMITED;
	}

	@Override
	public void setMonitoredInfo(MonitoredList<MonitoredFluidStack> updateInfo) {
		IMonitorInfo info = null;
		switch (setting.getObject()) {
		case FLUID:
			MonitoredFluidStack stack = selected.getMonitoredInfo();
			if (stack != null) {
				MonitoredFluidStack dummyInfo = stack.copy();
				Pair<Boolean, IMonitorInfo> latestInfo = updateInfo.getLatestInfo(dummyInfo);
				info = latestInfo.a ? latestInfo.b : dummyInfo;
			}
			break;
		case POS:
			break;
		case STORAGE:
			break;
		case TANKS:
			break;
		default:
			break;
		}
		if (info != null) {
			InfoUUID id = new InfoUUID(getMonitorUUID().hashCode(), 0);
			IMonitorInfo oldInfo = LogicMonitorCache.info.get(id);
			if (oldInfo == null || !oldInfo.isIdenticalInfo(info)) {
				LogicMonitorCache.changeInfo(id, info);
			}
		}

	}

	@Override
	public Object getServerElement(int id, EntityPlayer player, Object obj, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerFluidReader(this, player);
		}
		return null;
	}

	@Override
	public Object getClientElement(int id, EntityPlayer player, Object obj, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiFluidReader(this, player);
		}
		return null;
	}
}
