package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.ClientDataEmitter;
import sonar.logistics.api.connecting.ClientLogicMonitor;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoHelper;

public class ClientInfoManager implements IInfoManager {

	public ConcurrentHashMap<Integer, ConnectedDisplayScreen> connectedDisplays = new ConcurrentHashMap<Integer, ConnectedDisplayScreen>();
	
	//public LinkedHashMap<InfoUUID, IMonitorInfo> lastInfo = new LinkedHashMap();
	public LinkedHashMap<InfoUUID, IMonitorInfo> info = new LinkedHashMap();

	public Map<UUID, ArrayList<Object>> sortedLogicMonitors = new ConcurrentHashMap<UUID, ArrayList<Object>>();
	public Map<UUID, ArrayList<ClientLogicMonitor>> clientLogicMonitors = new ConcurrentHashMap<UUID, ArrayList<ClientLogicMonitor>>();
	
	public LinkedHashMap<InfoUUID, MonitoredList<?>> monitoredLists = new LinkedHashMap();	
	public LinkedHashMap<UUID, ILogicMonitor> monitors = new LinkedHashMap();
	public Map<Integer, MonitoredList<MonitoredBlockCoords>> coordMap = new ConcurrentHashMap<Integer, MonitoredList<MonitoredBlockCoords>>();
	
	//emitters
	public ArrayList<ClientDataEmitter> clientEmitters = new ArrayList<ClientDataEmitter>();
	

	public void onInfoPacket(NBTTagCompound packetTag, SyncType type) {
		NBTTagList packetList = packetTag.getTagList("infoList", NBT.TAG_COMPOUND);
		boolean save = type.isType(SyncType.SAVE);
		for (int i = 0; i < packetList.tagCount(); i++) {
			NBTTagCompound infoTag = packetList.getCompoundTagAt(i);
			InfoUUID id = NBTHelper.instanceNBTSyncable(InfoUUID.class, infoTag);
			if (save) {
				info.put(id, InfoHelper.readInfoFromNBT(infoTag));
				//info.replace(id, );
			} else {
				IMonitorInfo currentInfo = info.get(id);
				if(currentInfo!=null){
					currentInfo.readData(infoTag, type);
					info.put(id, currentInfo);
				}
			}
		}
	}

	public void addMonitor(ILogicMonitor monitor) {
		if (monitors.containsValue(monitor)) {
			return;
		}
		monitors.put(monitor.getIdentity(), monitor);
	}
	
	public void removeMonitor(ILogicMonitor monitor) {
		monitors.remove(monitor.getIdentity());
	}
	
	@Override
	public LinkedHashMap<UUID, ILogicMonitor> getMonitors() {
		return monitors;
	}

	@Override
	public LinkedHashMap<InfoUUID, IMonitorInfo> getInfoList() {
		return info;
	}
	
	public <T extends IMonitorInfo> MonitoredList<T> getMonitoredList(int networkID, InfoUUID uuid) {
		MonitoredList<T> list = MonitoredList.<T>newMonitoredList(networkID);
		monitoredLists.putIfAbsent(uuid, list);
		for (Entry<InfoUUID, MonitoredList<?>> entry : monitoredLists.entrySet()) {
			if (entry.getValue().networkID == networkID && entry.getKey().equals(uuid)) {
				return (MonitoredList<T>) entry.getValue();
			}
		}
		return list;
	}


	@Override
	public ConcurrentHashMap<Integer, ConnectedDisplayScreen> getConnectedDisplays() {
		return connectedDisplays;
	}
}
