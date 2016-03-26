package sonar.logistics.common.tileentity;

import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandlerInventory;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IConnectionArray;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.common.handlers.ArrayHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArray extends TileEntityHandlerInventory implements IConnectionArray, IDataCable {

	public int registryID = -1;

	public ArrayHandler handler = new ArrayHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != ForgeDirection.UP;
	}

	@Override
	public ItemStack[] getTransceivers() {
		return handler.slots;
	}

	@Override
	public void addConnections(Map<BlockCoords, ForgeDirection> connections) {
		connections.putAll(handler.coordList);
	}

	@Override
	public boolean isBlocked(ForgeDirection dir) {
		return dir == ForgeDirection.UP;
	}

	public CableType canRenderConnection(ForgeDirection dir) {
		if (dir == ForgeDirection.UP) {
			return CableType.NONE;
		}
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
		handler.updateCoordsList();
		// CacheRegistry.refreshCache(registryID);
	}

	public void invalidate() {
		if (!this.worldObj.isRemote) {
			removeCable();
		}
		super.invalidate();
	}

	public void addCable() {
		LogisticsAPI.getCableHelper().addCable(this);
		LogisticsAPI.getCableHelper().addConnection(registryID, this.getCoords());
	}

	public void removeCable() {
		LogisticsAPI.getCableHelper().removeCable(this);
		LogisticsAPI.getCableHelper().removeConnection(registryID, this.getCoords());
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
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
		return CableType.BLOCK_CONNECTION;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		super.setInventorySlotContents(i, itemstack);
		this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		handler.updateCoordsList();
	}

	@Override
	public void refreshConnections() {
		LogisticsAPI.getCableHelper().refreshConnections(this);
	}

}
