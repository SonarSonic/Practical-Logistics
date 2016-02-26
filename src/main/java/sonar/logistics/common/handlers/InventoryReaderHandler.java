package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.ActionType;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.BlockInteraction;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.interaction.IDefaultInteraction;
import sonar.logistics.api.providers.InventoryHandler.StorageSize;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredStackInfo;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class InventoryReaderHandler extends InventoryTileHandler implements IByteBufTile, IDefaultInteraction {

	public BlockCoords coords;
	public List<StoredItemStack> stacks = new ArrayList();
	public List<StoredItemStack> lastStacks = new ArrayList();

	public ItemStack current;
	// 0=Stack, 1=Slot (only accepts one input)
	public SyncTagType.INT setting = (INT) new SyncTagType.INT(1).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingOrder = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingType = (INT) new SyncTagType.INT(5).addSyncType(SyncType.SPECIAL);
	public StorageSize maxStorage = StorageSize.EMPTY;

	public InventoryReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
		super.slots = new ItemStack[1];
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		StorageItems list = LogisticsAPI.getItemHelper().getStackList(getNetwork(te));
		if (sortingType.getObject() == 0) {
			Collections.sort(list.items, new Comparator<StoredItemStack>() {
				public int compare(StoredItemStack str1, StoredItemStack str2) {
					if (str1.stored < str2.stored)
						return sortingOrder.getObject() == 0 ? 1 : -1;
					if (str1.stored == str2.stored)
						return 0;
					return sortingOrder.getObject() == 0 ? -1 : 1;
				}
			});
		} else if (sortingType.getObject() == 1) {
			Collections.sort(list.items, new Comparator<StoredItemStack>() {
				public int compare(StoredItemStack str1, StoredItemStack str2) {
					int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getItemStack().getDisplayName(), str2.getItemStack().getDisplayName());
					if (res == 0) {
						res = str1.getItemStack().getDisplayName().compareTo(str2.getItemStack().getDisplayName());
					}
					return sortingOrder.getObject() == 0 ? res : -res;
				}
			});
		} else if (sortingType.getObject() == 2) {
			Collections.sort(list.items, new Comparator<StoredItemStack>() {
				public int compare(StoredItemStack str1, StoredItemStack str2) {
					UniqueIdentifier modid1 = GameRegistry.findUniqueIdentifierFor(str1.getItemStack().getItem());
					UniqueIdentifier modid2 = GameRegistry.findUniqueIdentifierFor(str2.getItemStack().getItem());
					int res = String.CASE_INSENSITIVE_ORDER.compare(modid1.modId, modid2.modId);
					if (res == 0) {
						res = modid1.modId.compareTo(modid2.modId);
					}
					if (res == 0) {
						res = String.CASE_INSENSITIVE_ORDER.compare(modid1.name, modid2.name);
						if (res == 0) {
							res = modid1.name.compareTo(modid2.name);
						}
					}
					return sortingOrder.getObject() == 0 ? res : -res;
				}
			});
		}

		stacks = list.items;
		maxStorage = list.sizing;
	}

	public List<BlockCoords> getNetwork(TileEntity te) {
		return LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te))) || dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
		}
	}

	public Info currentInfo(TileEntity te) {

		switch (setting.getObject()) {
		case 0:
			if (slots[0] != null) {
				if (stacks != null) {
					for (StoredItemStack stack : stacks) {
						if (stack.equalStack(slots[0])) {
							return StoredStackInfo.createInfo(stack);
						}
					}
				}
				return StoredStackInfo.createInfo(new StoredItemStack(slots[0], 0));
			}
			break;
		case 1:
			StoredItemStack stack = LogisticsAPI.getItemHelper().getStack(LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()), targetSlot.getObject());
			if (stack != null) {
				return StoredStackInfo.createInfo(stack);
			}
			return new StandardInfo((byte) -1, "ITEMREND", " ", " ");

		case 2:
			if (posSlot.getObject() < stacks.size()) {
				return StoredStackInfo.createInfo(stacks.get(posSlot.getObject()));
			}
			break;
		case 3:
			if (stacks != null) {
				return InventoryInfo.createInfo(stacks);
			}
			break;
		case 4:
			return new ProgressInfo(maxStorage.getStoredFluids(), maxStorage.getMaxFluids(), FontHelper.formatStackSize(maxStorage.getStoredFluids()) + " / " + FontHelper.formatStackSize(maxStorage.getMaxFluids()));
		}
		return new StandardInfo((byte) -1, "ITEMREND", " ", "NO DATA");
	}

	@Override
	public void handleInteraction(Info info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		if (player.getHeldItem() != null) {
			if (!doubleClick) {
				insertItem(player, reader, player.inventory.currentItem);
			} else {
				insertInventory(player, reader, player.inventory.currentItem);
			}
		}
	}

	public StoredItemStack extractItem(TileEntity te, StoredItemStack stack, int max) {
		if (stack == null || stack.stored == 0) {
			return null;
		}
		int extractSize = (int) Math.min(stack.getItemStack().getMaxStackSize(), Math.min(stack.stored, max));
		StoredItemStack remainder = LogisticsAPI.getItemHelper().removeItems(stack.setStackSize(extractSize), LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()), ActionType.PERFORM);
		StoredItemStack storedStack = null;
		if (remainder == null || remainder.stored == 0) {
			storedStack = new StoredItemStack(stack.getItemStack(), extractSize);
		} else {
			storedStack = new StoredItemStack(stack.getItemStack(), extractSize - remainder.stored);
		}

		return storedStack;
	}

	public void insertItem(EntityPlayer player, TileEntity te, int slot) {
		ItemStack add = player.inventory.getStackInSlot(slot);
		if (add == null) {
			return;
		}
		StoredItemStack stack = LogisticsAPI.getItemHelper().addItems(new StoredItemStack(add), LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()), ActionType.PERFORM);
		if (stack == null || stack.stored == 0) {
			add = null;
		} else {
			add.stackSize = (int) stack.stored;
		}
		if (!ItemStack.areItemStacksEqual(add, player.inventory.getStackInSlot(slot))) {
			player.inventory.setInventorySlotContents(slot, add);
		}
	}

	public void insertInventory(EntityPlayer player, TileEntity te, int slotID) {
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
		List<Integer> slots = new ArrayList();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack item = inv.getStackInSlot(i);
			if (stack.equalStack(item)) {
				stack.add(item);
				slots.add(i);
			}
		}
		StoredItemStack remainder = LogisticsAPI.getItemHelper().addItems(stack.copy(), LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()), ActionType.PERFORM);

		long insertSize = 0;
		if (remainder == null || remainder.stored == 0) {
			insertSize = stack.getStackSize();
		} else {
			insertSize = stack.getStackSize() - remainder.stored;
		}
		if (insertSize == 0) {
			return;
		}
		LogisticsAPI.getItemHelper().removeStackFromPlayer(stack.copy().setStackSize(insertSize), player, false, ActionType.PERFORM);
		/*
		for (Integer slot : slots) {
			ItemStack item = inv.getStackInSlot(slot);
			int oldSize = item.stackSize;
			if (stack.equalStack(item)) {
				if (remainder == null || remainder.stored == 0) {
					item.stackSize = 0;
				} else {
					item.stackSize = (int) Math.min(insertSize, item.getMaxStackSize());
				}
				insertSize -= oldSize - item.stackSize;
				if (item.stackSize != 0) {
					player.inventory.setInventorySlotContents(slot, item);
				} else {
					player.inventory.setInventorySlotContents(slot, null);
				}
				if (insertSize == 0) {
					return;
				}
			}
		}
		*/

	}


	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		setting.readFromNBT(nbt, type);
		targetSlot.readFromNBT(nbt, type);
		posSlot.readFromNBT(nbt, type);
		if (type == SyncType.SAVE) {
			if (nbt.hasKey("coords")) {
				if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
					coords = BlockCoords.readFromNBT(nbt.getCompoundTag("coords"));
				} else {
					coords = null;
				}
			}
		}
		if (type == SyncType.SPECIAL) {

			if (nbt.hasKey("null")) {
				this.stacks = new ArrayList();
				return;
			}
			NBTTagList list = nbt.getTagList("Stacks", 10);
			if (this.stacks == null) {
				this.stacks = new ArrayList();
			}
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				int slot = compound.getInteger("Slot");
				boolean set = slot < stacks.size();
				switch (compound.getByte("f")) {
				case 0:
					if (set)
						stacks.set(slot, StoredItemStack.readFromNBT(compound));
					else
						stacks.add(slot, StoredItemStack.readFromNBT(compound));
					break;
				case 1:
					long stored = compound.getLong("Stored");
					if (stacks.get(slot) != null && stored != 0) {
						stacks.set(slot, new StoredItemStack(stacks.get(slot).item, stored));
					} else {
						stacks.set(slot, StoredItemStack.readFromNBT(compound));
					}
					break;
				case 2:
					if (set)
						stacks.set(slot, null);
					else
						stacks.add(slot, null);
					break;
				}

			}

		}
		if (type == SyncType.SYNC) {
			NBTTagList list = nbt.getTagList("StoredStacks", 10);
			this.stacks = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.stacks.add(StoredItemStack.readFromNBT(compound));

			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			NBTTagCompound coordTag = new NBTTagCompound();
			if (coords != null) {
				BlockCoords.writeToNBT(coordTag, coords);
				coordTag.setBoolean("hasCoords", true);
			} else {
				coordTag.setBoolean("hasCoords", false);
			}
			nbt.setTag("coords", coordTag);
		}
		if (type == SyncType.SPECIAL) {

			if (stacks == null) {
				stacks = new ArrayList();
			}
			if (lastStacks == null) {
				lastStacks = new ArrayList();
			}
			if (this.stacks.size() <= 0 && (!(this.lastStacks.size() <= 0))) {
				nbt.setBoolean("null", true);
				this.lastStacks = new ArrayList();
				return;
			}
			NBTTagList list = new NBTTagList();
			int size = Math.max(this.stacks.size(), this.lastStacks.size());
			for (int i = 0; i < size; ++i) {
				StoredItemStack current = null;
				StoredItemStack last = null;
				if (i < this.stacks.size()) {
					current = this.stacks.get(i);
				}
				if (i < this.lastStacks.size()) {
					last = this.lastStacks.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!last.item.equals(current.item)) {
							compound.setByte("f", (byte) 0);
							this.lastStacks.set(i, current);
							StoredItemStack.writeToNBT(compound, this.stacks.get(i));

						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastStacks.add(i, current);
						StoredItemStack.writeToNBT(compound, this.stacks.get(i));
					}
				} else if (last != null) {
					this.lastStacks.set(i, null);
					compound.setByte("f", (byte) 2);
				}
				if (!compound.hasNoTags()) {
					compound.setInteger("Slot", i);
					list.appendTag(compound);
				}

			}
			if (list.tagCount() != 0) {
				nbt.setTag("Stacks", list);
			}

		}
		if (type == SyncType.SYNC) {
			NBTTagList list = new NBTTagList();
			if (stacks == null) {
				stacks = new ArrayList();
			}
			for (int i = 0; i < this.stacks.size(); i++) {
				if (this.stacks.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					StoredItemStack.writeToNBT(compound, this.stacks.get(i));
					list.appendTag(compound);
				}
			}

			nbt.setTag("StoredStacks", list);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(setting, targetSlot, posSlot, sortingOrder, sortingType));
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
		} else if (id == 1) {
			targetSlot.writeToBuf(buf);
		} else if (id == 2) {
			posSlot.writeToBuf(buf);
		} else if (id == 3) {
			sortingOrder.writeToBuf(buf);
		} else if (id == 4) {
			sortingType.writeToBuf(buf);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			if (setting.getObject() == 4) {
				setting.setObject(0);
			} else {
				setting.increaseBy(1);
			}
		} else if (id == 1) {
			targetSlot.readFromBuf(buf);
		} else if (id == 2) {
			posSlot.readFromBuf(buf);
		} else if (id == 3) {
			sortingOrder.readFromBuf(buf);
		} else if (id == 4) {
			sortingType.readFromBuf(buf);
		}
	}

}
