package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IDataCable;

public class CablingWrapper {

	public void addConnection(TileEntity connection, ForgeDirection side) {}

	public void removeConnection(TileEntity connection, ForgeDirection side) {}

	public void addCable(IDataCable cable) {}

	public void removeCable(IDataCable cable) {}

	public List<BlockCoords> getConnections(TileEntity tile, ForgeDirection dir) {
		return Collections.EMPTY_LIST;
	}

	public Map<BlockCoords, ForgeDirection> getTileConnections(List<BlockCoords> network) {
		return Collections.EMPTY_MAP;
	}

	public int canRenderConnection(TileEntity te, ForgeDirection dir) {
		return 0;
	}
}
