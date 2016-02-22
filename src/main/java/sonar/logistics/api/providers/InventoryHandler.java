package sonar.logistics.api.providers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.core.utils.IRegistryObject;
import sonar.logistics.api.LogisticsAPI;

/**
 * used for providing information on Inventories for the Inventory Reader to
 * read, the Provider must be registered in the {@link LogisticsAPI} to be used
 */
public abstract class InventoryHandler implements IRegistryObject {

	public static class StorageSize {
		
		public static final StorageSize EMPTY = new StorageSize(0,0);
		
		private long stored,max;

		public StorageSize(long stored, long max) {
			this.stored = stored;
			this.max = max;
		}
		
		public long getStoredFluids(){
			return stored;
		}

		public long getMaxFluids(){
			return max;
		}
		
		public void addItems(long add){
			stored +=add;
		}
		
		public void addStorage(long add){
			max +=add;
		}
	}
	
	public int getID() {
		return LogisticsAPI.getRegistry().getInventorHandlerID(getName());
	}

	/** the name the info helper will be registered too */
	public abstract String getName();

	/**
	 * @param tile Z Coordinate
	 * @param dir The direction of the Node to the Block
	 * @return can this provider give info for the block/tile in the world at x,y,z
	 */
	public abstract boolean canHandleItems(TileEntity tile, ForgeDirection dir);

	public abstract StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir);

	/**returns what wasn't added*/
	public abstract StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir, ActionType action);

	/**returns what wasn't extracted*/
	public abstract StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir, ActionType action);

	/**
	 * only called if canProvideInfo is true
	 * @param storedStacks current list of items for the block from this Helper, providers only add to this and don't remove.
	 * @param tile The World
	 */
	public abstract StorageSize getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir);
	
	/**
	 * used when the provider is loaded normally used to check if relevant mods
	 * are loaded for APIs to work
	 */
	public boolean isLoadable() {
		return true;
	}
}
