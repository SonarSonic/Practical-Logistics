package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.IMonitorCache;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.helpers.MonitorHelper;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.monitoring.MonitoredItemStack;
import sonar.logistics.network.PacketMonitoredCoords;
import sonar.logistics.network.PacketMonitoredList;

public abstract class MonitorCache implements IMonitorCache {

	public LinkedHashMap<MonitorHandler, LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>>> monitoredList = new LinkedHashMap(); // block coords stored with the info gathered
	public LinkedHashMap<MonitorHandler, LinkedHashMap<ILogicMonitor, MonitoredList<?>>> monitoredCollections = new LinkedHashMap();
	public LinkedHashMap<IInfoDisplay, IInfoContainer> displayCollections = new LinkedHashMap();

	public void addDisplay(IInfoDisplay display) {
		if (!displayCollections.containsKey(display)) {
			displayCollections.put(display, new InfoContainer(display));
		}
	}

	public void removeDisplay(IInfoDisplay display) {
		if (displayCollections.containsKey(display)) {
			displayCollections.remove(display);
		}
	}

	public <T extends IMonitorInfo> void addMonitor(ILogicMonitor<T> monitor) {
		monitoredCollections.putIfAbsent(monitor.getHandler(), new LinkedHashMap());
		if (!monitoredCollections.get(monitor.getHandler()).containsKey(monitor)) {
			monitoredCollections.get(monitor.getHandler()).put(monitor, MonitoredList.<T>newMonitoredList());
		}
		compileCoordsList(monitor.getHandler());
	}

	public <T extends IMonitorInfo> void removeMonitor(ILogicMonitor<T> monitor) {
		monitoredCollections.get(monitor.getHandler()).remove(monitor);
		compileCoordsList(monitor.getHandler());
	}

	public int ticks = 0;

	public <T extends IMonitorInfo> MonitoredList<T> updateMonitoredList(ILogicMonitor<T> monitor, Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> connections) {		
		MonitoredList<T> updateList = MonitoredList.<T>newMonitoredList(); // make a new list
		IdentifiedCoordsList monitoredCoords = monitor.getMonitoringCoords(); // get all the connections which the monitor can view
		for (Entry<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> entry : connections.entrySet()) {//go through all the connections on the network		
			if ((entry.getValue() != null && !entry.getValue().isEmpty()) && (monitoredCoords.isEmpty() || monitoredCoords.contains(entry.getKey().a) )) { // make sure it can display the connections info
				for (T coordInfo : ((MonitoredList<T>)entry.getValue())) { //go through all the info
					updateList.addInfoToList(coordInfo); // add the info to the new list
				}
				if (monitor.channelType() == ChannelType.SINGLE) {
					break;
				}
			}
		}
		return updateList;
	}

	public void sendPacketsToViewer(ILogicMonitor monitor, List<MonitorViewer> viewers, MonitoredList saveList, MonitoredList lastList) {
		NBTTagCompound syncTag = MonitorHelper.writeMonitoredList(new NBTTagCompound(), lastList.isEmpty(), saveList, SyncType.DEFAULT_SYNC);
		NBTTagCompound specialTag = MonitorHelper.writeMonitoredList(new NBTTagCompound(), lastList.isEmpty(), saveList, SyncType.SPECIAL);

		MonitoredList<MonitoredBlockCoords> coords = CacheRegistry.coordMap.get(getNetworkID());
		NBTTagCompound coordTag = MonitorHelper.writeMonitoredList(new NBTTagCompound(), coords.isEmpty(), coords.copyInfo(), SyncType.DEFAULT_SYNC);
		viewers.forEach(viewer -> {
			switch (viewer.type) {
			case CHANNEL:
				if (!viewer.wasSent(MonitorType.CHANNEL) && !coordTag.hasNoTags())
					Logistics.network.sendTo(new PacketMonitoredCoords(getNetworkID(), coordTag), (EntityPlayerMP) viewer.player);
				break;
			case INFO:
				Logistics.network.sendTo(new PacketMonitoredList(monitor, !viewer.wasSent(viewer.type) ? syncTag : specialTag, !viewer.wasSent(viewer.type) ? SyncType.DEFAULT_SYNC : SyncType.SPECIAL), (EntityPlayerMP) viewer.player);
				break;
			}
		});

	}

	public <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(MonitorHandler<T> type) {
		monitoredList.putIfAbsent(type, new LinkedHashMap());
		LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> infoList = monitoredList.get(type);
		LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> coordInfo = new LinkedHashMap();
		for (Entry<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> entry : infoList.entrySet()) {
			MonitoredList<T> list = type.updateInfo((MonitoredList<T>) entry.getValue(), entry.getKey().a, entry.getKey().b);
			coordInfo.put(entry.getKey(), list);
		}
		this.monitoredList.put(type, coordInfo);
		return null;
	}

	public abstract <T extends IMonitorInfo> void compileCoordsList(MonitorHandler<T> type);
}
