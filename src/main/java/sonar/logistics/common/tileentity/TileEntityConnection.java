package sonar.logistics.common.tileentity;

import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IInfoEmitter;

public abstract class TileEntityConnection extends TileEntitySonar implements IInfoEmitter {

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote) {
			addConnections();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			removeConnections();
		}
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public abstract void addConnections();

	@Override
	public abstract void removeConnections();
}
