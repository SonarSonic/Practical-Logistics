package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandlerInventory;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class TileEntityInventoryReader extends TileEntityHandlerInventory implements IDataConnection, IByteBufTile {

	public InventoryReaderHandler handler = new InventoryReaderHandler(false);

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
		return handler.currentInfo(this);
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		handler.writePacket(buf, id);
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		handler.readPacket(buf, id);
	}

}
