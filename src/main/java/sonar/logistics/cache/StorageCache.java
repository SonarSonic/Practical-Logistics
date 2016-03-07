package sonar.logistics.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.FluidHandler;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;

public abstract class StorageCache implements IStorageCache{
	
	public StorageItems getCachedItems() {
		List<StoredItemStack> storedStacks = new ArrayList();
		StorageSize storage = new StorageSize(0, 0);

		Map<BlockCoords, ForgeDirection> blocks = getExternalBlocks(true);
		if (!blocks.isEmpty()) {
			storage = LogisticsAPI.getItemHelper().getTileInventory(storedStacks, storage, blocks);
		} else {
			TileEntity tile = getFirstTileEntity(CacheTypes.ENTITY_NODES);
			if (tile != null && tile instanceof IEntityNode) {
				storage = LogisticsAPI.getItemHelper().getEntityInventory(storedStacks, storage, ((IEntityNode) tile).getEntities());
			}
		}
		return new StorageItems(storedStacks, storage);
	}

	public StorageFluids getCachedFluids() {
		List<StoredFluidStack> fluidList = new ArrayList();
		StorageSize storage = new StorageSize(0, 0);
		List<FluidHandler> providers = Logistics.fluidProviders.getObjects();
		LinkedHashMap<BlockCoords, ForgeDirection> blocks = getExternalBlocks(true);
		for (FluidHandler provider : providers) {
			for (Map.Entry<BlockCoords, ForgeDirection> entry : blocks.entrySet()) {
				TileEntity fluidTile = entry.getKey().getTileEntity();
				if (fluidTile != null && provider.canHandleFluids(fluidTile, entry.getValue())) {
					List<StoredFluidStack> info = new ArrayList();
					StorageSize size = provider.getFluids(info, fluidTile, entry.getValue());
					storage.addItems(size.getStoredFluids());
					storage.addStorage(size.getMaxFluids());
					for (StoredFluidStack fluid : info) {
						LogisticsAPI.getFluidHelper().addFluidToList(fluidList, fluid);
					}
				}
			}
		}
		return new StorageFluids(fluidList, storage);
	}
}
