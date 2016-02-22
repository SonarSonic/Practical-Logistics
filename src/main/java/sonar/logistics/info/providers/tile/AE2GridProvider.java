package sonar.logistics.info.providers.tile;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.TileProvider;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridConnection;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.GridAccessException;
import appeng.me.helpers.IGridProxyable;
import cpw.mods.fml.common.Loader;

public class AE2GridProvider extends TileProvider {

	public static String name = "AE2-Grid-Provider";
	public String[] categories = new String[] { "AE2 Energy", "AE2 Channels", "AE2 Storage" };
	public String[] subcategories = new String[] { "Idle Power Usage", "Used Channels", "Is Active", "Is Powered", "Item Types", "Fluid Types" };

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
	public void getHelperInfo(List<Info> infoList, World world, int x, int y, int z, ForgeDirection dir) {
		int id = this.getID();
		TileEntity target = world.getTileEntity(x, y, z);
		if (target instanceof IGridBlock) {
			IGridBlock grid = (IGridBlock) target;
			infoList.add(new StandardInfo(id, 0, 0, (int) grid.getIdlePowerUsage()).addSuffix("ae/t"));
		}
		if (target instanceof IGridConnection) {
			IGridConnection grid = (IGridConnection) target;
			infoList.add(new StandardInfo(id, 1, 1, grid.getUsedChannels()));
		}
		if (target instanceof IPowerChannelState) {
			IPowerChannelState grid = (IPowerChannelState) target;
			infoList.add(new StandardInfo(id, 1, 2, grid.isActive()));
			infoList.add(new StandardInfo(id, 1, 3, grid.isPowered()));
		}
		if (target instanceof IGridProxyable) {
			IGridProxyable proxy = (IGridProxyable) target;
			IGrid grid;
			try {
				grid = proxy.getProxy().getGrid();
				IStorageGrid storage = grid.getCache(IStorageGrid.class);
				if (storage != null) {
					IItemList<IAEItemStack> itemInventory = storage.getItemInventory().getStorageList();
					infoList.add(new StandardInfo(id, 2, 4, itemInventory.size()));

					IItemList<IAEFluidStack> fluidInventory = storage.getFluidInventory().getStorageList();
					infoList.add(new StandardInfo(id, 2, 5, fluidInventory.size()));
				}
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

}
