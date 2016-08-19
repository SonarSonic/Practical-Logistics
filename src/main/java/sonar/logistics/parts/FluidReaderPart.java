package sonar.logistics.parts;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.core.utils.SortingDirection;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.readers.FluidReader;
import sonar.logistics.client.gui.GuiFluidReader;
import sonar.logistics.common.containers.ContainerFluidReader;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.FluidHelper;
import sonar.logistics.monitoring.MonitoredFluidStack;
import sonar.logistics.network.SyncMonitoredType;

public class FluidReaderPart extends ReaderMultipart<MonitoredFluidStack> implements IByteBufTile, IGuiTile {

	public SyncMonitoredType<LogicInfo> selected = new SyncMonitoredType<LogicInfo>(MonitorHandler.FLUIDS, 1);
	public SyncEnum<FluidReader.Modes> setting = (SyncEnum) new SyncEnum(FluidReader.Modes.values(), 2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncEnum<SortingDirection> sortingOrder = (SyncEnum) new SyncEnum(SortingDirection.values(), 5).addSyncType(SyncType.SPECIAL);
	public SyncEnum<FluidReader.SortingType> sortingType = (SyncEnum) new SyncEnum(FluidReader.SortingType.values(), 6).addSyncType(SyncType.SPECIAL);

	public FluidReaderPart() {
		super(MonitorHandler.FLUIDS);
		syncParts.addAll(Lists.newArrayList(setting, targetSlot, posSlot, sortingOrder, sortingType));
	}

	public FluidReaderPart(EnumFacing face) {
		super(MonitorHandler.FLUIDS, face);
		syncParts.addAll(Lists.newArrayList(setting, targetSlot, posSlot, sortingOrder, sortingType));
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.fluidReaderPart);
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerFluidReader(this, player);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiFluidReader(this, player);
	}

	@Override
	public MonitoredList<MonitoredFluidStack> updateInfo(MonitoredList<MonitoredFluidStack> updateInfo) {
		FluidHelper.sortFluidList(updateInfo.info, sortingOrder.getObject(), sortingType.getObject());
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.UNLIMITED;
	}

	@Override
	public ArrayList<IMonitorInfo> getSelectedInfo() {
		return Lists.newArrayList(selected.getMonitoredInfo());
	}

}
