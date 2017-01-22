package sonar.logistics.connections.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import mcmultipart.multipart.ISlottedPart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.utils.Pair;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.ILogisticsNetwork;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ClientLogicMonitor;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ILargeDisplay;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.common.multiparts.LargeDisplayScreenPart;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.PacketInfoList;
import sonar.logistics.network.PacketLogicMonitors;

public class ServerInfoManager implements IInfoManager {

	public final int UPDATE_RADIUS = 64;

	// server side
	public ArrayList<InfoUUID> changedInfo = new ArrayList();
	public ArrayList<IInfoDisplay> displays = new ArrayList();
	public boolean markDisplaysDirty = true;
	public HashMap<EntityPlayer, EntityEvent.EnteringChunk> requireUpdates = new HashMap();
	public HashMap<EntityPlayer, ArrayList<IInfoDisplay>> viewables = new HashMap();

	// client side
	public LinkedHashMap<InfoUUID, MonitoredList<?>> monitoredLists = new LinkedHashMap();
	public LinkedHashMap<UUID, ILogicMonitor> monitors = new LinkedHashMap();

	public LinkedHashMap<InfoUUID, IMonitorInfo> lastInfo = new LinkedHashMap();
	public LinkedHashMap<InfoUUID, IMonitorInfo> info = new LinkedHashMap();

	public ConcurrentHashMap<Integer, ConnectedDisplayScreen> connectedDisplays = new ConcurrentHashMap<Integer, ConnectedDisplayScreen>();

	public int ticks;
	public boolean updateViewingMonitors;

	public void onServerClosed() {
		monitors.clear();
		displays.clear();
		monitoredLists.clear();
		changedInfo.clear();
		lastInfo.clear();
		info.clear();
	}

	public boolean enableEvents() {
		return !displays.isEmpty();
	}

	public void addMonitor(ILogicMonitor monitor) {
		if (monitor.getCoords().getWorld().isRemote) {
			return;
		}
		if (monitors.containsValue(monitor)) {
			return;
		}
		monitors.put(monitor.getIdentity(), monitor);
		updateViewingMonitors = true;
	}

	public void addDisplay(IInfoDisplay display) {
		if (display.getCoords().getWorld().isRemote) {
			return;
		}
		if (displays.contains(display)) {
			return;
		}
		displays.add(display);
		updateViewingMonitors = true;
	}

	public void removeMonitor(ILogicMonitor monitor) {
		if (monitor.getCoords().getWorld().isRemote) {
			// return true;
		}
		if (!monitor.getNetwork().isFakeNetwork() && monitor.getNetwork() instanceof ILogisticsNetwork) {
			((ILogisticsNetwork) monitor.getNetwork()).removeMonitor(monitor);
		}
		monitors.remove(monitor.getIdentity());
		updateViewingMonitors = true;
	}

	public boolean removeDisplay(IInfoDisplay display) {
		if (display.getCoords().getWorld().isRemote) {
			return true;
		}
		updateViewingMonitors = true;
		return displays.remove(display);
	}

	public ArrayList<IInfoDisplay> getViewableDisplays(EntityPlayer player, boolean sendSyncPackets) {
		ArrayList<IInfoDisplay> viewable = new ArrayList();
		World world = player.getEntityWorld();
		PlayerChunkMap manager = ((WorldServer) world).getPlayerChunkMap();
		for (IInfoDisplay display : displays) {
			if (manager.isPlayerWatchingChunk((EntityPlayerMP) player, display.getCoords().getX() >> 4, display.getCoords().getZ() >> 4)) {
				viewable.add(display);
			}
			if (sendSyncPackets && display instanceof LargeDisplayScreenPart) {
				LargeDisplayScreenPart part = (LargeDisplayScreenPart) display;
				SonarMultipartHelper.sendMultipartSyncToPlayer(part, (EntityPlayerMP) player);
			}
		}
		return viewable;
	}

	public void sendFullPacket(EntityPlayer player) {
		if (player != null) {
			ArrayList<IMonitorInfo> infoList = getInfoFromUUIDs(getUUIDsToSync(getViewableDisplays(player, true)));
			if (infoList.isEmpty()) {
				return;
			}
			NBTTagList packetList = new NBTTagList();
			for (IMonitorInfo info : infoList) {
				if (info != null && info.isValid() && !info.isHeader()) {
					packetList.appendTag(InfoHelper.writeInfoToNBT(new NBTTagCompound(), info, SyncType.SAVE));
				}
			}
			if (!packetList.hasNoTags()) {
				NBTTagCompound packetTag = new NBTTagCompound();
				packetTag.setTag("infoList", packetList);
				Logistics.network.sendTo(new PacketInfoList(packetTag, SyncType.SAVE), (EntityPlayerMP) player);
			}
		}
	}

	public ArrayList<IMonitorInfo> getInfoFromUUIDs(ArrayList<InfoUUID> ids) {
		ArrayList<IMonitorInfo> infoList = new ArrayList();
		for (InfoUUID id : ids) {
			ILogicMonitor monitor = CableHelper.getMonitorFromHashCode(id.hashCode, false);
			if (monitor != null) {
				IMonitorInfo info = monitor.getMonitorInfo(id.channelID);
				if (info != null) {
					infoList.add(info);
				}
			}
		}
		return infoList;
	}

	public ArrayList<InfoUUID> getUUIDsToSync(ArrayList<IInfoDisplay> displays) {
		ArrayList<InfoUUID> ids = new ArrayList();
		for (IInfoDisplay display : displays) {
			IInfoContainer container = display.container();
			for (int i = 0; i < container.getMaxCapacity(); i++) {
				InfoUUID id = container.getInfoUUID(i);
				if (id.valid() && !ids.contains(id)) {
					ids.add(id);
				}
			}
		}
		return ids;
	}

	public IMonitorInfo getInfoFromUUID(InfoUUID uuid) {
		return info.get(uuid);
	}

	public TargetPoint getTargetPointFromPlayer(EntityPlayer player) {
		return new TargetPoint(player.getEntityWorld().provider.getDimension(), player.posX, player.posY, player.posZ, UPDATE_RADIUS);
	}

	// client methods
	@Nullable
	public Pair<InfoUUID, MonitoredList<?>> getMonitorFromServer(InfoUUID uuid) {
		for (Entry<InfoUUID, ?> entry : monitoredLists.entrySet()) {
			if (entry.getKey().equals(uuid)) {
				return new Pair(entry.getKey(), entry.getValue());
			}
		}
		return null;
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

	public void onServerTick() {
		if (updateViewingMonitors) {
			updateViewingMonitors = false;
			for (ILogicMonitor monitor : monitors.values()) {
				monitor.getViewersList().connectedDisplays.clear();
			}
			for (IInfoDisplay display : displays) {
				for (int i = 0; i < display.container().getMaxCapacity(); i++) {
					InfoUUID uuid = display.container().getInfoUUID(i);
					MonitoredList<?> list = monitoredLists.get(uuid);
					if (list != null) {
						ILogicMonitor monitor = CableHelper.getMonitorFromHashCode(uuid.hashCode, false);
						if (monitor != null) {
							monitor.getViewersList().connectedDisplays.add(display);
						}
					}
				}
			}
		}

		if (ticks < 50) {
			ticks++;
		} else {
			ticks = 0;
			updateViewers();
		}
		if (!changedInfo.isEmpty() && !displays.isEmpty()) {			
			HashMap<EntityPlayer, NBTTagList> savePackets = new HashMap<EntityPlayer, NBTTagList>();
			HashMap<EntityPlayer, NBTTagList> syncPackets = new HashMap<EntityPlayer, NBTTagList>();

			for (InfoUUID id : changedInfo) {
				boolean isSynced = false;
				IMonitorInfo monitorInfo = info.get(id);
				if (id.valid() && monitorInfo != null) {

					//NBTTagCompound updateTag = InfoHelper.writeInfoToNBT(new NBTTagCompound(), monitorInfo, SyncType.DEFAULT_SYNC);
					NBTTagCompound updateTag = InfoHelper.writeInfoToNBT(new NBTTagCompound(), monitorInfo, SyncType.SAVE);
					NBTTagCompound saveTag = InfoHelper.writeInfoToNBT(new NBTTagCompound(), monitorInfo, SyncType.SAVE);
					boolean shouldUpdate = !updateTag.hasNoTags();

					for (IInfoDisplay display : displays) {
						if (display.container().monitorsUUID(id)) {
							if (shouldUpdate) {
								ArrayList<EntityPlayer> viewers = display.getViewersList().getViewers(false, MonitorType.INFO);
								updateTag = id.writeData(updateTag, SyncType.SAVE);
								addPacketsToList(syncPackets, viewers, updateTag, saveTag, false);
							}
							ArrayList<EntityPlayer> fullViewers = display.getViewersList().getViewers(false, MonitorType.FULL_INFO);
							if (!fullViewers.isEmpty()) {
								saveTag = id.writeData(saveTag, SyncType.SAVE);
								addPacketsToList(savePackets, fullViewers, updateTag, saveTag, true);
								fullViewers.forEach(viewer -> {
									display.getViewersList().removeViewer(viewer, MonitorType.FULL_INFO);
									display.getViewersList().addViewer(viewer, MonitorType.INFO);
								});
							}
						}
					}
				}
			}

			if (!savePackets.isEmpty()) {
				savePackets.entrySet().forEach(entry -> sendPlayerPacket(entry.getKey(), entry.getValue(), SyncType.SAVE));
			}
			if (!syncPackets.isEmpty()) {
				//syncPackets.entrySet().forEach(entry -> sendPlayerPacket(entry.getKey(), entry.getValue(), SyncType.DEFAULT_SYNC));
				syncPackets.entrySet().forEach(entry -> sendPlayerPacket(entry.getKey(), entry.getValue(), SyncType.SAVE));
			}
			changedInfo.clear();
		}
		return;

	}

	public void addPacketsToList(HashMap<EntityPlayer, NBTTagList> playerPackets, ArrayList<EntityPlayer> viewers, NBTTagCompound updateTag, NBTTagCompound saveTag, boolean fullPacket) {
		for (EntityPlayer player : viewers) {
			NBTTagList list = playerPackets.get(player);
			if (list == null) {
				playerPackets.put(player, new NBTTagList());
				list = playerPackets.get(player);
			}
			list.appendTag(fullPacket ? saveTag.copy() : updateTag.copy());
		}
	}

	public void updateViewers() {
		for (Entry<EntityPlayer, EntityEvent.EnteringChunk> entry : requireUpdates.entrySet()) {
			EntityPlayer player = entry.getKey();
			// MonitorViewer viewer = new MonitorViewer(player, MonitorType.INFO);
			ArrayList<IInfoDisplay> lastDisplays = viewables.getOrDefault(player, new ArrayList());
			ArrayList<IInfoDisplay> displays = getViewableDisplays(player, false);
			displays.forEach(display -> {
				display.getViewersList().addViewer(player, MonitorType.FULL_INFO);
				lastDisplays.remove(display);
			});
			lastDisplays.forEach(display -> display.getViewersList().removeViewer(player, MonitorType.INFO));
			viewables.put(player, (ArrayList<IInfoDisplay>) displays.clone());
		}
		requireUpdates.clear();
	}

	public void sendPlayerPacket(EntityPlayer player, NBTTagList list, SyncType type) {
		NBTTagCompound packetTag = new NBTTagCompound();
		packetTag.setTag("infoList", list);
		Logistics.network.sendTo(new PacketInfoList(packetTag, type), (EntityPlayerMP) player);
	}

	public void changeInfo(InfoUUID id, IMonitorInfo newInfo) {
		lastInfo.put(id, info.get(id));
		info.put(id, newInfo);
		changedInfo.add(id);
	}

	public ArrayList<ILogicMonitor> getLocalMonitors(ArrayList<ILogicMonitor> monitors, ScreenMultipart part) {
		INetworkCache networkCache = part.getNetwork();
		ISlottedPart connectedPart = part.getContainer().getPartInSlot(PartSlot.getFaceSlot(part.face));
		if (connectedPart != null && connectedPart instanceof ILogicMonitor) {
			if (!monitors.contains((ILogicMonitor) connectedPart))
				monitors.add((ILogicMonitor) connectedPart);
		} else {
			for (ILogicMonitor monitor : networkCache.getLocalMonitors()) {
				if (!monitors.contains(monitor))
					monitors.add(monitor);
			}
		}

		return monitors;
	}

	public void sendLocalMonitorsToClient(ScreenMultipart part, EntityPlayer player) {
		ArrayList<ILogicMonitor> monitors = new ArrayList<ILogicMonitor>();
		UUID identity = part.getIdentity();
		if (part instanceof ILargeDisplay) {
			ConnectedDisplayScreen screen = ((ILargeDisplay) part).getDisplayScreen();
			if (screen != null && screen.getTopLeftScreen() != null) {
				identity = ((ScreenMultipart) screen.getTopLeftScreen()).getIdentity();
			}
			monitors = screen != null ? screen.getLogicMonitors(monitors) : getLocalMonitors(monitors, part);
		} else {
			monitors = getLocalMonitors(monitors, part);
		}

		ArrayList<ClientLogicMonitor> clientMonitors = new ArrayList();
		monitors.forEach(monitor -> {
			monitor.getViewersList().addViewer(player, MonitorType.TEMPORARY);
			clientMonitors.add(new ClientLogicMonitor(monitor));
		});
		Logistics.network.sendTo(new PacketLogicMonitors(clientMonitors, identity), (EntityPlayerMP) player);
	}

	public class StoredChunkPos extends ChunkPos {

		// these won't be included in the hashCode
		public int monitorCount = 0;
		public int dim;

		public StoredChunkPos(int dim, BlockPos pos) {
			super(pos);
			this.dim = dim;
		}

		public StoredChunkPos(BlockCoords coords) {
			this(coords.getDimension(), coords.getBlockPos());
		}

		public int addMonitor() {
			return monitorCount++;
		}

		public int removeMonitor() {
			return monitorCount--;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (!(obj instanceof StoredChunkPos)) {
				return false;
			} else {
				StoredChunkPos chunkpos = (StoredChunkPos) obj;
				return this.chunkXPos == chunkpos.chunkXPos && this.chunkZPos == chunkpos.chunkZPos && dim == chunkpos.dim;
			}
		}

	}

	@Override
	public LinkedHashMap<UUID, ILogicMonitor> getMonitors() {
		return monitors;
	}

	@Override
	public LinkedHashMap<InfoUUID, IMonitorInfo> getInfoList() {
		return info;
	}

	@Override
	public ConcurrentHashMap<Integer, ConnectedDisplayScreen> getConnectedDisplays() {
		return connectedDisplays;
	}
}
