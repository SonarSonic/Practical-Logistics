package sonar.logistics.common.tileentity;

import java.util.ArrayList;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandlerInventory;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.IDropInventory;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class TileEntityInventoryReader extends TileEntityHandlerInventory implements IInfoEmitter, IDropInventory {

	public InventoryReaderHandler handler = new InventoryReaderHandler(false, this);

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

	public boolean maxRender() {
		return true;
	}

	@Override
	public int[] dropSlots() {
		return null;
	}

	@Override
	public boolean canDrop() {
		return false;
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
