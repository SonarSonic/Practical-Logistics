package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import sonar.core.SonarCore;
import sonar.core.api.SonarAPI;
import sonar.core.api.StorageSize;
import sonar.core.api.asm.InventoryHandler;
import sonar.core.api.inventories.ISonarInventoryHandler;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.api.utils.ActionType;
import sonar.core.api.utils.BlockCoords;
import sonar.core.handlers.inventories.IInventoryProvider;
import sonar.core.network.PacketInvUpdate;
import sonar.core.utils.SortingDirection;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.settings.InventoryReader.SortingType;
import sonar.logistics.api.wrappers.ItemWrapper;
import sonar.logistics.connections.monitoring.MonitoredItemStack;

public class ItemHelper extends ItemWrapper {

	public StorageSize getTileInventory(List<StoredItemStack> storedStacks, StorageSize storage, Map<BlockCoords, EnumFacing> connections) {
		for (Map.Entry<BlockCoords, EnumFacing> entry : connections.entrySet()) {
			storage = getTileInventory(storedStacks, storage, entry);
		}
		return storage;
	}

	public StorageSize getTileInventory(List<StoredItemStack> storedStacks, StorageSize storage, Entry<BlockCoords, EnumFacing> entry) {
		TileEntity tile = entry.getKey().getTileEntity();
		if (tile == null) {
			return storage;
		}
		boolean specialProvider = false;
		for (ISonarInventoryHandler provider : SonarCore.inventoryHandlers) {
			if (provider.canHandleItems(tile, entry.getValue())) {
				if (!specialProvider) {
					StorageSize size = provider.getItems(storedStacks, tile, entry.getValue());
					if (size != StorageSize.EMPTY) {
						specialProvider = true;
						storage.add(size);
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
				storage.add(size);
			}
		}
		return storage;

	}

	public StoredItemStack addItems(StoredItemStack add, INetworkCache network, ActionType action) {
		Map<BlockCoords, EnumFacing> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, EnumFacing> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			if (tile == null) {
				continue;
			}
			for (ISonarInventoryHandler provider : SonarCore.inventoryHandlers) {
				if (provider.canHandleItems(tile, entry.getValue())) {
					add = provider.addStack(add, tile, entry.getValue(), action);
					if (add == null) {
						return null;
					}
					break; // make sure to only use one InventoryHandler!!
				}
			}
		}
		return add;
	}

	public void addItemsFromPlayer(StoredItemStack add, EntityPlayer player, INetworkCache network, ActionType action) {
		IInventory inv = player.inventory;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null && stack.stackSize != 0 && add.equalStack(stack)) {
				StoredItemStack toAdd = new StoredItemStack(stack.copy());
				StoredItemStack perform = LogisticsAPI.getItemHelper().addItems(toAdd.copy(), network, ActionType.PERFORM);
				if (!toAdd.equals(perform)) {
					inv.setInventorySlotContents(i, StoredItemStack.getActualStack(perform));
					inv.markDirty();
				}
			}
		}
	}

	public StoredItemStack removeItems(StoredItemStack remove, INetworkCache network, ActionType action) {
		Map<BlockCoords, EnumFacing> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, EnumFacing> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			if (tile == null) {
				continue;
			}
			for (ISonarInventoryHandler provider : SonarCore.inventoryHandlers) {
				if(provider instanceof IInventoryProvider){
					continue;
				}
				if (provider.canHandleItems(tile, entry.getValue())) {
					remove = provider.removeStack(remove, tile, entry.getValue(), action);
					if (remove == null) {
						return null;
					}
					break; // make sure to only use one InventoryHandler!!
				}
			}
		}
		return remove;
	}

	public StoredItemStack getStack(INetworkCache network, int slot) {
		Entry<BlockCoords, EnumFacing> block = network.getExternalBlock(true);
		StoredItemStack stack = getTileStack(network, slot);
		if (stack == null) {
			// network.getFirstConnection(CacheTypes.EMITTER);
		}
		return stack;
		/* if (block != null) { return getTileStack(network, slot); } else {
		 * 
		 * } for (BlockCoords connect : connections) { Object tile = connect.getTileEntity(); if (tile != null) { if (tile instanceof IConnectionNode) { return getTileStack((IConnectionNode) tile, slot); } if (tile instanceof IEntityNode) { return getEntityStack((IEntityNode) tile, slot); } } }
		 * 
		 * return null; */
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
		Map<BlockCoords, EnumFacing> connections = network.getExternalBlocks(true);
		for (Map.Entry<BlockCoords, EnumFacing> entry : connections.entrySet()) {
			for (ISonarInventoryHandler provider : SonarCore.inventoryHandlers) {
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
		inv = !enderChest ? player.inventory : player.getInventoryEnderChest();
		size = !enderChest ? player.inventory.mainInventory.length : inv.getSizeInventory();
		if (inv == null || size == 0) {
			return remove;
		}
		for (int i = 0; i < size; i++) {
			ItemStack stack = inv.getStackInSlot(i).copy();
			if (stack != null && remove.equalStack(stack)) {
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
		if (add == null)
			return;
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

	public static void sortItemList(ArrayList<MonitoredItemStack> info, final SortingDirection dir, SortingType type) {
		info.sort(new Comparator<MonitoredItemStack>() {
			public int compare(MonitoredItemStack str1, MonitoredItemStack str2) {
				StoredItemStack item1 = str1.itemStack.getObject(), item2 = str2.itemStack.getObject();
				return InfoHelper.compareStringsWithDirection(item1.getItemStack().getDisplayName(), item2.getItemStack().getDisplayName(), dir);
			}
		});

		switch (type) {
		case STORED:
			info.sort(new Comparator<MonitoredItemStack>() {
				public int compare(MonitoredItemStack str1, MonitoredItemStack str2) {
					StoredItemStack item1 = str1.itemStack.getObject(), item2 = str2.itemStack.getObject();
					return InfoHelper.compareWithDirection(item1.stored, item2.stored, dir);
				}
			});
			break;
		case MODID:
			info.sort(new Comparator<MonitoredItemStack>() {
				public int compare(MonitoredItemStack str1, MonitoredItemStack str2) {
					StoredItemStack item1 = str1.itemStack.getObject(), item2 = str2.itemStack.getObject();
					String modid1 = item1.getItemStack().getItem().getRegistryName().getResourceDomain();
					String modid2 = item2.getItemStack().getItem().getRegistryName().getResourceDomain();
					return InfoHelper.compareStringsWithDirection(modid1, modid2, dir);
				}
			});
		default:
			break;
		}
	}
}
