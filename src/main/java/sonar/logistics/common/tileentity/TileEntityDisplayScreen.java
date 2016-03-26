package sonar.logistics.common.tileentity;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.connecting.IInfoTile;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.common.handlers.DisplayScreenHandler;

public class TileEntityDisplayScreen extends TileEntityHandler implements IInfoTile {

	public DisplayScreenHandler handler = new DisplayScreenHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public ILogicInfo currentInfo() {
		return handler.currentInfo();
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}
}