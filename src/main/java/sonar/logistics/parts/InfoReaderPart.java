package sonar.logistics.parts;

import java.util.ArrayList;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.SyncMonitoredType;

public class InfoReaderPart extends ReaderMultipart<LogicInfo> implements IByteBufTile, IGuiTile {

	public SyncMonitoredType<LogicInfo> selected = new SyncMonitoredType<LogicInfo>(MonitorHandler.INFO, 1);

	public InfoReaderPart() {
		super(MonitorHandler.INFO);
		syncParts.addAll(Lists.newArrayList(selected));
	}

	public InfoReaderPart(EnumFacing face) {
		super(MonitorHandler.INFO, face);
		syncParts.addAll(Lists.newArrayList(selected));
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.infoReaderPart);
	}

	@Override
	public Object getGuiContainer(EntityPlayer player) {
		return new ContainerInfoReader(player, this);
	}

	@Override
	public Object getGuiScreen(EntityPlayer player) {
		return new GuiInfoReader(player, this);
	}

	@Override
	public MonitoredList<LogicInfo> updateInfo(MonitoredList<LogicInfo> updateInfo) {
		updateInfo.info = InfoHelper.sortInfoList(updateInfo.info);
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.SINGLE;
	}

	@Override
	public ArrayList<IMonitorInfo> getSelectedInfo() {
		return Lists.newArrayList(selected.getMonitoredInfo());
	}

	@Override
	public void addInfo(LogicInfo info) {
		if (selected.getMonitoredInfo() != null) {
			if (selected.getMonitoredInfo().isMatchingType(info) && selected.getMonitoredInfo().isMatchingInfo(info)) {
				selected.setInfo(null);
				return;
			}
		}
		selected.setInfo(info);
	}

}
