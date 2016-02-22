package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.api.wrappers.ItemWrapper;

public class ItemHelper extends ItemWrapper {

	public StorageItems getStackList(List<BlockCoords> network) {
		List<StoredItemStack> storedStacks = new ArrayList();
		StorageSize storage = new StorageSize(0,0);
		for (BlockCoords connect : network) {
			Object tile = connect.getTileEntity();
			if (tile != null) {
				if (tile instanceof IConnectionNode) {
					storage = getTileInventory(storedStacks, storage, (IConnectionNode) tile);

				}
				if (tile instanceof IEntityNode) {
					storage = getEntityInventory(storedStacks, storage, (IEntityNode) tile);
				}
			}
		}
		Collections.sort(storedStacks, new Comparator<StoredItemStack>() {
			public int compare(StoredItemStack str1, StoredItemStack str2) {
				if (str1.stored < str2.stored)
					return 1;
				if (str1.stored == str2.stored)
					return 0;
				return -1;
			}
		});
		return new StorageItems(storedStacks,storage);
	}

	public StorageSize getTileInventory(List<StoredItemStack> storedStacks, StorageSize storage, IConnectionNode node) {
		Map<BlockCoords, ForgeDirection> connections = node.getConnections();
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			boolean specialProvider = false;
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
				if (tile != null && provider.canHandleItems(tile, entry.getValue())) {
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
		}

		return storage;
	}

	public StorageSize getEntityInventory(List<StoredItemStack> storedStacks, StorageSize storage, IEntityNode tileNode) {
		List<Entity> entityList = tileNode.getEntities();
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

	public StoredItemStack addItems(StoredItemStack add, List<BlockCoords> network, ActionType action) {
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
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

	public StoredItemStack removeItems(StoredItemStack remove, List<BlockCoords> network, ActionType action) {
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
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

	public StoredItemStack getStack(List<BlockCoords> connections, int slot) {
		if (connections == null) {
			return null;
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

	public StoredItemStack getTileStack(IConnectionNode node, int slot) {
		Map<BlockCoords, ForgeDirection> connections = node.getConnections();
		for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
			for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
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
		if (!enderChest) {
			inv = player.inventory;
		} else {
			inv = player.getInventoryEnderChest();
		}
		if (inv == null) {
			return add;
		}
		List<Integer> empty = new ArrayList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				if (!(stack.stackSize >= stack.getMaxStackSize()) && add.equalStack(stack) && stack.stackSize < inv.getInventoryStackLimit()) {
					long used = (long) Math.min(add.item.getMaxStackSize(), Math.min(add.stored, inv.getInventoryStackLimit() - stack.stackSize));
					stack.stackSize += used;
					add.stored -= used;
					if (used == 0 || !action.shouldSimulate()) {
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
		return add;
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
}
