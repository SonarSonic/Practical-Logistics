package sonar.logistics.connections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.IMonitorCache;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.helpers.MonitorHelper;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.network.PacketMonitoredCoords;
import sonar.logistics.network.PacketMonitoredList;

public abstract class MonitorCache implements IMonitorCache {

	public LinkedHashMap<MonitorHandler, LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>>> monitoredList = new LinkedHashMap();
	public LinkedHashMap<MonitorHandler, LinkedHashMap<ILogicMonitor, MonitoredList<?>>> monitoredCollections = new LinkedHashMap();

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

	public <T extends IMonitorInfo> void getAndSendFullMonitoredList(ILogicMonitor<T> monitor, MonitoredList<T> lastList) {
		List<MonitorViewer> viewers = monitor.getViewers();
		if (!viewers.isEmpty()) {
			MonitoredList<T> list = MonitoredList.<T>newMonitoredList();
			monitoredList.putIfAbsent(monitor.getHandler(), new LinkedHashMap());
			LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> infoList = monitoredList.get(monitor.getHandler());
			for (Entry<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> entry : infoList.entrySet()) {
				IdentifiedCoordsList coords = monitor.getMonitoringCoords();
				if (coords.contains(entry.getKey().a) || coords.isEmpty() && entry.getValue() != null) {
					for (T info : ((MonitoredList<T>) entry.getValue()).info) {
						MonitorHelper.addInfoToList(list, monitor.getHandler(), info);
					}
					if(monitor.channelType()==ChannelType.SINGLE){
						break;
					}
				}
			}
			list.updateList(lastList);

			// FIXME make it so these are only retrieved if needed...
			NBTTagCompound syncTag = MonitorHelper.writeMonitoredList(new NBTTagCompound(), lastList.info.isEmpty(), monitor.getHandler(), list.copy(), SyncType.DEFAULT_SYNC);
			NBTTagCompound specialTag = MonitorHelper.writeMonitoredList(new NBTTagCompound(), lastList.info.isEmpty(), monitor.getHandler(), list.copy(), SyncType.SPECIAL);

			MonitoredList<MonitoredBlockCoords> coords = CacheRegistry.coordMap.get(getNetworkID());
			NBTTagCompound coordTag = MonitorHelper.writeMonitoredList(new NBTTagCompound(), true, CacheRegistry.handler, coords.copy(), SyncType.DEFAULT_SYNC);
			viewers.forEach(viewer -> {
				switch (viewer.type) {
				case CHANNEL:
					if (!viewer.wasSent(MonitorType.CHANNEL))
						Logistics.network.sendTo(new PacketMonitoredCoords(getNetworkID(), coordTag), (EntityPlayerMP) viewer.player);
					break;
				case INFO:
					Logistics.network.sendTo(new PacketMonitoredList(monitor, !viewer.wasSent(viewer.type) ? syncTag : specialTag, !viewer.wasSent(viewer.type) ? SyncType.DEFAULT_SYNC : SyncType.SPECIAL), (EntityPlayerMP) viewer.player);
					break;
				}
			});

			monitoredCollections.get(monitor.getHandler()).put(monitor, list);
		}
	}

	public <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(MonitorHandler<T> type) {
		monitoredList.putIfAbsent(type, new LinkedHashMap());
		LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> infoList = monitoredList.get(type);
		LinkedHashMap<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> monitoredList = new LinkedHashMap();
		for (Entry<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> entry : infoList.entrySet()) {
			MonitoredList<T> list = type.updateInfo((MonitoredList<T>) entry.getValue(), entry.getKey().a, entry.getKey().b);
			monitoredList.put(entry.getKey(), list);
		}
		this.monitoredList.put(type, monitoredList);
		return null;
	}

	public abstract <T extends IMonitorInfo> void compileCoordsList(MonitorHandler<T> type);
}
