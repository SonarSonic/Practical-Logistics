package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import codechicken.multipart.TMultiPart;
import codechicken.multipart.TileMultipart;
import codechicken.multipart.minecraft.McMetaPart;
import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.integration.SonarAPI;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.SonarTilePart;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.inventory.StoredItemStack;
import sonar.core.network.PacketByteBuf;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.connecting.IInfoReader;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.StoredStackInfo;
import sonar.logistics.network.packets.PacketProviders;

public class InventoryReaderHandler extends InventoryTileHandler implements IByteBufTile {

	public BlockCoords coords;
	public List<StoredItemStack> stacks;
	public List<StoredItemStack> lastStacks;

	public ItemStack current;

	public InventoryReaderHandler(boolean isMultipart) {
		super(isMultipart);
		super.slots = new ItemStack[1];
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		updateData(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
	}

	public void updateData(TileEntity te, ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(te, dir.getOpposite());
		target = FMPHelper.checkObject(target);
		if (target == null) {
			this.setCoords(null);
		} else {
			if (target instanceof TileEntityBlockNode || target instanceof TileEntityEntityNode) {
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
				CableHelper.updateAdjacentCoord(te, new BlockCoords(te.xCoord, te.yCoord, te.zCoord), false, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));

			} else if (tile != null && tile instanceof TileEntityEntityNode) {
				TileEntityEntityNode node = (TileEntityEntityNode) target;
				stacks = InfoHelper.getEntityInventory(node);
				CableHelper.updateAdjacentCoord(te, new BlockCoords(te.xCoord, te.yCoord, te.zCoord), false, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));

			} else {
				stacks = null;
			}
		} else {
			stacks = null;
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

	public void sendAvailableData(TileEntity te, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			SonarCore.network.sendTo(new PacketByteBuf(this, te.xCoord, te.yCoord,te.zCoord, 0), (EntityPlayerMP) player);
		}
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
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			NBTTagCompound nbt = ByteBufUtils.readTag(buf);
			NBTTagList list = nbt.getTagList("StoredStacks", 10);
			this.stacks = new ArrayList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				this.stacks.add(StoredItemStack.readFromNBT(compound));

			}
		}
		if (id == 1) {
			if (buf.readBoolean()) {
				slots[0] = ByteBufUtils.readItemStack(buf);
			} else {
				this.slots[0] = null;
			}
		}
	}

}
