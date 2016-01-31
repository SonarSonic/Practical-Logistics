package sonar.logistics.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityBlockNode extends TileEntityConnection implements IInfoEmitter, ICableRenderer {

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite();
	}

	public int canRenderConnection(ForgeDirection dir) {
		int meta = this.getBlockMetadata();
		TileEntity tile = SonarHelper.getAdjacentTileEntity(this, dir);
		if (tile != null && tile instanceof TileEntityBlockNode) {
			return 0;
		}
		return CableHelper.canRenderConnection(this, dir);

	}

	public void updateEntity() {
		super.updateEntity();
		if (this.worldObj.isRemote) {
			return;
		}
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void addConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite()) {
				CableHelper.addConnection(this, dir);
			}
		}
	}

	@Override
	public void removeConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite()) {
				CableHelper.removeConnection(this, dir);
			}
		}
	}

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Node", new BlockCoords(this));
	}
}
