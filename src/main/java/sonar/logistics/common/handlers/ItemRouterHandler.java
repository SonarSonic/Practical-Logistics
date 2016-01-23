package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncInt;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.InventoryHelper;
import sonar.core.utils.helpers.InventoryHelper.IInventoryFilter;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.SonarHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.info.filters.items.OreDictionaryFilter;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.StoredStackInfo;

public class ItemRouterHandler extends InventoryTileHandler implements ISidedInventory {

	// 0=nothing, 1=input, 2=output
	public SyncInt[] sideConfigs = new SyncInt[6];
	public BlockCoords[] coords = new BlockCoords[6];
	public List<ItemFilter>[] lastWhitelist = new List[6];
	public List<ItemFilter>[] whitelist = new List[6];

	public List<ItemFilter>[] lastBlacklist = new List[6];
	public List<ItemFilter>[] blacklist = new List[6];

	public int update = 0;
	public int updateTime = 20;

	public ItemRouterHandler(boolean isMultipart) {
		super(isMultipart);
		super.slots = new ItemStack[9];
		for (int i = 0; i < 6; i++) {
			sideConfigs[i] = new SyncInt(i);
			lastWhitelist[i] = new ArrayList();
			whitelist[i] = new ArrayList();
			lastBlacklist[i] = new ArrayList();
			blacklist[i] = new ArrayList();
		}
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		if (update < updateTime) {
			update++;
		} else {
			update = 0;
			updateConnections(te);
		}
		if (update < 6) {
			int config = this.sideConfigs[update].getInt();
			if (config != 0) {
				TileEntity target = null;
				ForgeDirection dir = null;
				TileEntity tile = this.coords[update] != null ? this.coords[update].getTileEntity() : null;
				if (tile != null && tile instanceof TileEntityBlockNode) {
					TileEntityBlockNode node = (TileEntityBlockNode) tile;
					dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
					target = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);

				} else {
					dir = ForgeDirection.getOrientation(update);
					target = te.getWorldObj().getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);
				}
				if (target != null && dir != null && target instanceof IInventory) {
					if (config == 1) {
						InventoryHelper.extractItems(target, te, dir.getOpposite().ordinal(), dir.ordinal(), null);
					} else {

						for (int i = 0; i < slots.length; i++) {
							if (slots[i] != null && matchesFilters(slots[i], whitelist[update], blacklist[update])) {
								slots[i] = InventoryHelper.addItems(target, slots[i], dir.ordinal(), null);
							}
						}

					}

				}

			}

		}
		SonarCore.sendFullSyncAround(te, 64);
	}

	public void updateConnections(TileEntity te) {
		for (int i = 0; i < 6; i++) {
			int config = sideConfigs[i].getInt();
			if (config != 0) {
				Object target = CableHelper.getConnectedTile(te, ForgeDirection.getOrientation(i));
				target = FMPHelper.checkObject(target);
				if (target == null) {
					this.coords[i] = null;
				} else {
					if (target instanceof TileEntityBlockNode || target instanceof TileEntityEntityNode) {
						TileEntity node = (TileEntity) target;
						this.coords[i] = new BlockCoords(node, node.getWorldObj().provider.dimensionId);
					} else {
						this.coords[i] = null;
					}
				}
			}
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return sideConfigs[dir.ordinal()].getInt() != 0;
	}

	@Override
	public void removed(World world, int x, int y, int z, int meta) {
		ForgeDirection dir = ForgeDirection.getOrientation(meta);
		Object tile = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
		tile = FMPHelper.checkObject(tile);
		if (tile != null) {
			if (tile instanceof IDataCable) {
				IDataCable cable = (IDataCable) tile;
				if (cable.getCoords() != null) {
					cable.setCoords(null);
				}
			}
		}

	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTTagList sideList = nbt.getTagList("Sides", 10);
			for (int i = 0; i < 6; i++) {
				NBTTagCompound compound = sideList.getCompoundTagAt(i);
				sideConfigs[i].readFromNBT(compound, type);
			}

			if (type == SyncType.SAVE) {
				NBTTagList list = nbt.getTagList("Configs", 10);
				for (int i = 0; i < 6; i++) {
					NBTTagCompound compound = list.getCompoundTagAt(i);
					if (compound.hasKey("x"))
						coords[i] = BlockCoords.readFromNBT(compound);
					list.appendTag(compound);
				}
				update = nbt.getInteger("update");

				for (int l = 0; l < 6; l++) {
					NBTTagList whitelist = nbt.getTagList("white" + l, 10);
					NBTTagList blacklist = nbt.getTagList("black" + l, 10);
					this.whitelist[l] = new ArrayList();
					this.blacklist[l] = new ArrayList();
					for (int i = 0; i < whitelist.tagCount(); i++) {
						NBTTagCompound compound = whitelist.getCompoundTagAt(i);
						this.whitelist[l].add((ItemFilter) NBTHelper.readNBTObject(compound, Logistics.itemFilters));

					}
					for (int i = 0; i < blacklist.tagCount(); i++) {
						NBTTagCompound compound = blacklist.getCompoundTagAt(i);
						this.whitelist[l].add((ItemFilter) NBTHelper.readNBTObject(compound, Logistics.itemFilters));

					}
				}
			}
		}
		if (type == SyncType.SPECIAL) {
			this.readList(nbt, whitelist, "whitelist");
			this.readList(nbt, blacklist, "blacklist");
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTTagList sideList = new NBTTagList();
			for (int i = 0; i < 6; i++) {
				NBTTagCompound compound = new NBTTagCompound();
				sideConfigs[i].writeToNBT(compound, type);
				sideList.appendTag(compound);
			}
			nbt.setTag("Sides", sideList);

			if (type == SyncType.SAVE) {
				NBTTagList list = new NBTTagList();
				for (int i = 0; i < 6; i++) {
					NBTTagCompound compound = new NBTTagCompound();
					if (coords[i] != null) {
						BlockCoords.writeToNBT(compound, coords[i]);
					}
					list.appendTag(compound);

				}
				nbt.setTag("Configs", list);
				nbt.setInteger("update", update);

				for (int l = 0; l < 6; l++) {
					NBTHelper.writeNBTObjectList("white" + l, nbt, whitelist[l]);
					NBTHelper.writeNBTObjectList("black" + l, nbt, blacklist[l]);
					/*
					 * NBTTagList whiteList = new NBTTagList(); NBTTagList
					 * blackList = new NBTTagList(); if (whitelist[l] == null) {
					 * whitelist[l] = new ArrayList(); } if (blacklist[l] ==
					 * null) { blacklist[l] = new ArrayList(); } for (int i = 0;
					 * i < this.whitelist[l].size(); i++) { if
					 * (this.whitelist[l].get(i) != null) { NBTTagCompound
					 * compound = new NBTTagCompound();
					 * InfoHelper.writeFilter(compound,
					 * this.whitelist[l].get(i));
					 * NBTHelper.writeNBTObject(object, tag);
					 * whiteList.appendTag(compound); } }
					 * 
					 * for (int i = 0; i < this.blacklist[l].size(); i++) { if
					 * (this.blacklist[l].get(i) != null) { NBTTagCompound
					 * compound = new NBTTagCompound();
					 * InfoHelper.writeFilter(compound,
					 * this.blacklist[l].get(i)); blackList.appendTag(compound);
					 * } }
					 * 
					 * nbt.setTag("white" + l, whiteList); nbt.setTag("black" +
					 * l, blackList);
					 */
				}
			}

		}
		if (type == SyncType.SPECIAL) {
			this.writeList(nbt, whitelist, lastWhitelist, "whitelist");
			this.writeList(nbt, blacklist, lastBlacklist, "blacklist");
		}
	}

	public static void readList(NBTTagCompound nbt, List<ItemFilter>[] filters, String type) {
		for (int f = 0; f < 6; f++) {
			if (nbt.hasKey(type + f + "null")) {
				filters[f] = new ArrayList();
				return;
			}
			NBTTagList list = nbt.getTagList(type + f, 10);
			if (filters[f] == null) {
				filters[f] = new ArrayList();
			}

			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				int slot = compound.getInteger("Slot");
				boolean set = slot < filters[f].size();
				switch (compound.getByte("f")) {
				case 0:
					if (set)
						filters[f].set(slot, Logistics.itemFilters.readFromNBT(compound));
					else
						filters[f].add(slot, Logistics.itemFilters.readFromNBT(compound));
					break;
				case 1:
					long stored = compound.getLong("Stored");
					if (stored != 0) {
						filters[f].set(slot, Logistics.itemFilters.readFromNBT(compound));
					} else {
						filters[f].set(slot, null);

					}
					break;
				case 2:
					filters[f].set(slot, null);
					break;
				}
			}
		}
	}

	public static void writeList(NBTTagCompound nbt, List<ItemFilter>[] filters, List<ItemFilter>[] lastFilters, String type) {
		for (int f = 0; f < 6; f++) {
			if (filters[f] == null) {
				filters[f] = new ArrayList();
			}
			if (lastFilters[f] == null) {
				lastFilters[f] = new ArrayList();
			}
			if (filters[f].size() <= 0 && (!(lastFilters[f].size() <= 0))) {
				nbt.setBoolean(type + f + "null", true);
				lastFilters[f] = new ArrayList();
				return;
			}
			NBTTagList list = new NBTTagList();
			int size = Math.max(filters[f].size(), lastFilters[f].size());
			for (int i = 0; i < size; ++i) {
				ItemFilter current = null;
				ItemFilter last = null;
				if (i < filters[f].size()) {
					current = filters[f].get(i);
				}
				if (i < lastFilters[f].size()) {
					last = lastFilters[f].get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!current.equalFilter(last)) {
							compound.setByte("f", (byte) 0);
							lastFilters[f].set(i, current);
							Logistics.itemFilters.writeToNBT(compound, filters[f].get(i));
						}
					} else {
						compound.setByte("f", (byte) 0);
						lastFilters[f].add(i, current);
						Logistics.itemFilters.writeToNBT(compound, filters[f].get(i));
					}
				} else if (last != null) {
					lastFilters[f].set(i, null);
					compound.setByte("f", (byte) 2);
				}
				if (!compound.hasNoTags()) {
					compound.setInteger("Slot", i);
					list.appendTag(compound);
				}

			}
			if (list.tagCount() != 0) {
				nbt.setTag(type + f, list);
			}
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return side != -1 ? (sideConfigs[side].getInt() == 1 && matchesFilters(item, whitelist[side], blacklist[side])) : true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return sideConfigs[side].getInt() == 2 && matchesFilters(item, whitelist[side], blacklist[side]);
	}

	public static boolean matchesFilters(ItemStack stack, List<ItemFilter> whitelist, List<ItemFilter> blacklist) {
		if (blacklist != null && !blacklist.isEmpty()) {
			for (ItemFilter filter : blacklist) {
				if (filter != null) {

					if (filter.matchesFilter(stack)) {
						return false;
					}

				}
			}
		}
		if (whitelist == null || whitelist.isEmpty()) {
			return true;
		}
		for (ItemFilter filter : whitelist) {
			if (filter != null) {

				if (filter.matchesFilter(stack)) {
					return true;
				}

			}
		}

		return false;
	}

	public static class Filter implements IInventoryFilter {

		public List<ItemFilter> whitelist, blacklist;

		public Filter(List<ItemFilter> whitelist, List<ItemFilter> blacklist) {
			this.whitelist = whitelist;
		}

		@Override
		public boolean matches(ItemStack stack) {
			return matchesFilters(stack, whitelist, blacklist);
		}

	}
}
