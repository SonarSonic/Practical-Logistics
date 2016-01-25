package sonar.logistics.common.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.ITileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.common.handlers.InfoReaderHandler;

public class TileEntityInfoReader extends TileEntityHandler implements IDataConnection, IInfoReader, ITileHandler {

	public InfoReaderHandler handler = new InfoReaderHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public Info getSecondaryInfo() {
		return handler.getSecondaryInfo(this);
	}

	@Override
	public Info currentInfo() {
		return handler.currentInfo(this);
	}

	public void sendAvailableData(EntityPlayer player) {
		handler.sendAvailableData(this, player);
	}

	public boolean maxRender() {
		return true;
	}
}
