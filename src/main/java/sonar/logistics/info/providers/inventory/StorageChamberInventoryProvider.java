package sonar.logistics.info.providers.inventory;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.common.tileentity.machines.TileEntityStorageChamber;
import sonar.core.inventory.StoredItemStack;
import sonar.logistics.api.providers.InventoryHandler;
import cpw.mods.fml.common.Loader;

public class StorageChamberInventoryProvider extends InventoryHandler {

	public static String name = "Storage Chamber";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean canHandleItems(TileEntity tile, ForgeDirection dir) {
		return tile instanceof TileEntityStorageChamber;
	}

	@Override
	public StoredItemStack getStack(int slot, TileEntity tile, ForgeDirection dir) {
		if (!(slot < 14)) {
			return null;
		}
		if (tile instanceof TileEntityStorageChamber) {
			TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
			if (chamber != null) {
				if (chamber.getSavedStack() != null) {
					ItemStack stack = chamber.getFullStack(slot);
					if (stack != null) {
						return new StoredItemStack(stack);

					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean getItems(List<StoredItemStack> storedStacks, TileEntity tile, ForgeDirection dir) {
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
				}
				return true;
			}
		}
		return false;
	}

	public boolean isLoadable() {
		return Loader.isModLoaded("Calculator");
	}

	@Override
	public StoredItemStack addStack(StoredItemStack add, TileEntity tile, ForgeDirection dir) {
		TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
		if (chamber.getSavedStack() != null) {
			if (chamber.getCircuitType(add.getItemStack()) == chamber.getCircuitType(chamber.getSavedStack())) {
				int stored = chamber.getStored()[add.getItemDamage()];
				if (stored == chamber.maxSize) {
					return add;
				}
				if (stored + add.getStackSize() <= chamber.maxSize) {
					chamber.increaseStored(add.getItemDamage(), (int) add.getStackSize());
					return null;
				} else {
					chamber.setStored(add.getItemDamage(), chamber.maxSize);
					add.stored -= chamber.maxSize - stored;
					return add;
				}
			}
		} else if (chamber.getCircuitType(add.getItemStack()) != null) {

			chamber.setSavedStack(add.getItemStack().copy());

			if (add.getStackSize() <= chamber.maxSize) {
				chamber.stored[add.getItemDamage()] += add.getStackSize();
				return null;
			} else {
				chamber.stored[add.getItemDamage()] = chamber.maxSize;
				add.stored -= chamber.maxSize;
				return add;
			}
		}

		return add;
	}

	@Override
	public StoredItemStack removeStack(StoredItemStack remove, TileEntity tile, ForgeDirection dir) {
		TileEntityStorageChamber chamber = (TileEntityStorageChamber) tile;
		if (chamber.getSavedStack() != null) {
			if (chamber.getCircuitType(remove.getItemStack()) == chamber.getCircuitType(chamber.getSavedStack())) {
				int stored = chamber.stored[remove.getItemDamage()];
				if (stored != 0) {
					if (stored <= remove.getStackSize()) {

						ItemStack stack = chamber.getFullStack(remove.getItemDamage());
						chamber.stored[remove.getItemDamage()] = 0;
						chamber.resetSavedStack(remove.getItemDamage());
						remove.stored-=stack.stackSize;
					} else {

						ItemStack stack = chamber.getSlotStack(remove.getItemDamage(), (int) remove.getStackSize());
						chamber.stored[remove.getItemDamage()] -= remove.getStackSize();
						
						return null;
					}
				}
			}
		}

		return remove;
	}

}
