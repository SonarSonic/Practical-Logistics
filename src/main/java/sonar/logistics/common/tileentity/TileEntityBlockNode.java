package sonar.logistics.common.tileentity;

import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityBlockNode extends TileEntityChannelledCable implements IInfoEmitter, ICableRenderer, IConnectionNode {

	@Override
	public CableType getCableType() {
		return CableType.BLOCK_CONNECTION;
	}

	@Override
	public boolean isBlocked(ForgeDirection dir) {
		return !canConnect(dir);
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.getOrientation(SonarHelper.invertMetadata(this.getBlockMetadata())).getOpposite();
	}

	public CableType canRenderConnection(ForgeDirection dir) {
		return LogisticsAPI.getCableHelper().canRenderConnection(this, dir, CableType.BLOCK_CONNECTION);
	}

	public void addCable() {
		super.addCable();
		LogisticsAPI.getCableHelper().addConnection(registryID, this.getCoords());
	}

	public void removeCable() {
		super.removeCable();
		LogisticsAPI.getCableHelper().removeConnection(registryID, this.getCoords());
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
		map.put(tileCoords, dir);
		return map;
	}

	@Override
	public void addConnections() {
	}

	@Override
	public void removeConnections() {
	}
}
