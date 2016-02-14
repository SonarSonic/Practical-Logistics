package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.BlockCoords;

public class FluidWrapper {

	public List<StoredFluidStack> getFluids(List<BlockCoords> coords) {
		return Collections.EMPTY_LIST;
	}

	public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {}

	public StoredFluidStack addItems(StoredFluidStack add, List<BlockCoords> network) {
		return add;
	}
	public StoredFluidStack extractItems(StoredFluidStack remove, List<BlockCoords> network) {
		return remove;
	}
}
