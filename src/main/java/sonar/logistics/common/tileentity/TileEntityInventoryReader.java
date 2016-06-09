package sonar.logistics.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.common.tileentity.TileEntityHandlerInventory;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.inventory.IDropInventory;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.common.handlers.InventoryReaderHandler;

public class TileEntityInventoryReader extends TileEntityHandlerInventory implements IInfoEmitter, IDropInventory, ISidedInventory {

	public InventoryReaderHandler handler = new InventoryReaderHandler(true);;

	@Override
	public InventoryReaderHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return getTileHandler().canConnect(this, dir);
	}

	@Override
	public ILogicInfo currentInfo() {
		return getTileHandler().currentInfo(this);
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public int[] dropSlots() {
		return null;
	}

	@Override
	public boolean canDrop() {
		return false;
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
		LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public void removeConnections() {
		LogisticsAPI.getCableHelper().removeConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int p_94128_1_) {
		return new int[0];
	}

	@Override
	public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_) {
		return false;
	}

	@Override
	public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_) {
		return false;
	}

}
