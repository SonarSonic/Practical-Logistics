package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.sync.SyncInt;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.StoredStackInfo;

public class InventoryReaderHandler extends InventoryTileHandler implements IByteBufTile {

	public BlockCoords coords;
	public List<StoredItemStack> stacks = new ArrayList();
	public List<StoredItemStack> lastStacks = new ArrayList();

	public ItemStack current;
	// 0=Stack, 1=Slot (only accepts one input)
	public SyncInt setting = new SyncInt(1);
	public SyncInt targetSlot = new SyncInt(2);
	public SyncInt posSlot = new SyncInt(3);

	public InventoryReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
		super.slots = new ItemStack[1];
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		stacks = LogisticsAPI.getItemHelper().getStackList(LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()));
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

		switch (setting.getInt()) {
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
			StoredItemStack stack = LogisticsAPI.getItemHelper().getStack(LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()), targetSlot.getInt());
			if (stack != null) {
				return StoredStackInfo.createInfo(stack);
			}
			return new StandardInfo((byte) -1, "ITEMREND", " ", " ");

		case 2:
			if (posSlot.getInt() < stacks.size()) {
				return StoredStackInfo.createInfo(stacks.get(posSlot.getInt()));
			}
			break;
		case 3:
			if (stacks != null) {
				return InventoryInfo.createInfo(stacks);
			}
			break;
		}
		return new StandardInfo((byte) -1, "ITEMREND", " ", "NO DATA");
	}
	public StoredItemStack extractItem(TileEntity te, StoredItemStack stack) {
		if (stack == null || stack.stored == 0) {
			return null;
		}
		int extractSize = (int) Math.min(stack.getItemStack().getMaxStackSize(), stack.stored);
		StoredItemStack remainder = LogisticsAPI.getItemHelper().removeItems(stack, LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()));
		StoredItemStack storedStack = null;
		if (remainder == null || remainder.stored == 0) {
			storedStack = new StoredItemStack(stack.getItemStack(), extractSize);
		} else {
			storedStack = new StoredItemStack(stack.getItemStack(), extractSize - remainder.stored);
		}
		return storedStack;
	}

	public void insertItem(EntityPlayer player, TileEntity te, ItemStack add) {
		StoredItemStack stack = LogisticsAPI.getItemHelper().addItems(new StoredItemStack(add), LogisticsAPI.getCableHelper().getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()));
		
		if (stack == null || stack.stored == 0) {
			add = null;
		} else {
			add.stackSize = (int) stack.stored;
		}
		if (!ItemStack.areItemStacksEqual(add, player.getHeldItem())) {
			player.inventory.setInventorySlotContents(player.inventory.currentItem, add);
		}
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
					if (stored != 0) {
						stacks.set(slot, new StoredItemStack(stacks.get(slot).item, stored));
					} else {
						stacks.set(slot, null);
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
		setting.writeToNBT(nbt, type);
		targetSlot.writeToNBT(nbt, type);
		posSlot.writeToNBT(nbt, type);
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
						if (!ItemStack.areItemStacksEqual(last.item, current.item)) {
							compound.setByte("f", (byte) 0);
							this.lastStacks.set(i, current);
							StoredItemStack.writeToNBT(compound, this.stacks.get(i));

						} else if (last.stored != current.stored) {
							compound.setByte("f", (byte) 1);
							this.lastStacks.set(i, current);
							StoredItemStack.writeToNBT(compound, this.stacks.get(i));
							compound.setLong("Stored", current.stored);
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

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			// setting.writeToBuf(buf);
		}
		if (id == 1) {
			targetSlot.writeToBuf(buf);
		}
		if (id == 2) {
			posSlot.writeToBuf(buf);
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			// setting.readFromBuf(buf);
			// System.out.print(setting.getInt());
			if (setting.getInt() == 3) {
				setting.setInt(0);
			} else {
				setting.increaseBy(1);
			}
		}
		if (id == 1) {
			targetSlot.readFromBuf(buf);
		}
		if (id == 2) {
			posSlot.readFromBuf(buf);
		}
	}

}
