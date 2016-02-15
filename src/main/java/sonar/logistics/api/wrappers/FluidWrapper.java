package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import sonar.core.fluid.StoredFluidStack;
import sonar.logistics.api.providers.FluidHandler;
import sonar.core.utils.BlockCoords;

public class FluidWrapper {

	/**used for getting the full list of Fluids on a given network
	 * @param network current coordinates of the network
	 * @return list of {@link StoredFluidStack} on the network
	 */
	public List<StoredFluidStack> getFluids(List<BlockCoords> network) {
		return Collections.EMPTY_LIST;
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
	public StoredFluidStack addFluids(StoredFluidStack add, List<BlockCoords> network) {
		return add;
	}
	/**used for removing Fluids from the network
	 * @param remove {@link StoredFluidStack} to remove
	 * @param network current coordinates of the network
	 * @return remaining {@link StoredFluidStack} (what wasn't removed), can be null
	 */
	public StoredFluidStack removeFluids(StoredFluidStack remove, List<BlockCoords> network) {
		return remove;
	}
}
