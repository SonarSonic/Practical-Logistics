package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
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
import sonar.core.utils.helpers.SonarHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.ItemFilter;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.info.types.StoredStackInfo;

public class ItemRouterHandler extends InventoryTileHandler implements ISidedInventory {

	// 0=nothing, 1=input, 2=output
	public SyncInt[] sideConfigs = new SyncInt[6];
	public BlockCoords[] coords = new BlockCoords[6];
	public IInventory[] inventories = new IInventory[6];
	public List<ItemFilter>[] lastFilters = new List[6];
	public List<ItemFilter>[] clientFilters = new List[6];

	public ItemRouterHandler(boolean isMultipart) {
		super(isMultipart);
		super.slots = new ItemStack[9];
		for (int i = 0; i < 6; i++) {
			sideConfigs[i] = new SyncInt(i);
			lastFilters[i] = new ArrayList();
			clientFilters[i] = new ArrayList();
		}
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		updateConnections(te);
		updateInventories(te);
		for (int i = 0; i < slots.length; i++) {
			for (int c = 0; c < 6; c++) {
				int config = this.sideConfigs[c].getInt();
				if ((config == 2 && slots[i] != null) || config == 1) {
					if (this.coords[c] != null) {
						TileEntity tile = this.coords[c].getTileEntity();
						if (tile != null && tile instanceof TileEntityBlockNode) {
							TileEntityBlockNode node = (TileEntityBlockNode) tile;
							ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
							TileEntity target = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);
							if (target != null && target instanceof IInventory) {
								if (config == 2) {
									slots[i] = InventoryHelper.addItems(target, slots[i], dir.ordinal(), null);
								} else {
									InventoryHelper.extractItems(target, te, dir.getOpposite().ordinal(), dir.ordinal(), null);
								}
							}

						}
					} else {
						ForgeDirection dir = ForgeDirection.getOrientation(c);
						TileEntity target = te.getWorldObj().getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);
						if (target != null && target instanceof IInventory) {
							if (config == 2) {
								slots[i] = InventoryHelper.addItems(target, slots[i], dir.ordinal(), null);
							} else {
								InventoryHelper.extractItems(target, te, dir.getOpposite().ordinal(), dir.ordinal(), null);
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

	public void updateInventories(TileEntity te) {
		for (int i = 0; i < 6; i++) {
			int config = sideConfigs[i].getInt();
			if (config == 2) {
				if (this.coords[i] != null) {
					TileEntity tile = this.coords[i].getTileEntity();
					if (tile != null && tile instanceof TileEntityBlockNode) {
						TileEntityBlockNode node = (TileEntityBlockNode) tile;
						ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
						TileEntity target = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);
						if (target != null) {
							if (target instanceof IInventory) {
								inventories[i] = (IInventory) target;
							}
						}
					}
				} else {
					ForgeDirection dir = ForgeDirection.getOrientation(i);
					TileEntity target = te.getWorldObj().getTileEntity(te.xCoord + dir.offsetX, te.yCoord + dir.offsetY, te.zCoord + dir.offsetZ);
					if (target != null) {
						if (target instanceof IInventory) {
							inventories[i] = (IInventory) target;
						}
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
			}
		}
		if (type == SyncType.SPECIAL) {
			for (int f = 0; f < 6; f++) {
				if (nbt.hasKey("null")) {
					this.clientFilters[f] = new ArrayList();
					return;
				}
				NBTTagList list = nbt.getTagList("Info", 10);
				if (this.clientFilters[f] == null) {
					this.clientFilters[f] = new ArrayList();
				}
				for (int i = 0; i < list.tagCount(); i++) {
					NBTTagCompound compound = list.getCompoundTagAt(i);
					int slot = compound.getInteger("Slot");
					boolean set = slot < clientFilters[f].size();
					switch (compound.getByte("f")) {
					case 0:
						if (set)
							clientFilters[f].set(slot, InfoHelper.readFilter(compound));
						else
							clientFilters[f].add(slot, InfoHelper.readFilter(compound));
						break;
					case 1:
						long stored = compound.getLong("Stored");
						if (stored != 0) {
							clientFilters[f].set(slot, InfoHelper.readFilter(compound));
						} else {
							clientFilters[f].set(slot, null);

						}
						break;
					case 2:
						clientFilters[f].set(slot, null);
						break;
					}
				}
			}
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

			}
		}
		if (type == SyncType.SPECIAL) {

			for (int f = 0; f < 6; f++) {
				if (clientFilters[f] == null) {
					clientFilters[f] = new ArrayList();
				}
				if (lastFilters[f] == null) {
					lastFilters[f] = new ArrayList();
				}
				if (this.clientFilters[f].size() <= 0 && (!(this.lastFilters[f].size() <= 0))) {
					nbt.setBoolean("null", true);
					this.lastFilters[f] = new ArrayList();
					return;
				}
				NBTTagList list = new NBTTagList();
				int size = Math.max(this.clientFilters[f].size(), this.lastFilters[f].size());
				for (int i = 0; i < size; ++i) {
					ItemFilter current = null;
					ItemFilter last = null;
					if (i < this.clientFilters[f].size()) {
						current = this.clientFilters[f].get(i);
					}
					if (i < this.lastFilters[f].size()) {
						last = this.lastFilters[f].get(i);
					}
					NBTTagCompound compound = new NBTTagCompound();
					if (current != null) {
						if (last != null) {
							if (!current.equalFilter(last)) {
								compound.setByte("f", (byte) 0);
								this.lastFilters[f].set(i, current);
								InfoHelper.writeFilter(compound, this.clientFilters[f].get(i));
							}
						} else {
							compound.setByte("f", (byte) 0);
							this.lastFilters[f].add(i, current);
							InfoHelper.writeFilter(compound, this.clientFilters[f].get(i));
						}
					} else if (last != null) {
						this.lastFilters[f].set(i, null);
						compound.setByte("f", (byte) 2);
					}
					if (!compound.hasNoTags()) {
						compound.setInteger("Slot", i);
						list.appendTag(compound);
					}

				}
				if (list.tagCount() != 0) {
					nbt.setTag("Info", list);
				}
			}
		}
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		return sideConfigs[side].getInt() != 0 ? new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 } : null;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, int side) {
		return sideConfigs[side].getInt() == 1;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, int side) {
		return sideConfigs[side].getInt() == 2;
	}
}
