package sonar.logistics.api.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.ActionType;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.BlockInteraction;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.info.types.InventoryInfo;

public class ItemWrapper {

	public static class StorageItems {

		public static final StorageItems EMPTY = new StorageItems(new ArrayList(), StorageSize.EMPTY);
		public ArrayList<StoredItemStack> items;
		public StorageSize sizing;

		public StorageItems(ArrayList<StoredItemStack> items, StorageSize sizing) {
			this.items = items;
			this.sizing = sizing;
		}
	}

	/** used for getting the full list of Items on a given network
	 * @param network the {@link INetworkCache} to get Items from
	 * @return list of {@link StoredItemStack} on the network */
	public StorageItems getItems(INetworkCache network) {
		return StorageItems.EMPTY;
	}

	/** used for getting the full list of Items at a given {@link IConnectionNode}
	 * @param storedStacks current list of {@link StoredItemStack} to be added to
	 * @param storage currentStorageSize
	 * @param node {@link IConnectionNode} to check from
	 * @return list of {@link StoredItemStack} on the network */
	public StorageSize getTileInventory(List<StoredItemStack> storedStacks, StorageSize storage, Map<BlockCoords, ForgeDirection> connections) {
		return storage;
	}

	/** used for getting the full list of Items at a given {@link IEntityNode}
	 * @param storedStacks current list of {@link StoredItemStack} to be added to
	 * @param node {@link IEntityNode} to check from
	 * @return list of {@link StoredItemStack} on the network */
	public StorageSize getEntityInventory(List<StoredItemStack> storedStacks, StorageSize storage, List<Entity> entityList) {
		return storage;
	}

	/** convenient method, adds the given inventory {@link IInventory} to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param inv {@link IInventory} to combine
	 * @return returns how many ite */
	public StorageSize addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		return StorageSize.EMPTY;
	}

	/** convenient method, adds the given stack to the list, used by {@link InventoryHandler}
	 * @param list {@link StoredItemStack} list to add to
	 * @param stack {@link StoredItemStack} to combine */
	public void addStackToList(List<StoredItemStack> list, StoredItemStack stack) {
	}

	/** used for adding Items to the network
	 * @param add {@link StoredItemStack} to add
	 * @param network the {@link INetworkCache} to add to
	 * @param action what type of action should be carried out
	 * @return remaining {@link StoredItemStack} (what wasn't added), can be null */
	public StoredItemStack addItems(StoredItemStack add, INetworkCache network, ActionType action) {
		return add;
	}

	/** used for removing Items from the network
	 * @param remove {@link StoredItemStack} to remove
	 * @param network the {@link INetworkCache} to remove from
	 * @param action what type of action should be carried out
	 * @return remaining {@link StoredItemStack} (what wasn't removed), can be null */
	public StoredItemStack removeItems(StoredItemStack remove, INetworkCache network, ActionType action) {
		return remove;
	}

	/** gets the {@link StoredItemStack} in the given slot of the first valid inventory on the network, used by the Inventory Reader
	 * @param network the {@link INetworkCache} to get the stack from
	 * @param slot id of the slot to look for the stack in
	 * @return {@link StoredItemStack} of the ItemStack in the slot */
	public StoredItemStack getStack(INetworkCache network, int slot) {
		return null;
	}

	/** gets the {@link StoredItemStack} in the given slot of the entity connected to the {@link IEntityNode}
	 * @param node {@link IEntityNode} to check at
	 * @param slot id of the slot to look for the stack in
	 * @return {@link StoredItemStack} of the ItemStack in the slot */
	public StoredItemStack getEntityStack(IEntityNode node, int slot) {
		return null;
	}

	/** gets the {@link StoredItemStack} in the given slot of the tile connected to the {@link IConnectionNode}
	 * @param network the {@link INetworkCache} to get stack from
	 * @param slot id of the slot to look for the stack in
	 * @return {@link StoredItemStack} of the ItemStack in the slot */
	public StoredItemStack getTileStack(INetworkCache network, int slot) {
		return null;
	}

	/** should NEVER be called on client, adds a StoredItemStack to a player inventory and sends changes with client
	 * @param add {@link StoredItemStack} to add
	 * @param player player to add to
	 * @param enderChest should change player Ender Chest or their normal inventory
	 * @param action what type of action should be carried out
	 * @return remaining {@link StoredItemStack} (what wasn't added), can be null */
	public StoredItemStack addStackToPlayer(StoredItemStack add, EntityPlayer player, boolean enderChest, ActionType action) {
		return add;
	}

	/** should NEVER be called on client, removes a StoredItemStack to a player inventory and sends changes with client
	 * @param remove {@link StoredItemStack} to remove
	 * @param player player to remove from
	 * @param enderChest should change player Ender Chest or their normal inventory
	 * @param action what type of action should be carried out
	 * @return remaining {@link StoredItemStack} (what wasn't removed), can be null */
	public StoredItemStack removeStackFromPlayer(StoredItemStack remove, EntityPlayer player, boolean enderChest, ActionType action) {
		return remove;
	}

	/** drops a full StoredItemStack on the floor
	 * @param drop {@link StoredItemStack} to drop
	 * @param world the world to drop it in
	 * @param x the X coordinate it will be dropped from
	 * @param y the Y coordinate it will be dropped from
	 * @param z the Z coordinate it will be dropped from
	 * @param side side to drop from */
	public void spawnStoredItemStack(StoredItemStack drop, World world, int x, int y, int z, ForgeDirection side) {
	}

	/** convenience method, adds the given stack to the player's inventory and returns what was/can be added.
	 * @param stack
	 * @param extractSize
	 * @param network the {@link INetworkCache} to remove from
	 * @param player
	 * @return the {@link StoredItemStack} to add to the player */
	public StoredItemStack removeToPlayerInventory(StoredItemStack stack, long extractSize, INetworkCache network, EntityPlayer player, ActionType type) {
		return null;
	}

	/** convenience method, removes the given stack to the player's inventory and returns what was/can be added.
	 * @param stack
	 * @param extractSize
	 * @param network the {@link INetworkCache} to add to
	 * @param player
	 * @return the {@link StoredItemStack} to add to the player */
	public StoredItemStack addFromPlayerInventory(StoredItemStack stack, long extractSize, INetworkCache network, EntityPlayer player, ActionType type) {
		return null;
	}

	/** convenience method, gets the stack to be added to the inventory from the remainder, can return null.
	 * @param inputSize
	 * @param stack
	 * @param returned
	 * @return */
	public StoredItemStack getStackToAdd(long inputSize, StoredItemStack stack, StoredItemStack returned) {
		return null;
	}

	/** simple method to extract a given stack from a network
	 * @param cache the network to remove from
	 * @param stack the stack to remove
	 * @return the removed stack */
	public StoredItemStack extractItem(INetworkCache cache, StoredItemStack stack) {
		return null;
	}

	/** inserts all the items from the players inventory into the network which match the one in the given slot
	 * @param player the player who is inserting the items
	 * @param cache the network to add them to
	 * @param slot the slot of the item to be added */
	public void insertInventoryFromPlayer(EntityPlayer player, INetworkCache cache, int slotID) {
	}

	/** inserts an item into the given network from the players inventory from the given slot
	 * @param player the player who is inserting the items
	 * @param cache the network to add them to
	 * @param slot the slot to remove from */
	public void insertItemFromPlayer(EntityPlayer player, INetworkCache cache, int slot) {
	}
}
