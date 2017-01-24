package sonar.logistics.common.multiparts;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.helpers.FontHelper;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.types.LogicInfo;
import sonar.logistics.client.gui.GuiInfoReader;
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
	public MonitoredList<LogicInfo> sortMonitoredList(MonitoredList<LogicInfo> updateInfo, int channelID) {
		updateInfo.setInfo(InfoHelper.sortInfoList(updateInfo));
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.SINGLE;
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new ContainerInfoReader(player, this) : null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return id == 0 ? new GuiInfoReader(player, this) : null;
	}

	@Override
	public String getDisplayName() {
		return FontHelper.translate("item.InfoReader.name");
	}

}
