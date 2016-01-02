package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityInventory;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.PacketByteBuf;
import sonar.core.network.SonarPackets;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.StoredStackInfo;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityInventoryReader extends TileEntityInventory implements IDataConnection, IByteBufTile {

	// client
	public BlockCoords coords;
	public List<StoredItemStack> stacks;
	public List<StoredItemStack> lastStacks;

	public ItemStack current;

	public TileEntityInventoryReader() {
		this.slots = new ItemStack[1];
	}

	@Override
	public Info currentInfo() {
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
		return new StandardInfo((byte) -1, "ITEMREND", " ", "NO DATA");
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(this.getBlockMetadata())) || dir.equals(ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite());
	}

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		updateData(ForgeDirection.getOrientation(this.getBlockMetadata()));
	}

	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
		}
	}

	@Override
	public void updateData(ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(this, dir.getOpposite());
		target = FMPHelper.checkObject(target);
		if (target == null) {
			this.setCoords(null);
		} else {
			if (target instanceof TileEntityBlockNode ||target instanceof TileEntityEntityNode) {
				TileEntity node = (TileEntity) target;
				this.setCoords(new BlockCoords(node, node.getWorldObj().provider.dimensionId));
			} else {
				this.setCoords(null);
			}
		}

		if (this.coords != null) {
			TileEntity tile = this.coords.getTileEntity();
			if (tile != null && tile instanceof TileEntityBlockNode) {
				TileEntityBlockNode node = (TileEntityBlockNode) target;
				stacks = InfoHelper.getTileInventory(node);
				CableHelper.updateAdjacentCoord(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, ForgeDirection.getOrientation(this.getBlockMetadata()));

			}else if (tile != null && tile instanceof TileEntityEntityNode) {
				TileEntityEntityNode node = (TileEntityEntityNode) target;
				stacks = InfoHelper.getEntityInventory(node);
				CableHelper.updateAdjacentCoord(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, ForgeDirection.getOrientation(this.getBlockMetadata()));

			} else {
				stacks = null;
			}
		} else {
			stacks = null;
		}
	}

	public void sendAvailableData(EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			SonarPackets.network.sendTo(new PacketByteBuf(this, 0), (EntityPlayerMP) player);
		}
	}

	public boolean maxRender() {
		return true;
	}

	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip) {

		return currenttip;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			if (nbt.hasKey("coords")) {
				if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
					coords = BlockCoords.readFromNBT(nbt.getCompoundTag("coords"));
				} else {
					coords = null;
				}
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

	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			NBTTagList list = new NBTTagList();
			NBTTagCompound nbt = new NBTTagCompound();
				if (stacks == null) {
					stacks = new ArrayList();
				}
				for (int i = 0; i < this.stacks.size(); i++) {
					if (this.stacks.get(i) != null) {
						NBTTagCompound compound = new NBTTagCompound();
						compound.setByte("Slot", (byte) i);
						StoredItemStack.writeToNBT(compound, this.stacks.get(i));
						list.appendTag(compound);
					}
				}

				nbt.setTag("StoredStacks", list);

				ByteBufUtils.writeTag(buf, nbt);

				this.lastStacks = stacks;
			
		}
		if (id == 1) {
			if (current != null) {
				buf.writeBoolean(true);
				current.stackSize = 1;
				ByteBufUtils.writeItemStack(buf, current);
			} else {
				buf.writeBoolean(false);
			}
		}
	}

	@Override
	public void readPacket(ByteBuf tag, int id) {
		if (id == 0) {
			NBTTagCompound nbt = ByteBufUtils.readTag(tag);
			NBTTagList list = nbt.getTagList("StoredStacks", 10);
			this.stacks = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.stacks.add(StoredItemStack.readFromNBT(compound));

			}
		}
		if (id == 1) {
			if (tag.readBoolean()) {
				slots[0] = ByteBufUtils.readItemStack(tag);
			} else {
				this.slots[0] = null;
			}
		}
	}
}
