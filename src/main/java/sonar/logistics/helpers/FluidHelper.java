package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.wrappers.FluidWrapper;

public class FluidHelper extends FluidWrapper {

	public List<StoredFluidStack> getFluids(List<BlockCoords> network) {
		List<StoredFluidStack> fluidList = new ArrayList();
		List<FluidHandler> providers = Logistics.fluidProviders.getObjects();

		for (FluidHandler provider : providers) {
			for (BlockCoords coord : network) {
				TileEntity target = coord.getTileEntity();
				if (target != null && target instanceof IConnectionNode) {
					IConnectionNode node = (IConnectionNode) target;
					Map<BlockCoords, ForgeDirection> connections = node.getConnections();
					for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
						TileEntity fluidTile = entry.getKey().getTileEntity(target.getWorldObj());
						if (provider.canHandleFluids(fluidTile, entry.getValue())) {
							List<StoredFluidStack> info = new ArrayList();
							provider.getFluids(info, fluidTile, entry.getValue());
							for (StoredFluidStack fluid : info) {
								addFluidToList(fluidList, fluid);
							}
						}
					}
				}
			}
		}
		Collections.sort(fluidList, new Comparator<StoredFluidStack>() {
			public int compare(StoredFluidStack str1, StoredFluidStack str2) {
				if (str1.stored < str2.stored)
					return 1;
				if (str1.stored == str2.stored)
					return 0;
				return -1;
			}
		});
		return fluidList;
	}

	public void addFluidToList(List<StoredFluidStack> list, StoredFluidStack stack) {
		int pos = 0;
		for (StoredFluidStack storedTank : list) {
			if (storedTank.equalStack(stack.fluid)) {
				list.get(pos).add(stack);
				return;
			}
			pos++;
		}
		list.add(stack);

	}

	public StoredFluidStack addFluids(StoredFluidStack add, List<BlockCoords> network) {
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (FluidHandler provider : Logistics.fluidProviders.getObjects()) {
				if (provider.canHandleFluids(tile, entry.getValue())) {
					add = provider.addStack(add, tile, entry.getValue());
					if (add == null) {
						return null;
					}
				}
			}
		}
		return add;
	}

	public  StoredFluidStack removeFluids(StoredFluidStack remove, List<BlockCoords> network) {
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (FluidHandler provider : Logistics.fluidProviders.getObjects()) {
				if (provider.canHandleFluids(tile, entry.getValue())) {
					remove = provider.removeStack(remove, tile, entry.getValue());
					if (remove == null) {
						return null;
					}
				}
			}
		}
		return remove;
	}
}
