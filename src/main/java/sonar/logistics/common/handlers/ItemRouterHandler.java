package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.calculator.mod.Calculator;
import sonar.core.SonarCore;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.network.sync.SyncInt;
import sonar.core.network.utils.IByteBufTile;
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

public class ItemRouterHandler extends InventoryTileHandler implements ISidedInventory, IByteBufTile {

	// 0=nothing, 1=input, 2=output
	public SyncInt[] sideConfigs = new SyncInt[6];
	public SyncInt listType = new SyncInt(7);
	public SyncInt side = new SyncInt(8);
	public SyncInt filterPos = new SyncInt(9);

	public BlockCoords[] coords = new BlockCoords[6];
	public List<ItemFilter>[] lastWhitelist = new List[6];
	public List<ItemFilter>[] whitelist = new List[6];

	public List<ItemFilter>[] lastBlacklist = new List[6];
	public List<ItemFilter>[] blacklist = new List[6];

	public int update = 0;
	public int updateTime = 20;

	public int clientClick = -1;
	public ItemStackFilter clientStackFilter = new ItemStackFilter();

	public ItemRouterHandler(boolean isMultipart) {
		super(isMultipart);
		super.slots = new ItemStack[9];
		for (int i = 0; i < 6; i++) {
			sideConfigs[i] = new SyncInt(i + 1);
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
			listType.readFromNBT(nbt, type);
			side.readFromNBT(nbt, type);
			filterPos.readFromNBT(nbt, type);
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
					whitelist[l] = (List<ItemFilter>) NBTHelper.readNBTObjectList("white" + l, nbt, Logistics.itemFilters);
					blacklist[l] = (List<ItemFilter>) NBTHelper.readNBTObjectList("black" + l, nbt, Logistics.itemFilters);
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
			listType.writeToNBT(nbt, type);
			side.writeToNBT(nbt, type);
			filterPos.writeToNBT(nbt, type);
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
						if ((true)) {
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
		if (id == -1) {
			clientStackFilter.writeToBuf(buf);
		}
		if (id == 0) {
			if (side.getInt() + 1 < 6) {
				side.increaseBy(1);
			} else {
				side.setInt(0);
			}
			buf.writeInt(side.getInt());
		}
		if (id == 1) {
			if (sideConfigs[side.getInt()].getInt() < 2) {
				sideConfigs[side.getInt()].increaseBy(1);
			} else {
				sideConfigs[side.getInt()].setInt(0);
			}
			buf.writeInt(sideConfigs[side.getInt()].getInt());
		}
		if (id == 2) {
			if (listType.getInt() == 0) {
				listType.setInt(1);
			} else {
				listType.setInt(0);
			}
			buf.writeInt(listType.getInt());
		}

		if (id == 3) {
			String name = Minecraft.getMinecraft().thePlayer.getGameProfile().getName();
			ByteBufUtils.writeUTF8String(buf, name);
		}
		if (id == 8) {
			if (listType.getInt() == 0) {
				if (clientClick < whitelist[side.getInt()].size()) {
					if (whitelist[side.getInt()].get(clientClick) != null) {
						buf.writeInt(clientClick);
					}
				}
			} else {
				if (clientClick < blacklist[side.getInt()].size()) {
					if (blacklist[side.getInt()].get(clientClick) != null) {
						buf.writeInt(clientClick);
					}
				}
			}
			buf.writeInt(-1);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == -1) {
			clientStackFilter = new ItemStackFilter();
			clientStackFilter.readFromBuf(buf);
			if (clientStackFilter != null && clientStackFilter.filters[0] != null) {
				if (listType.getInt() == 0) {
					whitelist[side.getInt()].add(clientStackFilter);
				} else {
					blacklist[side.getInt()].add(clientStackFilter);
				}
			}
			filterPos.setInt(-1);
		}
		if (id == 0) {
			side.setInt(buf.readInt());
			filterPos.setInt(-1);
		}
		if (id == 1) {
			sideConfigs[side.getInt()].setInt(buf.readInt());
		}
		if (id == 2) {
			listType.setInt(buf.readInt());
			filterPos.setInt(-1);
		}
		if (id == 3) {
			/*
			 * filt.ignoreDamage = true; if (listType.getInt() == 0) {
			 * whitelist[side.getInt()].add(filt); } else {
			 * blacklist[side.getInt()].add(filt); }
			 */
		}
		if (id == 5) {
			if (filterPos.getInt() != -1 && filterPos.getInt() != 0) {
				if (listType.getInt() == 0) {
					if (filterPos.getInt() - 1 < whitelist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() - 1);
						filterPos.setInt(filterPos.getInt() - 1);
					}
				} else {
					if (filterPos.getInt() - 1 < blacklist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() - 1);
						filterPos.setInt(filterPos.getInt() - 1);
					}
				}
			}
		}
		if (id == 6) {
			if (filterPos.getInt() != -1) {
				if (listType.getInt() == 0) {
					if (filterPos.getInt() + 1 < whitelist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() + 1);
						filterPos.setInt(filterPos.getInt() + 1);
					}
				} else {
					if (filterPos.getInt() + 1 < blacklist[side.getInt()].size()) {
						Collections.swap(whitelist[side.getInt()], filterPos.getInt(), filterPos.getInt() + 1);
						filterPos.setInt(filterPos.getInt() + 1);
					}
				}
			}
		}

		if (id == 7) {
			if (filterPos.getInt() != -1) {
				if (listType.getInt() == 0) {
					if (filterPos.getInt() < whitelist[side.getInt()].size())
						whitelist[side.getInt()].remove(filterPos.getInt());
				} else {
					if (filterPos.getInt() < blacklist[side.getInt()].size())
						blacklist[side.getInt()].remove(filterPos.getInt());
				}
				filterPos.setInt(-1);
			}
		}
		if (id == 8) {
			filterPos.setInt(buf.readInt());
		}
	}
}
