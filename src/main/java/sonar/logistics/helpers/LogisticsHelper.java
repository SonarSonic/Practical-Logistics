package sonar.logistics.helpers;

import net.minecraft.entity.player.EntityPlayer;
import sonar.logistics.api.connecting.IOperatorTool;

public class LogisticsHelper {

	public static boolean isPlayerUsingOperator(EntityPlayer player) {
		if (player.getHeldItemMainhand() != null) {
			return player.getHeldItemMainhand().getItem() instanceof IOperatorTool;
		}
		return false;
	}

}
