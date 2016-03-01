package sonar.logistics.common.tileentity;

import java.util.ArrayList;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.common.handlers.InfoCreatorHandler;

public class TileEntityInfoCreator extends TileEntityHandler implements IInfoEmitter, ICableRenderer, ITextField {

	public InfoCreatorHandler handler = new InfoCreatorHandler(false, this);

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
	public void textTyped(String string, int id) {
		handler.textTyped(string, id);

	}

	@Override
	public CableType canRenderConnection(ForgeDirection dir) {
		return handler.canRenderConnection(this, dir);
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
		LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public void getCacheTypes(ArrayList<CacheTypes> types) {
		types.add(CacheTypes.EMITTER);
		
	}
}
