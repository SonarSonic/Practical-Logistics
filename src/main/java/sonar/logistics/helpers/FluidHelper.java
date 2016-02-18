package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
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
		/*
		 * Collections.sort(fluidList, new Comparator<StoredFluidStack>() {
		 * public int compare(StoredFluidStack str1, StoredFluidStack str2) { if
		 * (str1.stored < str2.stored) return 1; if (str1.stored == str2.stored)
		 * return 0; return -1; } });
		 */
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

	public StoredFluidStack addFluids(StoredFluidStack add, List<BlockCoords> network, ActionType action) {
		if (add.stored == 0) {
			return add;
		}
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (FluidHandler provider : Logistics.fluidProviders.getObjects()) {
				if (provider.canHandleFluids(tile, entry.getValue())) {
					add = provider.addStack(add, tile, entry.getValue(), action);
					if (add == null) {
						return null;
					}
				}
			}
		}
		return add;
	}

	public StoredFluidStack removeFluids(StoredFluidStack remove, List<BlockCoords> network, ActionType action) {
		if (remove.stored == 0) {
			return remove;
		}
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (FluidHandler provider : Logistics.fluidProviders.getObjects()) {
				if (provider.canHandleFluids(tile, entry.getValue())) {
					remove = provider.removeStack(remove, tile, entry.getValue(), action);
					if (remove == null) {
						return null;
					}
				}
			}
		}
		return remove;
	}
	/**if simulating your expected to pass copies of both the container and stack to fill with*/
	public ItemStack fillFluidItemStack(ItemStack container, StoredFluidStack fill, List<BlockCoords> network, ActionType action) {
		if (FluidContainerRegistry.isContainer(container)) {
			return fillFluidContainer(container, fill, network, action);
		} else if ((container.getItem() instanceof IFluidContainerItem)) {
			return fillFluidHandler(container, fill, network, action);
		}
		return container;
	}

	/**if simulating your expected to pass copies of both the container and stack to fill with*/
	public ItemStack drainFluidItemStack(ItemStack container, List<BlockCoords> network, ActionType action) {
		if (FluidContainerRegistry.isContainer(container)) {
			return drainFluidContainer(container, network, action);
		} else if ((container.getItem() instanceof IFluidContainerItem)) {
			return drainFluidHandler(container, network, action);
		}
		return container;
	}

	public ItemStack fillFluidContainer(ItemStack container, StoredFluidStack fill, List<BlockCoords> network, ActionType action) {
		FluidStack stack = FluidContainerRegistry.getFluidForFilledItem(container);
		int extractSize = 0;
		if (stack != null && stack.isFluidEqual(fill.fluid)) {
			extractSize = FluidContainerRegistry.getContainerCapacity(container) - stack.amount;
		} else if (stack == null) {
			if (container.getItem() == Items.bucket) {
				extractSize = FluidContainerRegistry.BUCKET_VOLUME;
			} else {
				extractSize = FluidContainerRegistry.getContainerCapacity(container);
			}
		}
		if (extractSize == 0) {
			return container;
		}
		StoredFluidStack remainder = removeFluids(fill.setStackSize(extractSize), network, action);
		FluidStack fillStack = fill.fluid.copy();
		if (remainder == null || remainder.stored == 0) {
			fillStack.amount = extractSize;
		} else {
			fillStack.amount = (int) (extractSize - remainder.stored);
		}

		ItemStack filledStack = FluidContainerRegistry.fillFluidContainer(fillStack, container);

		if (filledStack != null) {
			container = filledStack;
		}
		return container;
	}

	public ItemStack drainFluidContainer(ItemStack container, List<BlockCoords> network, ActionType action) {
		FluidStack stack = FluidContainerRegistry.getFluidForFilledItem(container);
		if (stack != null) {
			StoredFluidStack remainder = addFluids(new StoredFluidStack(stack), network, action);
			if (remainder == null || remainder.stored == 0) {
				container = FluidContainerRegistry.drainFluidContainer(container);
			}
		}
		return container;
	}

	public ItemStack fillFluidHandler(ItemStack handler, StoredFluidStack fill, List<BlockCoords> network, ActionType action) {
		IFluidContainerItem container = (IFluidContainerItem) handler.getItem();
		FluidStack stack = container.getFluid(handler);
		int extractSize = 0;
		if (stack != null && stack.isFluidEqual(fill.fluid)) {
			extractSize = (int) Math.min(fill.stored, container.getCapacity(handler) - stack.amount);
		} else if (stack == null) {
			extractSize = container.fill(handler, fill.getFullStack(), false);
		}
		if (extractSize == 0) {
			return handler;
		}
		StoredFluidStack remainder = LogisticsAPI.getFluidHelper().removeFluids(fill.setStackSize(extractSize), network, action);
		FluidStack fillStack = fill.fluid.copy();
		if (remainder == null || remainder.stored == 0) {
			fillStack.amount = extractSize;
		} else {
			fillStack.amount = (int) (extractSize - remainder.stored);
		}
		container.fill(handler, fillStack, true);
		return handler;

	}

	public ItemStack drainFluidHandler(ItemStack handler, List<BlockCoords> network, ActionType action) {
		IFluidContainerItem container = (IFluidContainerItem) handler.getItem();
		FluidStack stack = container.getFluid(handler);
		if (stack != null) {
			FluidStack insertSize = container.drain(handler, Integer.MAX_VALUE, false);
			StoredFluidStack remainder = addFluids(new StoredFluidStack(insertSize), network, action);
			int drainSize = 0;
			if (remainder == null || remainder.stored == 0) {
				drainSize = insertSize.amount;
			} else {
				drainSize = (int) (insertSize.amount - remainder.stored);
			}
			container.drain(handler, drainSize, true);
		}
		return handler;

	}
}
