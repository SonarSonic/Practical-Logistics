package sonar.logistics.common.tileentity;

import java.util.ArrayList;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.IInfoTile;
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
	public Info currentInfo() {
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