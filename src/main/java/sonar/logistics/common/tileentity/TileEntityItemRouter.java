package sonar.logistics.common.tileentity;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandlerInventory;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.IFilteredInventory;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IInfoTile;
import sonar.logistics.common.handlers.ItemRouterHandler;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityItemRouter extends TileEntityHandlerInventory implements IInfoTile, ISidedInventory, IFilteredInventory {
	
	public ItemRouterHandler handler = new ItemRouterHandler(false, this);

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
		return BlockCoordsInfo.createInfo("Item Router", new BlockCoords(this));
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return handler.getAccessibleSlotsFromSide(side);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) {
		return handler.canInsertItem(slot, stack, side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) {
		return handler.canExtractItem(slot, stack, side);
	}

	@Override
	public boolean canPushItem(ItemStack item, int side) {
		return handler.matchesFilters(item, handler.whitelist[side], handler.blacklist[side]);
	}

	@Override
	public boolean canPullItem(ItemStack item, int side) {
		return handler.matchesFilters(item, handler.whitelist[side], handler.blacklist[side]);
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}


}
