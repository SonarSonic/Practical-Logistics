package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.Info;
import sonar.logistics.api.info.StandardInfo;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyReaderHandler extends TileHandler {

	public BlockCoords coords;
	public List<StoredEnergyInfo> stacks;
	public List<StoredEnergyInfo> lastStacks;

	public SyncGeneric<StoredEnergyInfo> primaryInfo = new SyncGeneric(Logistics.infoTypes, 0);

	public EnergyReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		stacks = LogisticsAPI.getEnergyHelper().getEnergyList(network);
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
		if (stacks != null) {
			for (StoredEnergyInfo stack : stacks) {
				if (stack != null && primaryInfo.getObject()!=null && stack.coords.equals(primaryInfo.getObject().coords)) {
					return stack;
				}
			}
		}
		return new StandardInfo((byte) -1, "ITEMREND", " ", "NO DATA");
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);

		if (type == SyncType.SAVE || type == SyncType.SYNC)
			this.primaryInfo.readFromNBT(nbt, type);

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
						stacks.set(slot, (StoredEnergyInfo) Logistics.infoTypes.readFromNBT(compound));
					else
						stacks.add(slot, (StoredEnergyInfo) Logistics.infoTypes.readFromNBT(compound));
					break;
				case 1:
					stacks.get(slot).readUpdate(compound);
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
				this.stacks.add((StoredEnergyInfo) Logistics.infoTypes.readFromNBT(compound));

			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC)
			this.primaryInfo.writeToNBT(nbt, type);

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
				StoredEnergyInfo current = null;
				StoredEnergyInfo last = null;
				if (i < this.stacks.size()) {
					current = this.stacks.get(i);
				}
				if (i < this.lastStacks.size()) {
					last = this.lastStacks.get(i);
				}
				NBTTagCompound compound = new NBTTagCompound();
				if (current != null) {
					if (last != null) {
						if (!last.equals(current)) {
							this.lastStacks.set(i, current);
							NBTTagCompound updateTag = compound;
							last.writeUpdate(current, updateTag);
							if (!updateTag.hasNoTags()) {
								compound.setByte("f", (byte) 1);
								compound = updateTag;
							}
							// StoredEnergyStack.writeToNBT(compound,
							// this.stacks.get(i));
						}
					} else {
						compound.setByte("f", (byte) 0);
						this.lastStacks.add(i, current);
						Logistics.infoTypes.writeToNBT(compound, this.stacks.get(i));
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
					Logistics.infoTypes.writeToNBT(compound, this.stacks.get(i));
					list.appendTag(compound);
				}
			}

			nbt.setTag("StoredStacks", list);
		}
	}
}