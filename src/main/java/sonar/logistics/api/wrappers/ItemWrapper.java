package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;

public class ItemWrapper {

	public List<StoredItemStack> getStackList(List<BlockCoords> connections) {
		return Collections.EMPTY_LIST;
	}

	public List<StoredItemStack> getTileInventory(List<StoredItemStack> storedStacks, IConnectionNode node) {
		return Collections.EMPTY_LIST;
	}

	public List<StoredItemStack> getEntityInventory(List<StoredItemStack> storedStacks, IEntityNode tileNode) {
		return Collections.EMPTY_LIST;
	}

	public void addInventoryToList(List<StoredItemStack> list, IInventory inv) {}	

	public void addStackToList(List<StoredItemStack> list, ItemStack stack) {}

	public StoredItemStack addItems(StoredItemStack add, List<BlockCoords> network) {
		return add;
	}

	public StoredItemStack removeItems(StoredItemStack remove, List<BlockCoords> network) {
		return remove;
	}
}
