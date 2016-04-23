package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.api.InventoryHandler.StorageSize;
import sonar.core.api.StoredItemStack;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.InventoryTileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.wrappers.ItemWrapper.SortingDirection;
import sonar.logistics.api.wrappers.ItemWrapper.SortingType;
import sonar.logistics.api.wrappers.ItemWrapper.StorageItems;
import sonar.logistics.helpers.ItemHelper;
import sonar.logistics.info.types.InventoryInfo;
import sonar.logistics.info.types.ProgressInfo;
import sonar.logistics.info.types.StoredStackInfo;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

public class InventoryReaderHandler extends InventoryTileHandler implements IByteBufTile {

	public BlockCoords coords;
	public StorageItems cachedItems = StorageItems.EMPTY.copy();
	public boolean lastSync = false;

	public ItemStack current;
	// 0=Stack, 1=Slot (only accepts one input)
	public SyncTagType.INT setting = (INT) new SyncTagType.INT(1).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingOrder = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT sortingType = (INT) new SyncTagType.INT(5).addSyncType(SyncType.SPECIAL);
	public int cacheID = -1;

	public InventoryReaderHandler(boolean isMultipart) {
		super(isMultipart, null);
		super.slots = new ItemStack[1];
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		INetworkCache cache = getNetwork(te);
		cacheID = cache.getNetworkID();
		cachedItems = LogisticsAPI.getItemHelper().getItems(cache).copy();
	}

	public INetworkCache getNetwork(TileEntity te) {
		return LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te))) || dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public ILogicInfo currentInfo(TileEntity te) {
		ArrayList<StoredItemStack> stacks = (ArrayList<StoredItemStack>) cachedItems.items.clone();
		switch (setting.getObject()) {
		case 0:
			if (slots[0] != null) {
				if (stacks != null) {
					for (StoredItemStack stack : stacks) {
						if (stack.equalStack(slots[0])) {
							return StoredStackInfo.createInfo(stack, cacheID);
						}
					}
				}
				return StoredStackInfo.createInfo(new StoredItemStack(slots[0], 0), cacheID);
			}
			break;
		case 1:
			StoredItemStack stack = LogisticsAPI.getItemHelper().getStack(LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()), targetSlot.getObject());
			if (stack != null) {
				return StoredStackInfo.createInfo(stack, cacheID);
			}
			return new LogicInfo((byte) -1, "ITEMREND", " ", " ");
		case 2:
			if (posSlot.getObject() < stacks.size()) {
				return StoredStackInfo.createInfo(stacks.get(posSlot.getObject()), cacheID);
			}
			break;
		case 3:
			if (stacks != null) {
				return InventoryInfo.createInfo(cachedItems.copy(), cacheID, sortingType.getObject(), sortingOrder.getObject());
			}
			break;
		case 4:
			return new ProgressInfo(cachedItems.sizing.getStoredFluids(), cachedItems.sizing.getMaxFluids(), FontHelper.formatStackSize(cachedItems.sizing.getStoredFluids()) + " / " + FontHelper.formatStackSize(cachedItems.sizing.getMaxFluids()));
		}
		return new LogicInfo((byte) -1, "ITEMREND", " ", "NO DATA");
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
		if (type == SyncType.SPECIAL || type == SyncType.SYNC){
			ItemHelper.readStorageToNBT(nbt, cachedItems.items, type);
			ItemHelper.sortItemList(cachedItems.items, SortingDirection.values()[sortingOrder.getObject()], SortingType.values()[sortingType.getObject()]);
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
		if (type == SyncType.SPECIAL || type == SyncType.SYNC){
			lastSync= ItemHelper.writeStorageToNBT(nbt, lastSync, cachedItems, type);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(setting, targetSlot, posSlot, sortingOrder, sortingType));
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch(id){
		case 1:
			targetSlot.writeToBuf(buf);
			break;
		case 2:
			posSlot.writeToBuf(buf);
			break;
		case 3:
			sortingOrder.writeToBuf(buf);
			break;
		case 4:
			sortingType.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch(id){
		case 0:
			if (setting.getObject() == 4) {
				setting.setObject(0);
			} else {
				setting.increaseBy(1);
			}
			break;
		case 1:
			targetSlot.readFromBuf(buf);
			break;
		case 2:
			posSlot.readFromBuf(buf);
			break;
		case 3:
			sortingOrder.readFromBuf(buf);
			break;
		case 4:
			sortingType.readFromBuf(buf);
			break;
		}
	}
}
