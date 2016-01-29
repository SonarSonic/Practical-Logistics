package sonar.logistics.info.providers.inventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.providers.InventoryProvider;
import sonar.logistics.helpers.InfoHelper;

import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawer;
import com.jaquadro.minecraft.storagedrawers.api.storage.IDrawerGroup;

import cpw.mods.fml.common.Loader;

public class DrawersInventoryProvider extends InventoryProvider {

	public static String name = "Storage Drawers";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideItems(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null && tile instanceof IDrawerGroup;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof IDrawerGroup) {
			IDrawerGroup drawers = (IDrawerGroup) tile;
			for (int i = 0; i < drawers.getDrawerCount(); i++) {
				if (drawers.getDrawer(i) != null) {
					IDrawer draw = drawers.getDrawer(i);
					ItemStack item = draw.getStoredItemCopy();
					if (item != null)
						InfoHelper.addStackToList(storedStacks, item);
				}
			}
			return true;
		}
		return false;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("StorageDrawers");
	}

}