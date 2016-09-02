package sonar.logistics.parts;

import com.google.common.collect.Lists;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.core.api.inventories.StoredItemStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.inventory.SonarMultipartInventory;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.IGuiTile;
import sonar.core.utils.Pair;
import sonar.core.utils.SortingDirection;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.LogicInfoList;
import sonar.logistics.api.info.ProgressInfo;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.api.readers.InventoryReader;
import sonar.logistics.client.gui.GuiInfoReader;
import sonar.logistics.client.gui.GuiInventoryReader;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.common.containers.ContainerInventoryReader;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.ItemHelper;
import sonar.logistics.monitoring.ItemMonitorHandler;
import sonar.logistics.monitoring.MonitoredItemStack;
import sonar.logistics.registries.LogicRegistry.RegistryType;

public class InventoryReaderPart extends ReaderMultipart<MonitoredItemStack> implements IByteBufTile {

	public SonarMultipartInventory inventory = new SonarMultipartInventory(this, 1);
	public SyncEnum<InventoryReader.Modes> setting = (SyncEnum) new SyncEnum(InventoryReader.Modes.values(), 2).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT targetSlot = (INT) new SyncTagType.INT(3).addSyncType(SyncType.SPECIAL);
	public SyncTagType.INT posSlot = (INT) new SyncTagType.INT(4).addSyncType(SyncType.SPECIAL);
	public SyncEnum<SortingDirection> sortingOrder = (SyncEnum) new SyncEnum(SortingDirection.values(), 5).addSyncType(SyncType.SPECIAL);
	public SyncEnum<InventoryReader.SortingType> sortingType = (SyncEnum) new SyncEnum(InventoryReader.SortingType.values(), 6).addSyncType(SyncType.SPECIAL);
	{
		syncParts.addAll(Lists.newArrayList(inventory, setting, targetSlot, posSlot, sortingOrder, sortingType));
	}

	public InventoryReaderPart() {
		super(ItemMonitorHandler.id);
	}

	public InventoryReaderPart(EnumFacing face) {
		super(ItemMonitorHandler.id, face);
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.inventoryReaderPart);
	}

	@Override
	public MonitoredList<MonitoredItemStack> sortMonitoredList(MonitoredList<MonitoredItemStack> updateInfo) {
		ItemHelper.sortItemList(updateInfo, sortingOrder.getObject(), sortingType.getObject());
		return updateInfo;
	}

	@Override
	public ChannelType channelType() {
		return ChannelType.UNLIMITED;
	}

	@Override
	public void setMonitoredInfo(MonitoredList<MonitoredItemStack> updateInfo) {
		IMonitorInfo info = null;
		switch (setting.getObject()) {
		case INVENTORIES:
			info = new LogicInfoList(this.getIdentity(), MonitoredItemStack.id);
			break;
		case POS:
			int pos = posSlot.getObject();
			if (pos < updateInfo.size())
				info = updateInfo.get(posSlot.getObject());
			break;
		case SLOT:
			// make a way of getting the slot
			break;
		case STACK:
			ItemStack stack = inventory.getStackInSlot(0);
			if (stack != null) {
				MonitoredItemStack dummyInfo = new MonitoredItemStack(new StoredItemStack(stack, 0));
				Pair<Boolean, IMonitorInfo> latestInfo = updateInfo.getLatestInfo(dummyInfo);
				info = latestInfo.a ? latestInfo.b : dummyInfo;
			}
			break;
		case STORAGE:
			info = new ProgressInfo(LogicInfo.buildDirectInfo("stored", RegistryType.TILE, updateInfo.sizing.getStored()), LogicInfo.buildDirectInfo("max", RegistryType.TILE, updateInfo.sizing.getMaxStored()));
			break;
		default:
			break;
		}
		if (info != null) {
			InfoUUID id = new InfoUUID(getIdentity().hashCode(), 0);
			IMonitorInfo oldInfo = LogicMonitorCache.info.get(id);
			if (oldInfo == null || !oldInfo.isMatchingType(info) || !oldInfo.isIdenticalInfo(info)) {
				LogicMonitorCache.changeInfo(id, info);
			}
		}
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerInventoryReader(this, player);
		}
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiInventoryReader(this, player);
		}
		return null;
	}

}
