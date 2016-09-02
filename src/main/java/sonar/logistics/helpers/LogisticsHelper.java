package sonar.logistics.helpers;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.MonitoredList;

public class LogisticsHelper {

	public static boolean isPlayerUsingOperator(EntityPlayer player) {
		if (player.getHeldItemMainhand() != null) {
			return player.getHeldItemMainhand().getItem() instanceof IOperatorTool;
		}
		return false;
	}

}
