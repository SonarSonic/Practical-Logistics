package sonar.logistics.common.tileentity;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityBlockNode extends TileEntityConnection implements IInfoEmitter, ICableRenderer, IConnectionNode {

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
		return LogisticsAPI.getCableHelper().canRenderConnection(this, dir);

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
				LogisticsAPI.getCableHelper().addConnection(this, dir);
			}
		}
	}

	@Override
	public void removeConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite()) {
				LogisticsAPI.getCableHelper().removeConnection(this, dir);
			}
		}
	}

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Node", new BlockCoords(this));
	}

	@Override
	public Map<BlockCoords, ForgeDirection> getConnections() {
		Map<BlockCoords, ForgeDirection> map = new LinkedHashMap();
		ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(getBlockMetadata())).getOpposite();
		BlockCoords tileCoords = new BlockCoords(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ, worldObj.provider.dimensionId);
		//BlockCoords tileCoords2 = new BlockCoords(xCoord + dir.offsetX*2, yCoord + dir.offsetY, zCoord + dir.offsetZ*2, worldObj.provider.dimensionId);
		map.put(tileCoords, dir);
		//map.put(tileCoords2, dir);
		return map;
	}
}
