package sonar.logistics.common.tileentity;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.ITextField;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.common.handlers.DataModifierHandler;

public class TileEntityDataModifier extends TileEntityHandler implements IInfoEmitter, ICableRenderer, ITextField {

	public DataModifierHandler handler = new DataModifierHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(dir);
	}

	@Override
	public ILogicInfo currentInfo() {
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
		return handler.canRenderConnection(dir, this);
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote) {
			addConnections();
		}
	}

	public void invalidate() {
		if (!this.worldObj.isRemote) {
			removeConnections();
		}
		super.invalidate();
	}

	@Override
	public void addConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != ForgeDirection.getOrientation(FMPHelper.getMeta(this)).getOpposite()) {
				LogisticsAPI.getCableHelper().addConnection(this, dir);
			}
		}
	}

	@Override
	public void removeConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != ForgeDirection.getOrientation(FMPHelper.getMeta(this)).getOpposite()) {
				LogisticsAPI.getCableHelper().removeConnection(this, dir);
			}
		}
	}
}
