package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import sonar.core.api.BlockCoords;
import sonar.core.api.StoredFluidStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.wrappers.FluidWrapper.StorageFluids;
import sonar.logistics.api.wrappers.ItemWrapper.SortingDirection;
import sonar.logistics.api.wrappers.ItemWrapper.SortingType;
import sonar.logistics.helpers.FluidHelper;
import sonar.logistics.info.types.FluidInventoryInfo;
import sonar.logistics.info.types.FluidStackInfo;
import sonar.logistics.info.types.ProgressInfo;

import com.google.common.collect.Lists;

public class FluidReaderHandler extends TileHandler implements IByteBufTile {

	public BlockCoords coords;
	public StorageFluids cachedFluids = StorageFluids.EMPTY;
	public boolean lastSync = false;

	public FluidStack current;
	public SyncTagType.INT setting = (INT) new SyncTagType.INT(1).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingOrder = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingType = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public int cacheID = -1;

	public FluidReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		cacheID = network.getNetworkID();
		cachedFluids = LogisticsAPI.getFluidHelper().getFluids(network).copy();
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te))) || dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
		}
	}

	public ILogicInfo currentInfo(TileEntity te) {
		ArrayList<StoredFluidStack> stacks = (ArrayList<StoredFluidStack>) cachedFluids.fluids.clone();
		switch (setting.getObject()) {
		case 0:
			if (current != null) {
				for (StoredFluidStack stack : stacks) {
					if (stack.equalStack(current)) {
						return FluidStackInfo.createInfo(stack, cacheID);
					}
				}
				return FluidStackInfo.createInfo(new StoredFluidStack(current, 0), cacheID);
			}
			break;
		case 1:
			if (posSlot.getObject() < stacks.size()) {
				return FluidStackInfo.createInfo(stacks.get(posSlot.getObject()), cacheID);
			}
			break;
		case 2:
			return FluidInventoryInfo.createInfo(cachedFluids, cacheID, sortingType.getObject(), sortingOrder.getObject());
		case 3:
			return new ProgressInfo(cachedFluids.sizing.getStoredFluids(), cachedFluids.sizing.getMaxFluids(), FontHelper.formatFluidSize(cachedFluids.sizing.getStoredFluids()) + " / " + FontHelper.formatFluidSize(cachedFluids.sizing.getMaxFluids()));
		}
		return new LogicInfo((byte) -1, "ITEMREND", " ", "NO DATA");
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
		if (type == SyncType.SPECIAL || type == SyncType.SYNC){
			FluidHelper.readStorageToNBT(nbt, cachedFluids.fluids, type);
			FluidHelper.sortFluidList(cachedFluids.fluids, SortingDirection.values()[sortingOrder.getObject()], SortingType.values()[sortingType.getObject()]);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		setting.writeToNBT(nbt, type);
		posSlot.writeToNBT(nbt, type);
		if (current != null) {
			current.writeToNBT(nbt);
		}
		if (type == SyncType.SPECIAL || type == SyncType.SYNC){
			lastSync= FluidHelper.writeStorageToNBT(nbt, lastSync, cachedFluids, type);
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
			if (setting.getObject() == 3) {
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
