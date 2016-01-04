package sonar.logistics.common.tileentity;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityBlockNode extends TileEntityNode implements IDataConnection, ICableRenderer {

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Block Node", new BlockCoords(this));
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite();
	}

	public boolean canRenderConnection(ForgeDirection dir) {
		int meta = this.getBlockMetadata();
		TileEntity tile = SonarHelper.getAdjacentTileEntity(this, dir);
		if(tile!=null && tile instanceof TileEntityBlockNode){
			return false;
		}
		return CableHelper.canRenderConnection(this, dir);

	}

	@Override
	public void updateData(ForgeDirection dir) {
	}

	public void updateEntity() {
		super.updateEntity();
		if (this.worldObj.isRemote) {
			return;
		}
		CableHelper.updateAdjacentCoords(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false);
	}

	public boolean maxRender() {
		return true;
	}
}
