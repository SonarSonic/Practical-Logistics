package sonar.logistics.parts;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.client.gui.GuiNewSelectionList;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.InfoHelper;

public class InfoReaderPart extends LogicReaderPart<LogicInfo> implements IByteBufTile {

	public InfoReaderPart() {
		super(MonitorHandler.INFO);
	}

	public InfoReaderPart(EnumFacing face) {
		super(MonitorHandler.INFO, face);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.infoReaderPart);
	}

	@Override
	public MonitoredList<LogicInfo> sortMonitoredList(MonitoredList<LogicInfo> updateInfo) {
		updateInfo.setInfo(InfoHelper.sortInfoList(updateInfo));
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.SINGLE;
	}

	@Override
	public Object getServerElement(int id, EntityPlayer player, Object obj, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerInfoReader(player, this);
		}
		return null;
	}

	@Override
	public Object getClientElement(int id, EntityPlayer player, Object obj, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiInfoReader(player, this);
		}
		return null;
	}

}
