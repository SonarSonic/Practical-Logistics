package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;

/** used for providing information on Fluids stored in TileEntities for the Fluid Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class FluidHandler extends LogicProvider {

	public int getID() {
		return LogisticsAPI.getRegistry().getFluidHandlerID(getName());
	}

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @return can this provider handle fluids for this side of the TileEntity */
	public abstract boolean canHandleFluids(TileEntity tile, ForgeDirection dir);

	/** used for adding an a {@link StoredFluidStack} to the Fluid Inventory
	 * @param add the {@link StoredFluidStack} to add
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract StoredFluidStack addStack(StoredFluidStack add, TileEntity tile, ForgeDirection dir, ActionType action);

	/** used for removing an a {@link StoredFluidStack} from the Fluid Inventory
	 * @param remove the {@link StoredFluidStack} to remove
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract StoredFluidStack removeStack(StoredFluidStack remove, TileEntity tile, ForgeDirection dir, ActionType action);

	/** only called if canHandleFluids is true
	 * @param storedStacks current list of fluids for the block from this Helper, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @return an {@link StorageSize} object, ensure that capacity and stored fluids have been fully accounted for */
	public abstract StorageSize getFluids(List<StoredFluidStack> fluids, TileEntity tile, ForgeDirection dir);

}
