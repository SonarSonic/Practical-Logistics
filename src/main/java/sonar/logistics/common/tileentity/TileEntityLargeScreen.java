package sonar.logistics.common.tileentity;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IInfoTile;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.helpers.DisplayHelper;

public class TileEntityLargeScreen extends TileEntityHandler implements IInfoTile, ILargeDisplay {

	public LargeDisplayScreenHandler handler = new LargeDisplayScreenHandler(false, this);

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

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote)
			DisplayHelper.addScreen(this);
	}

	public void invalidate() {
		if (!this.worldObj.isRemote)
			DisplayHelper.removeScreen(this);
		super.invalidate();
	}

	@Override
	public int registryID() {
		return handler.registryID;
	}

	@Override
	public void setRegistryID(int id) {
		handler.registryID = id;
	}

	@Override
	public int getOrientation() {
		return this.getBlockMetadata();
	}
}