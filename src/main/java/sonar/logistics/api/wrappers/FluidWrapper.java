package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.ActionType;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;

public class FluidWrapper {

	public static class StorageFluids {
		
		public static final StorageFluids EMPTY = new StorageFluids(Collections.EMPTY_LIST, StorageSize.EMPTY);
		public List<StoredFluidStack> fluids;
		public StorageSize sizing;
		
		public StorageFluids(List<StoredFluidStack> items, StorageSize sizing) {
			this.fluids=items;
			this.sizing=sizing;
		}
	}
	/**used for getting the full list of Fluids on a given network
	 * @param network current coordinates of the network
	 * @return list of {@link StoredFluidStack} on the network
	 */
	public StorageFluids getFluids(List<BlockCoords> network) {
		return StorageFluids.EMPTY;
	}
	/**convenient method, adds the given stack to the list, used by {@link FluidHandler}
	 * @param list {@link StoredFluidStack} list to add to
	 * @param stack {@link StoredFluidStack} to combine
	 */
	public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {}

	/**used for adding Fluids to the network
	 * @param add {@link StoredFluidStack} to add
	 * @param network current coordinates of the network
	 * @return remaining {@link StoredFluidStack} (what wasn't added), can be null
	 */
	public StoredFluidStack addFluids(StoredFluidStack add, List<BlockCoords> network, ActionType action) {
		return add;
	}
	/**used for removing Fluids from the network
	 * @param remove {@link StoredFluidStack} to remove
	 * @param network current coordinates of the network
	 * @return remaining {@link StoredFluidStack} (what wasn't removed), can be null
	 */
	public StoredFluidStack removeFluids(StoredFluidStack remove, List<BlockCoords> network, ActionType action) {
		return remove;
	}

	public ItemStack fillFluidItemStack(ItemStack container, StoredFluidStack fill, List<BlockCoords> network, ActionType action){
		return container;
	}
	
	public ItemStack drainFluidItemStack(ItemStack container, List<BlockCoords> network, ActionType action) {
		return container;
	}
}
