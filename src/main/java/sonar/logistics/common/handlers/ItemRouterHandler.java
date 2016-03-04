package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.InventoryHelper;
import sonar.core.utils.helpers.InventoryHelper.IInventoryFilter;
import sonar.core.utils.helpers.NBTHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.Logistics;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.info.filters.items.ItemStackFilter;
import sonar.logistics.info.filters.items.OreDictionaryFilter;

import com.google.common.collect.Lists;

public class ItemRouterHandler extends InventoryTileHandler implements ISidedInventory, IByteBufTile {

	// 0=nothing, 1=input, 2=output
	public SyncTagType.INT[] sideConfigs = new SyncTagType.INT[6];
	public SyncTagType.INT listType = new SyncTagType.INT(7);
	public SyncTagType.INT side = new SyncTagType.INT(8);
	public SyncTagType.INT filterPos = new SyncTagType.INT(9);

	public List<BlockCoords>[] coords = new List[6];
	public List<ItemFilter>[] lastWhitelist = new List[6];
	public List<ItemFilter>[] whitelist = new List[6];

	public List<ItemFilter>[] lastBlacklist = new List[6];
	public List<ItemFilter>[] blacklist = new List[6];

	public int update = 0;
	public int updateTime = 20;

	public int clientClick = -1;
	public int editStack = -1, editOre = -1;
	public ItemStackFilter clientStackFilter = new ItemStackFilter();
	public OreDictionaryFilter clientOreFilter = new OreDictionaryFilter();

	public ItemRouterHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
		super.slots = new ItemStack[9];
		for (int i = 0; i < 6; i++) {
			sideConfigs[i] = new SyncTagType.INT(i + 1);
			lastWhitelist[i] = new ArrayList();
			whitelist[i] = new ArrayList();
			lastBlacklist[i] = new ArrayList();
			blacklist[i] = new ArrayList();
			coords[i] = new ArrayList();
		}
	}

	public List<ItemFilter> getFilters() {
		return listType.getObject() == 0 ? whitelist[side.getObject()] : blacklist[side.getObject()];

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
			int config = this.sideConfigs[update].getObject();
			if (config != 0) {
				TileEntity target = null;
				ForgeDirection dir = null;
				if (this.coords[update] != null) {
					for (BlockCoords coords : this.coords[update]) {
						TileEntity tile = coords.getTileEntity();
						if (tile != null && tile instanceof TileEntityBlockNode) {
							TileEntityBlockNode node = (TileEntityBlockNode) tile;
							dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
							target = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);

						} else {
							dir = ForgeDirection.getOrientation(update);
							target = te.getWorldObj().getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);
						}
						routeInventory(te, config, target, dir);
					}
				}
			}

		}
	}

	public void routeInventory(TileEntity te, int config, TileEntity target, ForgeDirection dir) {
		if (target != null && dir != null && target instanceof IInventory) {
			if (config == 1) {
				InventoryHelper.extractItems(target, te, ForgeDirection.OPPOSITES[dir.ordinal()], dir.ordinal(), null);
			} else {
				for (int i = 0; i < slots.length; i++) {
					if (slots[i] != null && matchesFilters(slots[i], whitelist[update], blacklist[update])) {
						slots[i] = InventoryHelper.addItems(target, slots[i], ForgeDirection.OPPOSITES[dir.ordinal()], null);
					}
				}
			}
		}
	}

	public void addItemFilter(ItemFilter filter) {
		if (filter == null) {
			return;
		}
		this.getFilters().add(filter);
	}

	public void replaceItemFilter(int i, ItemFilter filter) {
		List<ItemFilter> filters = this.getFilters();
		if (filter == null || filters == null || filters.isEmpty() || (i > filters.size()) || i == -1) {
			return;
		}
		if (filters.get(i).getID() == filter.getID()) {
			if (!filter.getFilters().isEmpty()) {
				this.getFilters().set(i, filter);
			} else {
				this.getFilters().remove(i);
			}

		}
	}

	public void resetClientStackFilter() {
		this.clientStackFilter = new ItemStackFilter();
		if (editStack != -1) {
			List<ItemFilter> filters = this.getFilters();
			if (editStack < filters.size() && filters.get(editStack) != null) {
				this.clientStackFilter = (ItemStackFilter) filters.get(editStack);
			}
		}

	}

	public void resetClientOreFilter() {
		this.clientOreFilter = new OreDictionaryFilter();
		if (editOre != -1) {
			List<ItemFilter> filters = this.getFilters();
			if (editOre < filters.size() && filters.get(editOre) != null) {
				this.clientStackFilter = (ItemStackFilter) filters.get(editOre);
			}
		}
	}

	public void updateConnections(TileEntity te) {
		for (int i = 0; i < 6; i++) {
			int config = sideConfigs[i].getObject();
			if (config != 0) {
				List<BlockCoords> connections = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(i));
				if (!connections.isEmpty()) {
					this.coords[i] = connections;
				}else{
					this.coords[i]=new ArrayList();
					this.coords[i].add(BlockCoords.translateCoords(new BlockCoords(te), ForgeDirection.getOrientation(i)));
				}
			}
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return sideConfigs[dir.ordinal()].getObject() != 0;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.PACKET || type == SyncType.DROP) {
			for (int l = 0; l < 6; l++) {

				whitelist[l] = (List<ItemFilter>) NBTHelper.readNBTObjectList("white" + l, nbt, Logistics.itemFilters);
				blacklist[l] = (List<ItemFilter>) NBTHelper.readNBTObjectList("black" + l, nbt, Logistics.itemFilters);
			}
		}
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTTagList sideList = nbt.getTagList("Sides", 10);
			for (int i = 0; i < 6; i++) {
				NBTTagCompound compound = sideList.getCompoundTagAt(i);
				sideConfigs[i].readFromNBT(compound, type);
			}
		}
		if (type == SyncType.SPECIAL) {
			for (int l = 0; l < 6; l++) {
				NBTHelper.readSyncedNBTObjectList("white" + l, nbt, Logistics.itemFilters, whitelist[l]);
				NBTHelper.readSyncedNBTObjectList("black" + l, nbt, Logistics.itemFilters, blacklist[l]);
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.PACKET || type == SyncType.DROP) {
			for (int l = 0; l < 6; l++) {
				NBTHelper.writeNBTObjectList("white" + l, nbt, whitelist[l]);
				NBTHelper.writeNBTObjectList("black" + l, nbt, blacklist[l]);
			}
		}

		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTTagList sideList = new NBTTagList();
			for (int i = 0; i < 6; i++) {
				NBTTagCompound compound = new NBTTagCompound();
				sideConfigs[i].writeToNBT(compound, type);
				sideList.appendTag(compound);
			}
			nbt.setTag("Sides", sideList);
		}
		if (type == SyncType.SPECIAL) {
			for (int l = 0; l < 6; l++) {
				NBTHelper.writeSyncedNBTObjectList("white" + l, nbt, Logistics.itemFilters, whitelist[l], lastWhitelist[l]);
				NBTHelper.writeSyncedNBTObjectList("black" + l, nbt, Logistics.itemFilters, blacklist[l], lastBlacklist[l]);
			}
		}
	}
	
	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(listType, side, filterPos));
	}
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return side != -1 ? (sideConfigs[side].getObject() == 1 && matchesFilters(item, whitelist[side], blacklist[side])) : true;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return sideConfigs[side].getObject() == 2 && matchesFilters(item, whitelist[side], blacklist[side]);
	}

	public static boolean matchesFilters(ItemStack stack, List<ItemFilter> whitelist, List<ItemFilter> blacklist) {
		if (stack == null) {
			return false;
		}
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

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -2:
			writePacket(buf, 9);
			clientOreFilter.writeToBuf(buf);
			break;
		case -1:
			writePacket(buf, 9);
			clientStackFilter.writeToBuf(buf);
			break;
		case 0:
			if (side.getObject() + 1 < 6) {
				side.increaseBy(1);
			} else {
				side.setObject(0);
			}
			buf.writeInt(side.getObject());
			break;
		case 1:
			if (sideConfigs[side.getObject()].getObject() < 2) {
				sideConfigs[side.getObject()].increaseBy(1);
			} else {
				sideConfigs[side.getObject()].setObject(0);
			}
			buf.writeInt(sideConfigs[side.getObject()].getObject());
			break;
		case 2:
			if (listType.getObject() == 0) {
				listType.setObject(1);
			} else {
				listType.setObject(0);
			}
			buf.writeInt(listType.getObject());
			break;
		case 5:

			break;
		case 6:

			break;
		case 7:

			break;
		case 8:
			boolean clicked = false;
			if (listType.getObject() == 0 && clientClick != -1) {
				if (clientClick < whitelist[side.getObject()].size()) {
					if (whitelist[side.getObject()].get(clientClick) != null) {
						buf.writeInt(clientClick);
						clicked = true;
					}
				}
			} else if (listType.getObject() == 1 && clientClick != -1) {
				if (clientClick < blacklist[side.getObject()].size()) {
					if (blacklist[side.getObject()].get(clientClick) != null) {
						buf.writeInt(clientClick);
						clicked = true;
					}
				}
			}
			if (!clicked) {
				buf.writeInt(-1);
			}
			break;
		case 9:
			writePacket(buf, 8);
			buf.writeInt(editStack);
			buf.writeInt(editOre);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case -2:
			readPacket(buf, 9);
			clientOreFilter = new OreDictionaryFilter();
			clientOreFilter.readFromBuf(buf);
			if (clientOreFilter != null) {
				if (editOre == -1 && clientOreFilter.oreDict != null && !clientOreFilter.oreDict.isEmpty())
					addItemFilter(clientOreFilter);
				else
					replaceItemFilter(editOre, clientOreFilter);
			}
			editOre = -1;
			break;
		case -1:
			readPacket(buf, 9);
			clientStackFilter = new ItemStackFilter();
			clientStackFilter.readFromBuf(buf);
			if (clientStackFilter != null) {
				if (editStack == -1 && clientStackFilter.filters[0] != null) {
					addItemFilter(clientStackFilter);
				} else {
					replaceItemFilter(editStack, clientStackFilter);
				}

			}
			editStack = -1;
			break;
		case 0:
			side.setObject(buf.readInt());
			filterPos.setObject(-1);
			break;
		case 1:
			sideConfigs[side.getObject()].setObject(buf.readInt());
			SonarCore.sendFullSyncAround(tile, 64);
			break;
		case 2:
			listType.setObject(buf.readInt());
			filterPos.setObject(-1);
			break;
		case 5:
			if (filterPos.getObject() != -1 && filterPos.getObject() != 0) {
				if (listType.getObject() == 0) {
					if (filterPos.getObject() - 1 < whitelist[side.getObject()].size()) {
						Collections.swap(whitelist[side.getObject()], filterPos.getObject(), filterPos.getObject() - 1);
						filterPos.setObject(filterPos.getObject() - 1);
					}
				} else {
					if (filterPos.getObject() - 1 < blacklist[side.getObject()].size()) {
						Collections.swap(whitelist[side.getObject()], filterPos.getObject(), filterPos.getObject() - 1);
						filterPos.setObject(filterPos.getObject() - 1);
					}
				}
			}
			break;
		case 6:
			if (filterPos.getObject() != -1) {
				if (listType.getObject() == 0) {
					if (filterPos.getObject() + 1 < whitelist[side.getObject()].size()) {
						Collections.swap(whitelist[side.getObject()], filterPos.getObject(), filterPos.getObject() + 1);
						filterPos.setObject(filterPos.getObject() + 1);
					}
				} else {
					if (filterPos.getObject() + 1 < blacklist[side.getObject()].size()) {
						Collections.swap(whitelist[side.getObject()], filterPos.getObject(), filterPos.getObject() + 1);
						filterPos.setObject(filterPos.getObject() + 1);
					}
				}
			}
			break;
		case 7:
			if (filterPos.getObject() != -1) {
				if (listType.getObject() == 0) {
					if (filterPos.getObject() < whitelist[side.getObject()].size())
						whitelist[side.getObject()].remove(filterPos.getObject());
				} else {
					if (filterPos.getObject() < blacklist[side.getObject()].size())
						blacklist[side.getObject()].remove(filterPos.getObject());
				}
				filterPos.setObject(-1);
			}
			break;
		case 8:
			filterPos.setObject(buf.readInt());
			break;
		case 9:
			readPacket(buf, 8);
			editStack = buf.readInt();
			editOre = buf.readInt();
			break;
		}
	}
}
