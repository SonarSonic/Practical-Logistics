package sonar.logistics.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.common.handlers.EnergyReaderHandler;

public class TileEntityEnergyReader extends TileEntityHandler implements IInfoEmitter, ITileHandler {

	public EnergyReaderHandler handler;

	@Override
	public EnergyReaderHandler getTileHandler() {
		if (handler == null) {
			handler = new EnergyReaderHandler(false, this);
		}
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return getTileHandler().canConnect(this, dir);
	}

	@Override
	public ILogicInfo currentInfo() {
		return getTileHandler().currentInfo(this);
	}

	public void sendAvailableData(EntityPlayer player) {
		// handler.sendAvailableData(this, player);
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public void addConnections() {
		LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public void removeConnections() {
		LogisticsAPI.getCableHelper().removeConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}
}
