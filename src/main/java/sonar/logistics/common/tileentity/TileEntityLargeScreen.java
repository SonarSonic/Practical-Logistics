package sonar.logistics.common.tileentity;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;

public class TileEntityLargeScreen extends TileEntityHandler implements IDataConnection {

	public LargeDisplayScreenHandler handler = new LargeDisplayScreenHandler(false);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public void updateData(ForgeDirection dir) {
		handler.updateData(this, dir);
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo();
	}

	public boolean maxRender() {
		return true;
	}

}