package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
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
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.info.types.FluidInventoryInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.ProgressInfo;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class FluidReaderHandler extends TileHandler implements IByteBufTile, IDefaultInteraction {

	public BlockCoords coords;
	public List<StoredFluidStack> fluids;
	public List<StoredFluidStack> lastFluids;

	public FluidStack current;
	public SyncTagType.INT setting = (INT) new SyncTagType.INT(1).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingOrder = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingType = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public StorageSize maxStorage = StorageSize.EMPTY;

	public FluidReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		StorageFluids list = LogisticsAPI.getFluidHelper().getFluids(LogisticsAPI.getCableHelper().getTileConnections(LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite())));
		if (sortingType.getObject() == 0) {
			Collections.sort(list.fluids, new Comparator<StoredFluidStack>() {
				public int compare(StoredFluidStack str1, StoredFluidStack str2) {
					if (str1.stored < str2.stored)
						return sortingOrder.getObject() == 0 ? 1 : -1;
					if (str1.stored == str2.stored)
						return 0;
					return sortingOrder.getObject() == 0 ? -1 : 1;
				}
			});
		} else if (sortingType.getObject() == 1) {
			Collections.sort(list.fluids, new Comparator<StoredFluidStack>() {
				public int compare(StoredFluidStack str1, StoredFluidStack str2) {
					int res = String.CASE_INSENSITIVE_ORDER.compare(str1.getFullStack().getLocalizedName(), str2.getFullStack().getLocalizedName());
					if (res == 0) {
						res = str1.getFullStack().getLocalizedName().compareTo(str2.getFullStack().getLocalizedName());
					}
					return sortingOrder.getObject() == 0 ? res : -res;
				}
			});
		} else if (sortingType.getObject() == 2) {
			Collections.sort(list.fluids, new Comparator<StoredFluidStack>() {
				public int compare(StoredFluidStack str1, StoredFluidStack str2) {
					if (str1.getFullStack().getFluid().getTemperature() < str2.getFullStack().getFluid().getTemperature())
						return sortingOrder.getObject() == 0 ? 1 : -1;
					if (str1.getFullStack().getFluid().getTemperature() == str2.getFullStack().getFluid().getTemperature())
						return 0;
					return sortingOrder.getObject() == 0 ? -1 : 1;
				}
			});
		}

		fluids = list.fluids;
		maxStorage = list.sizing;
	}

	@Override
	public void handleInteraction(Info info, ScreenType type, TileEntity screen, TileEntity reader, EntityPlayer player, int x, int y, int z, BlockInteraction interact, boolean doubleClick) {
		if (player.getHeldItem() != null) {
			emptyFluid(player, reader, player.getHeldItem());
		}
	}

	public void fillItemStack(EntityPlayer player, TileEntity te, StoredFluidStack storedStack) {
		ItemStack heldItem = player.getHeldItem();
		if (heldItem == null || storedStack == null) {
			return;
		}
		if (heldItem.stackSize == 1) {
			List<BlockCoords> network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
			ItemStack simulate = LogisticsAPI.getFluidHelper().fillFluidItemStack(heldItem.copy(), storedStack.copy(), network, ActionType.SIMULATE);
			if (!ItemStack.areItemStacksEqual(simulate, heldItem) || !ItemStack.areItemStackTagsEqual(simulate, heldItem)) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, LogisticsAPI.getFluidHelper().fillFluidItemStack(heldItem, storedStack, network, ActionType.PERFORM));
			}
		} else {
			ItemStack insert = heldItem.copy();
			insert.stackSize = 1;

			List<BlockCoords> network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
			ItemStack simulate = LogisticsAPI.getFluidHelper().fillFluidItemStack(insert.copy(), storedStack.copy(), network, ActionType.SIMULATE);
			if (!ItemStack.areItemStacksEqual(simulate, insert) || !ItemStack.areItemStackTagsEqual(simulate, insert)) {
				ItemStack toAdd = LogisticsAPI.getFluidHelper().fillFluidItemStack(insert, storedStack, network, ActionType.PERFORM);
				player.inventory.decrStackSize(player.inventory.currentItem, 1);
				StoredItemStack add = LogisticsAPI.getItemHelper().addStackToPlayer(new StoredItemStack(toAdd), player, false, ActionType.PERFORM);
			}
		}
	}

	public void emptyFluid(EntityPlayer player, TileEntity te, ItemStack add) {
		ItemStack heldItem = player.getHeldItem();
		if (heldItem == null) {
			return;
		}
		ItemStack insert = heldItem.copy();
		insert.stackSize = 1;
		List<BlockCoords> network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		ItemStack empty = LogisticsAPI.getFluidHelper().drainFluidItemStack(insert.copy(), network, ActionType.PERFORM);
		if (!player.capabilities.isCreativeMode) {
			if (insert.stackSize == heldItem.stackSize) {
				player.inventory.setInventorySlotContents(player.inventory.currentItem, empty);
			} else {
				player.inventory.decrStackSize(player.inventory.currentItem, 1);
				if (empty != null) {
					LogisticsAPI.getItemHelper().addStackToPlayer(new StoredItemStack(empty), player, false, ActionType.PERFORM);
				}
			}
		}
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
			if (current != null) {
				if (fluids != null) {
					for (StoredFluidStack stack : fluids) {
						if (stack.equalStack(current)) {
							return FluidStackInfo.createInfo(stack);
						}
					}
				}
				return FluidStackInfo.createInfo(new StoredFluidStack(current, 0));
			}
			break;
		case 1:
			if (posSlot.getObject() < fluids.size()) {
				return FluidStackInfo.createInfo(fluids.get(posSlot.getObject()));
			}
			break;
		case 2:
			if (fluids != null) {
				return FluidInventoryInfo.createInfo((ArrayList<StoredFluidStack>) fluids);
			}
			break;
		case 3:
			return new ProgressInfo(maxStorage.getStoredFluids(), maxStorage.getMaxFluids(), FontHelper.formatFluidSize(maxStorage.getStoredFluids()) + " / " + FontHelper.formatFluidSize(maxStorage.getMaxFluids()));
		}
		return new StandardInfo((byte) -1, "ITEMREND", " ", "NO DATA");
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		setting.readFromNBT(nbt, type);
		posSlot.readFromNBT(nbt, type);
		if (nbt.hasKey("FluidName")) {
			this.current = FluidStack.loadFluidStackFromNBT(nbt);
		}
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
				this.fluids = new ArrayList();
				return;
			}
			NBTTagList list = nbt.getTagList("Stacks", 10);
			if (this.fluids == null) {
				this.fluids = new ArrayList();
			}
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				int slot = compound.getInteger("Slot");
				boolean set = slot < fluids.size();
				switch (compound.getByte("f")) {
				case 0:
					if (set)
						fluids.set(slot, StoredFluidStack.readFromNBT(compound));
					else
						fluids.add(slot, StoredFluidStack.readFromNBT(compound));
					break;
				case 2:
					if (set)
						fluids.set(slot, null);
					else
						fluids.add(slot, null);
					break;
				}

			}

		}
		if (type == SyncType.SYNC) {
			NBTTagList list = nbt.getTagList("StoredStacks", 10);
			this.fluids = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.fluids.add(StoredFluidStack.readFromNBT(compound));

			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		setting.writeToNBT(nbt, type);
		posSlot.writeToNBT(nbt, type);
		if (current != null) {
			current.writeToNBT(nbt);
		}
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

			if (fluids == null) {
				fluids = new ArrayList();
			}
			if (lastFluids == null) {
				lastFluids = new ArrayList();
			}
			if (this.fluids.size() <= 0 && (!(this.lastFluids.size() <= 0))) {
				nbt.setBoolean("null", true);
				this.lastFluids = new ArrayList();
				return;
			}
			NBTTagList list = new NBTTagList();
			int size = Math.max(this.fluids.size(), this.lastFluids.size());
			for (int i = 0; i < size; ++i) {
				StoredFluidStack current = null;
				StoredFluidStack last = null;
				if (i < this.fluids.size()) {
					current = this.fluids.get(i);
				}
				if (i < this.lastFluids.size()) {
					last = this.lastFluids.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!last.equalStack(current.fluid) || current.stored != last.stored) {
							compound.setByte("f", (byte) 0);
							this.lastFluids.set(i, current);
							StoredFluidStack.writeToNBT(compound, this.fluids.get(i));
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastFluids.add(i, current);
						StoredFluidStack.writeToNBT(compound, this.fluids.get(i));
					}
				} else if (last != null) {
					this.lastFluids.set(i, null);
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
			if (fluids == null) {
				fluids = new ArrayList();
			}
			for (int i = 0; i < this.fluids.size(); i++) {
				if (this.fluids.get(i) != null) {
					NBTTagCompound compound = new NBTTagCompound();
					StoredFluidStack.writeToNBT(compound, this.fluids.get(i));
					list.appendTag(compound);
				}
			}

			nbt.setTag("StoredStacks", list);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(setting, posSlot, sortingOrder, sortingType));
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
		} else if (id == 1) {
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
			posSlot.readFromBuf(buf);
		} else if (id == 3) {
			sortingOrder.readFromBuf(buf);
		} else if (id == 4) {
			sortingType.readFromBuf(buf);
		}
	}

}
