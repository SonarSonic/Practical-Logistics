package sonar.logistics.api.wrappers;

import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.InventoryHandler;

public class ItemWrapper {

	/**used for getting the full list of Items on a given network
	 * @param network current coordinates of the network
	 * @return list of {@link StoredItemStack} on the network
	 */
	public List<StoredItemStack> getStackList(List<BlockCoords> network) {
		return Collections.EMPTY_LIST;
	}

	/**used for getting the full list of Items at a given {@link IConnectionNode}
	 * @param storedStacks current list of {@link StoredItemStack} to be added to
	 * @param node {@link IConnectionNode} to check from
	 * @return list of {@link StoredItemStack} on the network
	 */
	public List<StoredItemStack> getTileInventory(List<StoredItemStack> storedStacks, IConnectionNode node) {
		return Collections.EMPTY_LIST;
	}
	
	/**used for getting the full list of Items at a given {@link IEntityNode}
	 * @param storedStacks current list of {@link StoredItemStack} to be added to
	 * @param node {@link IEntityNode} to check from
	 * @return list of {@link StoredItemStack} on the network
	 */
	public List<StoredItemStack> getEntityInventory(List<StoredItemStack> storedStacks, IEntityNode node) {
		return Collections.EMPTY_LIST;
	}

	/**convenient method, adds the given inventory {@link IInventory} to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param inv {@link IInventory} to combine
	 */
	public void addInventoryToList(List<StoredItemStack> list, IInventory inv) {
	}

	/**convenient method, adds the given stack to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param stack {@link StoredItemStack} to combine
	 */
	public void addStackToList(List<StoredItemStack> list, ItemStack stack) {
	}
	
	/**used for adding Items to the network
	 * @param add {@link StoredItemStack} to add
	 * @param network current coordinates of the network
	 * @return remaining {@link StoredItemStack} (what wasn't added), can be null
	 */
	public StoredItemStack addItems(StoredItemStack add, List<BlockCoords> network) {
		return add;
	}
	
	/**used for removing Items from the network
	 * @param remove {@link StoredItemStack} to remove
	 * @param network current coordinates of the network
	 * @return remaining {@link StoredItemStack} (what wasn't removed), can be null
	 */
	public StoredItemStack removeItems(StoredItemStack remove, List<BlockCoords> network) {
		return remove;
	}
	/**gets the {@link StoredItemStack} in the given slot of the first valid inventory on the network, used by the Inventory Reader
	 * @param network current coordinates of the network
	 * @param slot id of the slot to look for the stack in
	 * @return {@link StoredItemStack} of the ItemStack in the slot
	 */
	public StoredItemStack getStack(List<BlockCoords> network, int slot) {
		return null;
	}
	/**gets the {@link StoredItemStack} in the given slot of the entity connected to the {@link IEntityNode}
	 * @param node {@link IEntityNode} to check at
	 * @param slot id of the slot to look for the stack in
	 * @return {@link StoredItemStack} of the ItemStack in the slot
	 */
	public StoredItemStack getEntityStack(IEntityNode node, int slot) {
		return null;
	}
	/**gets the {@link StoredItemStack} in the given slot of the tile connected to the {@link IConnectionNode}
	 * @param node {@link IConnectionNode} to check at
	 * @param slot id of the slot to look for the stack in
	 * @return {@link StoredItemStack} of the ItemStack in the slot
	 */
	public StoredItemStack getTileStack(IConnectionNode node, int slot) {
		return null;
	}
}
