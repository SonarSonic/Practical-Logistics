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
import sonar.logistics.api.cache.ILogisticsNetwork;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.connections.managers.NetworkManager;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.helpers.LogisticsHelper;
import sonar.logistics.network.PacketMonitoredCoords;
import sonar.logistics.network.PacketMonitoredList;

public abstract class AbstractNetwork implements ILogisticsNetwork {

	public boolean resendAllLists = false;
	public final Map<LogicMonitorHandler, Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>>> connectionInfo = new LinkedHashMap(); // block coords stored with the info gathered
	public final Map<LogicMonitorHandler, Map<ILogicMonitor, MonitoredList<?>>> monitorInfo = new LinkedHashMap();
	public final Map<IInfoDisplay, IInfoContainer> connectedDisplays = new LinkedHashMap();
	public final ArrayList<ILogicMonitor> localMonitors = new ArrayList();

	/**adds an info display to the list of display associated with this cache*/
	public void addDisplay(IInfoDisplay display) {
		if (!connectedDisplays.containsKey(display)) {
			connectedDisplays.put(display, new InfoContainer(display));
		}
	}

	public void removeDisplay(IInfoDisplay display) {
		if (connectedDisplays.containsKey(display)) {
			connectedDisplays.remove(display);
		}
	}

	public <T extends IMonitorInfo> void addMonitor(ILogicMonitor<T> monitor) {
		monitorInfo.putIfAbsent(monitor.getHandler(), new LinkedHashMap());
		if (!monitorInfo.get(monitor.getHandler()).containsKey(monitor)) {
			monitorInfo.get(monitor.getHandler()).put(monitor, MonitoredList.<T>newMonitoredList(getNetworkID()));
		}
		compileCoordsList(monitor.getHandler());
	}

	public <T extends IMonitorInfo> void removeMonitor(ILogicMonitor<T> monitor) {
		monitorInfo.get(monitor.getHandler()).remove(monitor);
		compileCoordsList(monitor.getHandler());
	}

	public <T extends IMonitorInfo> MonitoredList<T> updateMonitoredList(ILogicMonitor<T> monitor, Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> connections) {
		MonitoredList<T> updateList = MonitoredList.<T>newMonitoredList(getNetworkID());
		IdentifiedCoordsList channels = monitor.getChannels();
		for (Entry<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> entry : connections.entrySet()) {
			if ((entry.getValue() != null && !entry.getValue().isEmpty()) && (channels.isEmpty() || channels.contains(entry.getKey().a))) {
				for (T coordInfo : (MonitoredList<T>) entry.getValue()) {
					updateList.addInfoToList(coordInfo);
				}
				updateList.sizing.add(entry.getValue().sizing);
				if (monitor.channelType() == ChannelType.SINGLE) {
					break;
				}
			}
		}
		return updateList;
	}

	public void sendPacketsToViewer(ILogicMonitor monitor, List<MonitorViewer> viewers, boolean fullPacket, MonitoredList saveList, MonitoredList lastList) {		
		NBTTagCompound tag = InfoHelper.writeMonitoredList(new NBTTagCompound(), lastList.isEmpty(), saveList, fullPacket ? SyncType.DEFAULT_SYNC : SyncType.SPECIAL);		
		MonitoredList<MonitoredBlockCoords> coords = fullPacket ? NetworkManager.getCoordMap().get(getNetworkID()) : null;
		NBTTagCompound coordTag = fullPacket ? InfoHelper.writeMonitoredList(new NBTTagCompound(), coords.isEmpty(), coords.copyInfo(), SyncType.DEFAULT_SYNC) : null;
		if (resendAllLists || fullPacket || !tag.hasNoTags()) {
			viewers.forEach(viewer -> {
				switch (viewer.type) {
				case CHANNEL:
					if (fullPacket && !coordTag.hasNoTags())
						Logistics.network.sendTo(new PacketMonitoredCoords(getNetworkID(), coordTag), (EntityPlayerMP) viewer.player);
					break;
				case INFO:
					Logistics.network.sendTo(new PacketMonitoredList(monitor, saveList.networkID, tag, fullPacket ? SyncType.DEFAULT_SYNC : SyncType.SPECIAL), (EntityPlayerMP) viewer.player);
					break;
				}
				if (resendAllLists || fullPacket) {
					monitor.sentViewerPacket(viewer, true);
				}
			});
		}
	}
	
	public MonitoredList<?> getListForMonitor(ILogicMonitor monitor){
		return null;		
	}
	
	public <T extends IMonitorInfo> Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> getMonitoredList(LogicMonitorHandler<T> type) {
		Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> infoList = connectionInfo.getOrDefault(type, new LinkedHashMap());
		Map<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> coordInfo = new LinkedHashMap();
		for (Entry<Pair<BlockCoords, EnumFacing>, MonitoredList<?>> entry : infoList.entrySet()) {
			MonitoredList<T> oldList = entry.getValue() == null ? MonitoredList.<T>newMonitoredList(getNetworkID()) : (MonitoredList<T>) entry.getValue();
			MonitoredList<T> list = type.updateInfo(this, oldList, entry.getKey().a, entry.getKey().b);
			coordInfo.put(entry.getKey(), list);
		}
		return coordInfo;
	}

	public abstract <T extends IMonitorInfo> void compileCoordsList(LogicMonitorHandler<T> type);
}
