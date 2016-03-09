package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;

/** used for providing information on Inventories for the Inventory Reader to read, the Provider must be registered in the {@link LogisticsAPI} to be used */
public abstract class InventoryHandler extends LogicProvider {

	public static class StorageSize {

		public static final StorageSize EMPTY = new StorageSize(0, 0);

		private long stored, max;

		public StorageSize(long stored, long max) {
			this.stored = stored;
			this.max = max;
		}

		public long getStoredFluids() {
			return stored;
		}

		public long getMaxFluids() {
			return max;
		}

		public void addItems(long add) {
			stored += add;
		}

		public void addStorage(long add) {
			max += add;
		}
	}

	/** gets the ID of this handler, shouldn't be required outside of this class **/
	public int getID() {
		return LogisticsAPI.getRegistry().getInventorHandlerID(getName());
	}

	/** @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @return can this provider handle items for this side of the TileEntity */
	public abstract boolean canHandleItems(TileEntity tile, ForgeDirection dir);

	/** used by the Inventory Reader, returns the {@link StoredItemStack} in the given slot
	 * @param slot the slot's ID, could be out of the Tile Range, ensure appropriate checks.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @return the relevant {@link StoredItemStack} */
	public abstract StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir);

	/** used for adding an a {@link StoredItemStack} to the Inventory
	 * @param add the {@link StoredItemStack} to add
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @param action should this action be simulated
	 * @return what wasn't added */
	public abstract StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir, ActionType action);

	/** used for removing an a {@link StoredItemStack} from the Inventory
	 * @param remove the {@link StoredItemStack} to remove
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @param action should this action be simulated
	 * @return what wasn't extracted */
	public abstract StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir, ActionType action);

	/** only called if canHandleItems is true
	 * @param storedStacks current list of items for the block from this Helper, providers only add to this and don't remove.
	 * @param tile the {@link TileEntity} to check
	 * @param dir the {@link ForgeDirection} to check from
	 * @return an {@link StorageSize} object, ensure that capacity and stored items have been fully accounted for */
	public abstract StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir);
}
