package sonar.logistics.info.providers.tile;

import java.util.Iterator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.AE2Helper;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.providers.TileProvider;
import appeng.api.AEApi;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.IMachineSet;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.StorageChannel;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import appeng.tile.storage.TileDrive;
import cpw.mods.fml.common.Loader;

public class AE2GridProvider extends TileProvider {

	public static String name = "AE2-Grid-Provider";
	public String[] categories = new String[] { "AE2 Energy", "AE2 Channels", "AE2 Storage" };
	public String[] subcategories = new String[] { "Idle Power Usage", "Used Channels", "Is Active", "Is Powered", "Item Types", "Fluid Types", "Cell Count", "Total Cells", "Used Bytes", "Total Bytes", "Stored Item Types", "Total Item Types", "Stored Fluid Types", "Total Fluid Types" };

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideInfo(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity target = world.getTileEntity(x, y, z);
		return target != null && (target instanceof IGridBlock || target instanceof IGridConnection || target instanceof IPowerChannelState || target instanceof IGridProxyable);
	}

	@Override
	public void getTileInfo(List<ILogicInfo> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IGridBlock) {
			IGridBlock grid = (IGridBlock) target;
			infoList.add(new LogicInfo(id, 0, 0, (int) grid.getIdlePowerUsage()).addSuffix("ae/t"));
		}
		if (target instanceof IGridConnection) {
			IGridConnection grid = (IGridConnection) target;
			infoList.add(new LogicInfo(id, 1, 1, grid.getUsedChannels()));
		}
		if (target instanceof IPowerChannelState) {
			IPowerChannelState grid = (IPowerChannelState) target;
			infoList.add(new LogicInfo(id, 1, 2, grid.isActive()));
			infoList.add(new LogicInfo(id, 1, 3, grid.isPowered()));
		}
		if (target instanceof IGridProxyable) {
			IGridProxyable proxy = (IGridProxyable) target;
			IGrid grid;
			try {
				grid = proxy.getProxy().getGrid();
				/*
				 * IStorageGrid storage = grid.getCache(IStorageGrid.class); if (storage != null) { IItemList<IAEItemStack> itemInventory = storage.getItemInventory().getStorageList(); infoList.add(new StandardInfo(id, 2, 4, itemInventory.size()));
				 * 
				 * IItemList<IAEFluidStack> fluidInventory = storage.getFluidInventory().getStorageList(); infoList.add(new StandardInfo(id, 2, 5, fluidInventory.size())); }
				 */
				long usedCells = 0;
				long totalCells = 0;

				long usedBytes = 0;
				long totalBytes = 0;
				long usedTypes = 0;
				long totalTypes = 0;
				long usedTypesF = 0;
				long totalTypesF = 0;

				IMachineSet set = grid.getMachines(TileDrive.class);
				// IMachineSet chest = grid.getMachines(TileChest.class);
				Iterator<IGridNode> drives = set.iterator();
				while (drives.hasNext()) {
					IGridHost node = drives.next().getMachine();
					if (node instanceof TileDrive) {
						TileDrive drive = (TileDrive) node;
						totalCells += drive.getCellCount();
						for (int i = 0; i < drive.getInternalInventory().getSizeInventory(); i++) {
							ItemStack is = drive.getInternalInventory().getStackInSlot(i);
							if (is != null) {
								IMEInventoryHandler itemInventory = AEApi.instance().registries().cell().getCellInventory(is, null, StorageChannel.ITEMS);
								if (itemInventory instanceof ICellInventoryHandler) {
									ICellInventoryHandler handler = (ICellInventoryHandler) itemInventory;
									ICellInventory cellInventory = handler.getCellInv();
									if (cellInventory != null) {

										totalBytes += cellInventory.getTotalBytes();
										usedBytes += cellInventory.getUsedBytes();
										totalTypes += cellInventory.getTotalItemTypes();
										usedTypes += cellInventory.getStoredItemTypes();
									}
								}
								IMEInventoryHandler fluidInventory = AEApi.instance().registries().cell().getCellInventory(is, null, StorageChannel.FLUIDS);
								if (fluidInventory instanceof ICellInventoryHandler) {
									ICellInventoryHandler handler = (ICellInventoryHandler) fluidInventory;
									ICellInventory cellInventory = handler.getCellInv();
									if (cellInventory != null) {

										totalBytes += cellInventory.getTotalBytes();
										usedBytes += cellInventory.getUsedBytes();
										totalTypesF += cellInventory.getTotalItemTypes();
										usedTypesF += cellInventory.getStoredItemTypes();
									}
								}
							}
						}
					}
				}
				infoList.add(new StorageInfo(id, 2, 6, usedCells).addSuffix("cells"));
				infoList.add(new StorageInfo(id, 2, 7, totalCells).addSuffix("cells"));

				infoList.add(new StorageInfo(id, 2, 8, usedBytes).addSuffix("bytes"));
				infoList.add(new StorageInfo(id, 2, 9, totalBytes).addSuffix("bytes"));
				infoList.add(new StorageInfo(id, 2, 10, usedTypes));
				infoList.add(new StorageInfo(id, 2, 11, totalTypes));
				infoList.add(new StorageInfo(id, 2, 12, usedTypesF));
				infoList.add(new StorageInfo(id, 2, 13, totalTypesF));

			} catch (GridAccessException e) {
				e.printStackTrace();
			}

		}
	}

	@Override
	public String getCategory(int id) {
		return categories[id];
	}

	@Override
	public String getSubCategory(int id) {
		return subcategories[id];
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("appliedenergistics2");
	}

	public static class StorageInfo extends LogicInfo {
		public StorageInfo() {

		}

		public StorageInfo(int providerID, int category, int subCategory, Object data) {
			super(providerID, category, subCategory, data);

		}

		public StorageInfo(int providerID, String category, String subCategory, Object data) {
			super(providerID, category, subCategory, data);
		}

		public int updateTicks() {
			return 20;
		}
	}
}
