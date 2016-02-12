package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.SonarCore;
import sonar.core.energy.StoredEnergyStack;
import sonar.core.fluid.StoredFluidStack;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.PacketTileSync;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.providers.EnergyHandler;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.EnergyHelper;
import sonar.logistics.helpers.FluidHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.CategoryInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.ProgressInfo;

public class EnergyReaderHandler extends TileHandler {

	public BlockCoords coords;
	public List<StoredEnergyStack> stacks;
	public List<StoredEnergyStack> lastStacks;

	public EnergyReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		List<BlockCoords> coords = CableHelper.getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		stacks = EnergyHelper.getEnergy(coords);
		List<StoredEnergyStack> energyList = new ArrayList();
		List<EnergyHandler> handlers = Logistics.energyProviders.getObjects();

		for (BlockCoords coord : coords) {
			boolean completed = false;
			for (EnergyHandler handler : handlers) {
				if (!completed) {
					TileEntity target = coord.getTileEntity();
					if (target != null && target instanceof TileEntityBlockNode) {
						TileEntityBlockNode node = (TileEntityBlockNode) target;
						ForgeDirection dir = ForgeDirection.getOrientation(SonarHelper.invertMetadata(node.getBlockMetadata())).getOpposite();
						TileEntity energyTile = node.getWorldObj().getTileEntity(node.xCoord + dir.offsetX, node.yCoord + dir.offsetY, node.zCoord + dir.offsetZ);

						// if (energyTile != null &&
						// handler.canProvideInfo(energyTile, dir)) {
						//handler.removeEnergy(10000000, energyTile, dir);
						//handler.addEnergy(10000001, energyTile, dir);
						completed = true;

						// }
					}
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
		if (stacks != null && !stacks.isEmpty() && stacks.get(0) != null) {
			StoredEnergyStack energy = stacks.get(0);
			return new ProgressInfo((long) energy.stored, (long) energy.capacity, (long) energy.stored + " RF");
		}
		/*
		 * if (current != null && current.getFluid() != null) { if (stacks !=
		 * null) { for (StoredFluidStack stack : stacks) { if (stack != null &&
		 * stack.fluid.isFluidEqual(current)) { return
		 * FluidStackInfo.createInfo(stack); } } } return
		 * FluidStackInfo.createInfo(new StoredFluidStack(current, 0, 0));
		 * 
		 * }
		 */
		return new StandardInfo((byte) -1, "ITEMREND", " ", "NO DATA");
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);

		// if (nbt.hasKey("FluidName")) {
		// this.current = FluidStack.loadFluidStackFromNBT(nbt);
		// }
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
						stacks.set(slot, StoredEnergyStack.readFromNBT(compound));
					else
						stacks.add(slot, StoredEnergyStack.readFromNBT(compound));
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
				this.stacks.add(StoredEnergyStack.readFromNBT(compound));

			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		// if (current != null) {
		// current.writeToNBT(nbt);
		// }
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
				StoredEnergyStack current = null;
				StoredEnergyStack last = null;
				if (i < this.stacks.size()) {
					current = this.stacks.get(i);
				}
				if (i < this.lastStacks.size()) {
					last = this.lastStacks.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!last.equalEnergyType(current) || current.stored != last.stored || current.capacity != last.capacity) {
							compound.setByte("f", (byte) 0);
							this.lastStacks.set(i, current);
							StoredEnergyStack.writeToNBT(compound, this.stacks.get(i));
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastStacks.add(i, current);
						StoredEnergyStack.writeToNBT(compound, this.stacks.get(i));
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
					StoredEnergyStack.writeToNBT(compound, this.stacks.get(i));
					list.appendTag(compound);
				}
			}

			nbt.setTag("StoredStacks", list);
		}
	}
}