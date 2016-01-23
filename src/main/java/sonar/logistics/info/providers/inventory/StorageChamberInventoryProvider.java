package sonar.logistics.info.providers.inventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.common.tileentity.machines.TileEntityStorageChamber;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.providers.InventoryProvider;
import cpw.mods.fml.common.Loader;

public class StorageChamberInventoryProvider extends InventoryProvider {

	public static String name = "Storage Chamber";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canProvideItems(World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		return tile != null && tile instanceof TileEntityStorageChamber;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, World world, int x, int y, int z, ForgeDirection dir) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile instanceof TileEntityStorageChamber) {
			TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
			if (chamber != null) {
				if (chamber.getSavedStack() != null) {
					for (int i = 0; i < 14; i++) {
						ItemStack stack = chamber.getFullStack(i);
						if (stack != null) {
							storedStacks.add(new StoredItemStack(stack));
						}
					}

					return true;
				}
			}
		}
		return false;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Calculator");
	}
	
}
