package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.providers.InventoryHandler;
import sonar.logistics.api.wrappers.ItemWrapper;

public class ItemHelper extends ItemWrapper {

	public List<StoredItemStack> getStackList(List<BlockCoords> network) {
		List<StoredItemStack> storedStacks = new ArrayList();
		if (network == null) {
			return storedStacks;
		}
		for (BlockCoords connect : network) {
			Object tile = connect.getTileEntity();
			if (tile != null) {
				if (tile instanceof IConnectionNode) {
					getTileInventory(storedStacks, (IConnectionNode) tile);

				}
				if (tile instanceof IEntityNode) {
					getEntityInventory(storedStacks, (IEntityNode) tile);
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
		return storedStacks;
	}

	public List<StoredItemStack> getTileInventory(List<StoredItemStack> storedStacks, IConnectionNode node) {
		Map<BlockCoords, ForgeDirection> connections = node.getConnections();
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			boolean specialProvider = false;
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
				if (tile != null && provider.canHandleItems(tile, entry.getValue())) {
					if (!specialProvider) {
						specialProvider = provider.getItems(storedStacks, tile, entry.getValue());
					}
				}
			}
		}

		return storedStacks;
	}

	public List<StoredItemStack> getEntityInventory(List<StoredItemStack> storedStacks, IEntityNode tileNode) {
		List<Entity> entityList = tileNode.getEntities();
		for (Entity entity : entityList) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				addInventoryToList(storedStacks, player.inventory);
			}
		}
		return storedStacks;

	}

	public void addInventoryToList(List<StoredItemStack> list, IInventory inv) {
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i);
			if (stack != null) {
				addStackToList(list, inv.getStackInSlot(i));
			}
		}
	}

	public void addStackToList(List<StoredItemStack> list, ItemStack stack) {
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

	public StoredItemStack addItems(StoredItemStack add, List<BlockCoords> network) {
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
				if (provider.canHandleItems(tile, entry.getValue())) {
					add = provider.addStack(add, tile, entry.getValue());
					if (add == null) {
						return null;
					}
				}
			}
		}
		return add;
	}

	public StoredItemStack removeItems(StoredItemStack remove, List<BlockCoords> network) {
		Map<BlockCoords, ForgeDirection> connections = LogisticsAPI.getCableHelper().getTileConnections(network);
		for (Map.Entry<BlockCoords, ForgeDirection> entry : connections.entrySet()) {
			TileEntity tile = entry.getKey().getTileEntity();
			for (InventoryHandler provider : Logistics.inventoryProviders.getObjects()) {
				if (provider.canHandleItems(tile, entry.getValue())) {
					remove = provider.removeStack(remove, tile, entry.getValue());
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

}
