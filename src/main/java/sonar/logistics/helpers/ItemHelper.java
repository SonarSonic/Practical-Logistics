package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.ActionType;
import sonar.core.api.BlockCoords;
import sonar.core.api.InventoryHandler;
import sonar.core.api.InventoryHandler.StorageSize;
import sonar.core.api.SonarAPI;
import sonar.core.api.StoredItemStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketInvUpdate;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IStorageCache;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.wrappers.ItemWrapper;

public class ItemHelper extends ItemWrapper {

	public StorageItems getItems(INetworkCache network) {
		if (network instanceof IStorageCache) {
			return ((IStorageCache) network).getStoredItems();
		}
		return StorageItems.EMPTY;
	}

	public StorageSize getTileInventory(List<StoredItemStack> storedStacks, StorageSize storage, Map<BlockCoords, ForgeDirection> connections) {
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			storage = getTileInventory(storedStacks, storage, entry);
		}
		return storage;
	}

	public StorageSize getTileInventory(List<StoredItemStack> storedStacks, StorageSize storage, Entry<BlockCoords, ForgeDirection> entry) {
		TileEntity tile = entry.getKey().getTileEntity();
		if (tile == null) {
			return storage;
		}
		boolean specialProvider = false;
		for (InventoryHandler provider : SonarCore.inventoryProviders.getObjects()) {
			if (provider.canHandleItems(tile, entry.getValue())) {
				if (!specialProvider) {
					StorageSize size = provider.getItems(storedStacks, tile, entry.getValue());
					if (size != StorageSize.EMPTY) {
						specialProvider = true;
						storage.addItems(size.getStoredFluids());
						storage.addStorage(size.getMaxFluids());
					}
				} else {
					continue;
				}
			}
		}

		return storage;
	}

	public StorageSize getEntityInventory(List<StoredItemStack> storedStacks, StorageSize storage, List<Entity> entityList) {
		for (Entity entity : entityList) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				StorageSize size = SonarAPI.getItemHelper().addInventoryToList(storedStacks, player.inventory);
				storage.addItems(size.getStoredFluids());
				storage.addStorage(size.getMaxFluids());
			}
		}
		return storage;

	}

	public StoredItemStack addItems(StoredItemStack add, INetworkCache network, ActionType action) {
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (InventoryHandler provider : SonarCore.inventoryProviders.getObjects()) {
				if (provider.canHandleItems(tile, entry.getValue())) {
					add = provider.addStack(add, tile, entry.getValue(), action);
					if (add == null) {
						return null;
					}
				}
			}
		}
		return add;
	}

	public StoredItemStack removeItems(StoredItemStack remove, INetworkCache network, ActionType action) {
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (InventoryHandler provider : SonarCore.inventoryProviders.getObjects()) {
				if (provider.canHandleItems(tile, entry.getValue())) {
					remove = provider.removeStack(remove, tile, entry.getValue(), action);
					if (remove == null) {
						return null;
					}
				}
			}
		}
		return remove;
	}

	public StoredItemStack getStack(INetworkCache network, int slot) {
		Entry<BlockCoords, ForgeDirection> block = network.getExternalBlock(true);
		StoredItemStack stack = getTileStack(network, slot);
		if (stack == null) {
			network.getFirstConnection(CacheTypes.EMITTER);
		}
		return stack;
		/*
		 * if (block != null) { return getTileStack(network, slot); } else {
		 * 
		 * } for (BlockCoords connect : connections) { Object tile = connect.getTileEntity(); if (tile != null) { if (tile instanceof IConnectionNode) { return getTileStack((IConnectionNode) tile, slot); } if (tile instanceof IEntityNode) { return getEntityStack((IEntityNode) tile, slot); } } }
		 * 
		 * return null;
		 */
	}

	public StoredItemStack getEntityStack(IEntityNode node, int slot) {
		List<StoredItemStack> storedStacks = new ArrayList();
		List<Entity> entityList = node.getEntities();
		for (Entity entity : entityList) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				IInventory inv = (IInventory) player.inventory;
				if (slot < inv.getSizeInventory()) {
					ItemStack stack = inv.getStackInSlot(slot);
					if (stack == null) {
						return null;
					} else {
						return new StoredItemStack(stack);
					}
				}
			}
		}

		return null;
	}

	public StoredItemStack getTileStack(INetworkCache network, int slot) {
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			for (InventoryHandler provider : SonarCore.inventoryProviders.getObjects()) {
				TileEntity tile = entry.getKey().getTileEntity();
				if (tile != null && provider.canHandleItems(tile, entry.getValue())) {
					return provider.getStack(slot, tile, entry.getValue());
				}
			}

		}
		return null;
	}

	public StoredItemStack addStackToPlayer(StoredItemStack add, EntityPlayer player, boolean enderChest, ActionType action) {
		if (add == null) {
			return null;
		}
		IInventory inv = null;
		int size = 0;
		if (!enderChest) {
			inv = player.inventory;
			size = player.inventory.mainInventory.length;
		} else {
			inv = player.getInventoryEnderChest();
			size = inv.getSizeInventory();
		}
		if (inv == null || size == 0) {
			return add;
		}
		List<Integer> empty = new ArrayList();
		for (int i = 0; i < size; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (!(stack.stackSize >= stack.getMaxStackSize()) && add.equalStack(stack) && stack.stackSize < inv.getInventoryStackLimit()) {
					long used = (long) Math.min(add.item.getMaxStackSize(), Math.min(add.stored, inv.getInventoryStackLimit() - stack.stackSize));
					stack.stackSize += used;
					add.stored -= used;
					if (used != 0 && !action.shouldSimulate()) {
						inv.setInventorySlotContents(i, stack);
						if (!enderChest) {
							SonarCore.network.sendTo(new PacketInvUpdate(i, stack), (EntityPlayerMP) player);
						}
					}
					if (add.stored == 0) {
						return null;
					}
				}

			} else {
				empty.add(i);
			}

		}
		for (Integer slot : empty) {
			ItemStack stack = add.item.copy();
			if (inv.isItemValidForSlot(slot, stack)) {
				int used = (int) Math.min(add.stored, inv.getInventoryStackLimit());
				stack.stackSize = used;
				add.stored -= used;
				if (!action.shouldSimulate()) {
					inv.setInventorySlotContents(slot, stack);
					if (!enderChest) {
						SonarCore.network.sendTo(new PacketInvUpdate(slot, stack), (EntityPlayerMP) player);
					}
				}
				if (add.stored == 0) {
					return null;
				}
			}
		}
		return add;
	}

	public StoredItemStack removeStackFromPlayer(StoredItemStack remove, EntityPlayer player, boolean enderChest, ActionType action) {
		if (remove == null) {
			return null;
		}
		IInventory inv = null;
		int size = 0;
		if (!enderChest) {
			inv = player.inventory;
			size = player.inventory.mainInventory.length;
		} else {
			inv = player.getInventoryEnderChest();
			size = inv.getSizeInventory();
		}
		if (inv == null || size == 0) {
			return remove;
		}
		List<Integer> empty = new ArrayList();
		for (int i = 0; i < size; i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (remove.equalStack(stack)) {
					long used = (long) Math.min(remove.stored, Math.min(inv.getInventoryStackLimit(), stack.stackSize));
					stack.stackSize -= used;
					remove.stored -= used;
					if (!action.shouldSimulate()) {
						if (stack.stackSize == 0) {
							stack = null;
						}
						inv.setInventorySlotContents(i, stack);
					}
					if (remove.stored == 0) {
						return null;
					}
				}

			}

		}
		return remove;
	}

	public StoredItemStack removeToPlayerInventory(StoredItemStack stack, long extractSize, INetworkCache network, EntityPlayer player, ActionType type) {
		StoredItemStack simulate = SonarAPI.getItemHelper().getStackToAdd(extractSize, stack, removeItems(stack.copy().setStackSize(extractSize), network, type));
		if (simulate == null) {
			return null;
		}
		StoredItemStack returned = SonarAPI.getItemHelper().getStackToAdd(stack.stored, simulate, addStackToPlayer(simulate.copy(), player, false, type));
		return returned;

	}

	public StoredItemStack addFromPlayerInventory(StoredItemStack stack, long extractSize, INetworkCache network, EntityPlayer player, ActionType type) {
		StoredItemStack simulate = SonarAPI.getItemHelper().getStackToAdd(extractSize, stack, removeStackFromPlayer(stack.copy().setStackSize(extractSize), player, false, type));
		if (simulate == null) {
			return null;
		}
		StoredItemStack returned = SonarAPI.getItemHelper().getStackToAdd(stack.stored, simulate, addItems(simulate.copy(), network, type));
		return returned;

	}

	public StoredItemStack extractItem(INetworkCache cache, StoredItemStack stack) {
		if (stack != null && stack.stored != 0) {
			StoredItemStack extract = LogisticsAPI.getItemHelper().removeItems(stack.copy(), cache, ActionType.PERFORM);
			StoredItemStack toAdd = SonarAPI.getItemHelper().getStackToAdd(stack.getStackSize(), stack, extract);
			return toAdd;
		}
		return null;
	}

	public void insertInventoryFromPlayer(EntityPlayer player, INetworkCache cache, int slotID) {
		ItemStack add = null;
		if (slotID == -1) {
			add = player.inventory.getItemStack();
		} else
			add = player.inventory.getStackInSlot(slotID);
		if (add == null) {
			return;
		}
		StoredItemStack stack = new StoredItemStack(add).setStackSize(0);
		IInventory inv = player.inventory;
		ArrayList<Integer> slots = new ArrayList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack item = inv.getStackInSlot(i);
			if (stack.equalStack(item)) {
				stack.add(item);
				slots.add(i);
			}
		}
		StoredItemStack remainder = LogisticsAPI.getItemHelper().addItems(stack.copy(), cache, ActionType.PERFORM);
		StoredItemStack toAdd = SonarAPI.getItemHelper().getStackToAdd(stack.getStackSize(), stack, remainder);
		LogisticsAPI.getItemHelper().removeStackFromPlayer(toAdd, player, false, ActionType.PERFORM);

	}

	public void insertItemFromPlayer(EntityPlayer player, INetworkCache cache, int slot) {
		ItemStack add = player.inventory.getStackInSlot(slot);
		if (add == null) {
			return;
		}
		StoredItemStack stack = LogisticsAPI.getItemHelper().addItems(new StoredItemStack(add), cache, ActionType.PERFORM);
		if (stack == null || stack.stored == 0) {
			add = null;
		} else {
			add.stackSize = (int) stack.stored;
		}
		if (!ItemStack.areItemStacksEqual(add, player.inventory.getStackInSlot(slot))) {
			player.inventory.setInventorySlotContents(slot, add);
		}
	}

	public static boolean writeStorageToNBT(NBTTagCompound tag, boolean lastWasNull, StorageItems stacks, SyncType type) {
		if (type == SyncType.SYNC) {
			NBTTagList list = new NBTTagList();
			for (int i = 0; i < stacks.items.size(); i++) {
				if (stacks.items.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					StoredItemStack.writeToNBT(compound, stacks.items.get(i));
					list.appendTag(compound);
				}
			}
			if (list.tagCount() != 0) {
				tag.setTag("c", list);
				return false;
			} else {
				tag.setBoolean("n", true);
				return true;
			}
		} else if (type == SyncType.SPECIAL) {
			if ((stacks.items == null || stacks.items.isEmpty())) {
				if (!lastWasNull)
					tag.setBoolean("n", true);
				return true;
			}
			NBTTagList list = new NBTTagList();
			for (int l = 0; l < 2; l++) {
				ArrayList<StoredItemStack> stackList = null;
				switch (l) {
				case 0:
					stackList = stacks.changed;
					break;
				case 1:
					stackList = stacks.removed;
					break;
				}
				for (int i = 0; i < stackList.size(); i++) {
					if (stackList.get(i) != null) {
						NBTTagCompound compound = new NBTTagCompound();
						if (l == 1) {
							compound.setBoolean("r", true);
						}
						StoredItemStack.writeToNBT(compound, stackList.get(i));
						list.appendTag(compound);
					}
				}
			}
			if (list.tagCount() != 0) {
				tag.setTag("s", list);
			}
		}
		return false;
	}

	public static void readStorageToNBT(NBTTagCompound tag, ArrayList<StoredItemStack> current, SyncType type) {
		if (tag.hasKey("n")) {
			current.clear();
			return;
		}
		if (type == SyncType.SYNC) {
			if(!tag.hasKey("c")){
				return;
			}
			NBTTagList list = tag.getTagList("c", 10);
			current.clear();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				current.add(StoredItemStack.readFromNBT(compound));
			}
		} else if (type == SyncType.SPECIAL) {
			if(!tag.hasKey("s")){
				return;
			}
			NBTTagList list = tag.getTagList("s", 10);
			tags: for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				StoredItemStack stack = StoredItemStack.readFromNBT(compound);
				for (StoredItemStack stored : (ArrayList<StoredItemStack>) current.clone()) {
					if (stored.equalStack(stack.getItemStack())) {
						if (compound.getBoolean("r")) {
							current.remove(stored);
						} else {
							stored.setStackSize(stack.getStackSize());
						}
						continue tags;
					}
				}
				current.add(stack);
			}
		}
	}

	public static void sortItemList(ArrayList<StoredItemStack> current, final SortingDirection dir, SortingType type) {
		current.sort(new Comparator<StoredItemStack>() {
			public int compare(StoredItemStack str1, StoredItemStack str2) {
				int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getItemStack().getDisplayName(), str2.getItemStack().getDisplayName());
				if (res == 0) {
					res = str1.getItemStack().getDisplayName().compareTo(str2.getItemStack().getDisplayName());
				}
				return dir == SortingDirection.DOWN ? res : -res;
			}
		});

		switch (type) {
		case STORED:
			current.sort(new Comparator<StoredItemStack>() {
				public int compare(StoredItemStack str1, StoredItemStack str2) {
					if (str1.stored < str2.stored)
						return dir == SortingDirection.DOWN ? 1 : -1;
					if (str1.stored == str2.stored)
						return 0;
					return dir == SortingDirection.DOWN ? -1 : 1;
				}
			});
			break;
		case MODID:
			current.sort(new Comparator<StoredItemStack>() {
				public int compare(StoredItemStack str1, StoredItemStack str2) {
					UniqueIdentifier ui1 = GameRegistry.findUniqueIdentifierFor(str1.getItemStack().getItem());
					UniqueIdentifier ui2 = GameRegistry.findUniqueIdentifierFor(str2.getItemStack().getItem());
					int res = String.CASE_INSENSITIVE_ORDER.compare(ui1.modId, ui2.modId);
					if (res == 0) {
						res = ui1.modId.compareTo(ui2.modId);
					}
					return dir == SortingDirection.DOWN ? res : -res;
				}
			});
		default:
			break;
		}
	}
}
