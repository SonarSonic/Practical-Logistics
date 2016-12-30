package sonar.logistics.common.multiparts;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.client.gui.GuiChannelSelection;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.client.gui.GuiSelectionList;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.connections.monitoring.InfoMonitorHandler;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;

public class InfoReaderPart extends LogisticsReader<LogicInfo> implements IByteBufTile {

	public InfoReaderPart() {
		super(InfoMonitorHandler.id);
	}

	public InfoReaderPart(EnumFacing face) {
		super(InfoMonitorHandler.id, face);
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
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerInfoReader(player, this);
		}
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiInfoReader(player, this);
		}
		return null;
	}

}
