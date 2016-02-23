package sonar.logistics.common.tileentity;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;

public class TileEntityExpulsionPort extends TileEntityChannelledCable {

	public CableType canRenderConnection(ForgeDirection dir) {
		if (dir == ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite()) {
			return this.getCableType().BLOCK_CONNECTION;
		}

		return LogisticsAPI.getCableHelper().canRenderConnection(this, dir, getCableType());

	}
}
