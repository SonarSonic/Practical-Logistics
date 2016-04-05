package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IDataCable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityChannelledCable extends TileEntitySonar implements IDataCable {

	public int registryID = -1;

	@Override
	public boolean isBlocked(ForgeDirection dir) {
		return false;
	}

	public CableType canRenderConnection(ForgeDirection dir) {
		return LogisticsAPI.getCableHelper().canRenderConnection(this, dir, getCableType());
	}

	public boolean maxRender() {
		return true;
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote) {
			addCable();
		}
	}

	public void invalidate() {
		if (!this.worldObj.isRemote) {
			removeCable();
		}
		super.invalidate();
	}

	public void addCable() {
		LogisticsAPI.getCableHelper().addCable(this);
	}

	public void removeCable() {
		LogisticsAPI.getCableHelper().removeCable(this);
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public int registryID() {
		return registryID;
	}

	@Override
	public void setRegistryID(int id) {
		this.registryID = id;
	}

	@Override
	public CableType getCableType() {
		return CableType.CHANNELLED_CABLE;
	}

	@Override
	public void refreshConnections() {
		LogisticsAPI.getCableHelper().refreshConnections(this);
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return true;
	}

}
