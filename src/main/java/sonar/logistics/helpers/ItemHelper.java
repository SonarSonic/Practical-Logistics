package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.PacketInvUpdate;
import sonar.core.utils.ActionType;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.api.wrappers.ItemWrapper;

public class ItemHelper extends ItemWrapper {

	public StorageItems getStackList(INetworkCache network) {
		List<StoredItemStack> storedStacks = new ArrayList();
		StorageSize storage = new StorageSize(0, 0);

		Entry<BlockCoords, ForgeDirection> coord = network.getExternalBlock();
		if (coord != null) {
			storage = getTileInventory(storedStacks, storage, coord);
		} else {
			TileEntity tile = network.getFirstTileEntity(CacheTypes.ENTITY_NODES);
			if (tile != null && tile instanceof IEntityNode) {
				storage = getEntityInventory(storedStacks, storage, ((IEntityNode) tile).getEntities());
			}
		}
		return new StorageItems(storedStacks, storage);
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
		for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
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
				StorageSize size = addInventoryToList(storedStacks, player.inventory);
				storage.addItems(size.getStoredFluids());
				storage.addStorage(size.getMaxFluids());
			}
		}
		return storage;

	}

	public StorageSize addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		long stored = 0;
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				stored += stack.stackSize;
				addStackToList(list, inv.getStackInSlot(i));
			}
		}
		return new StorageSize(stored, inv.getInventoryStackLimit() * inv.getSizeInventory());
	}

	public void addStackToList(List<StoredItemStack> list, StoredItemStack stack) {
		if (stack == null || list == null) {
			return;
		}
		int pos = 0;
		for (StoredItemStack storedStack : list) {
			if (storedStack.equalStack(stack.item)) {
				list.get(pos).add(stack);
				return;
			}
			pos++;
		}
		list.add(stack);
	}

	private void addStackToList(List<StoredItemStack> list, ItemStack stack) {
		int pos = 0;
		for (StoredItemStack storedStack : list) {
			if (storedStack.equalStack(stack)) {
				list.get(pos).add(stack);
				return;
			}
			pos++;
		}
		list.add(new StoredItemStack(stack));
	}

	public StoredItemStack addItems(StoredItemStack add, INetworkCache network, ActionType action) {
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks();
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
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
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks();
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
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
		Entry<BlockCoords, ForgeDirection> block = network.getExternalBlock();
		StoredItemStack stack = getTileStack(network, slot);
		if (stack == null) {
			network.getFirstConnection(CacheTypes.EMITTER);
		}
		return stack;
/*
		if (block != null) {
			return getTileStack(network, slot);
		} else {
			
		}
		for (BlockCoords connect : connections) {
			Object tile = connect.getTileEntity();
			if (tile != null) {
				if (tile instanceof IConnectionNode) {
					return getTileStack((IConnectionNode) tile, slot);
				}
				if (tile instanceof IEntityNode) {
					return getEntityStack((IEntityNode) tile, slot);
				}
			}
		}

		return null;
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
		Map<BlockCoords, ForgeDirection> connections = network.getExternalBlocks();
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
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

	public void spawnStoredItemStack(StoredItemStack drop, World world, int x, int y, int z, ForgeDirection side) {
		List<EntityItem> drops = new ArrayList();
		while (!(drop.stored <= 0)) {
			ItemStack dropStack = drop.getItemStack();
			dropStack.stackSize = (int) Math.min(drop.stored, dropStack.getMaxStackSize());
			drop.stored -= dropStack.stackSize;
			drops.add(new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, dropStack));
		}
		if (drop.stored < 0) {
			Logistics.logger.error("ERROR: Excess Items in Drop");
		}
		for (EntityItem item : drops) {
			item.motionX = 0;
			item.motionY = 0;
			item.motionZ = 0;
			if (side == ForgeDirection.NORTH) {
				item.motionZ = -0.1;
			}
			if (side == ForgeDirection.SOUTH) {
				item.motionZ = 0.1;
			}
			if (side == ForgeDirection.WEST) {
				item.motionX = -0.1;
			}
			if (side == ForgeDirection.EAST) {
				item.motionX = 0.1;
			}
			world.spawnEntityInWorld(item);
		}
	}

	public StoredItemStack removeToPlayerInventory(StoredItemStack stack, long extractSize, INetworkCache network, EntityPlayer player, ActionType type) {
		StoredItemStack simulate = getStackToAdd(extractSize, stack, removeItems(stack.copy().setStackSize(extractSize), network, type));
		if (simulate == null) {
			return null;
		}
		StoredItemStack returned = getStackToAdd(stack.stored, simulate, addStackToPlayer(simulate.copy(), player, false, type));
		return returned;

	}

	public StoredItemStack addFromPlayerInventory(StoredItemStack stack, long extractSize, INetworkCache network, EntityPlayer player, ActionType type) {
		StoredItemStack simulate = getStackToAdd(extractSize, stack, removeStackFromPlayer(stack.copy().setStackSize(extractSize), player, false, type));
		if (simulate == null) {
			return null;
		}
		StoredItemStack returned = getStackToAdd(stack.stored, simulate, addItems(simulate.copy(), network, type));
		return returned;

	}

	public StoredItemStack getStackToAdd(long inputSize, StoredItemStack stack, StoredItemStack returned) {
		StoredItemStack simulateStack = null;
		if (returned == null || returned.stored == 0) {
			simulateStack = new StoredItemStack(stack.getItemStack(), inputSize);
		} else {
			simulateStack = new StoredItemStack(stack.getItemStack(), inputSize - returned.stored);
		}
		return simulateStack;
	}
}
