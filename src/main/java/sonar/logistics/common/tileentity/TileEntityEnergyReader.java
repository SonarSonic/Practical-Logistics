package sonar.logistics.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.common.handlers.EnergyReaderHandler;
import sonar.logistics.common.handlers.InfoReaderHandler;
import sonar.logistics.helpers.CableHelper;

public class TileEntityEnergyReader extends TileEntityHandler implements IInfoEmitter, ITileHandler {

	public EnergyReaderHandler handler = new EnergyReaderHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}
	@Override
	public Info currentInfo() {
		return handler.currentInfo(this);
	}

	public void sendAvailableData(EntityPlayer player) {
		//handler.sendAvailableData(this, player);
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
		CableHelper.addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public void removeConnections() {
		CableHelper.removeConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}
}
