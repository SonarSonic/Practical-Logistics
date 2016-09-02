package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import sonar.core.api.utils.BlockCoords;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.IMonitorCache;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.network.SyncMonitoredType;

public abstract class MonitorMultipart<T extends IMonitorInfo> extends SidedMultipart implements ILogicMonitor<T>, IByteBufTile, IChannelledTile {

	protected IdentifiedCoordsList list = new IdentifiedCoordsList(-1);
	protected HashMap<Boolean, ArrayList<MonitorViewer>> viewers = new HashMap<Boolean, ArrayList<MonitorViewer>>();
	protected SyncUUID uuid = new SyncUUID(-2);
	protected LogicMonitorHandler handler = null;
	protected String handlerID;
	public SyncMonitoredType<T> selectedInfo;
	public BlockCoords lastSelected = null;
	public IMonitorInfo lastInfo = null;
	public int lastPos = -1;

	public MonitorMultipart(String handlerID, double width, double heightMin, double heightMax) {
		super(width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncParts.addAll(Lists.newArrayList(list, uuid));
		selectedInfo = new SyncMonitoredType<T>(-4);
	}

	public MonitorMultipart(String handlerID, EnumFacing face, double width, double heightMin, double heightMax) {
		super(face, width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncParts.addAll(Lists.newArrayList(list, uuid));
		selectedInfo = new SyncMonitoredType<T>(-4);
	}

	public void onLoaded() {
		super.onLoaded();
		LogicMonitorCache.addMonitor(this);
	}

	public void onRemoved() {
		super.onRemoved();
		LogicMonitorCache.removeMonitor(this);
	}

	public void onUnloaded() {
		super.onUnloaded();
		LogicMonitorCache.removeMonitor(this);
	}

	public void onFirstTick() {
		super.onFirstTick();
		this.setUUID();
		LogicMonitorCache.addMonitor(this);
	}

	public void setUUID() {
		if (this.getWorld() != null && !this.getWorld().isRemote) {
			if (uuid.getUUID() == null) {
				uuid.setObject(UUID.randomUUID());
				list.setIdentity(uuid.getUUID());
				LogicMonitorCache.monitors.add(this);
			}
			sendByteBufPacket(-2);
		}
	}

	@Override
	public IdentifiedCoordsList getChannels() {
		return list;
	}

	@Override
	public LogicMonitorHandler getHandler() {
		return handler == null ? handler = LogicMonitorHandler.instance(handlerID) : handler;
	}

	@Override
	public ArrayList<MonitorViewer> getViewers(boolean sentFirstPacket) {
		return viewers.getOrDefault(sentFirstPacket, new ArrayList());
	}

	public void sentViewerPacket(MonitorViewer viewer, boolean sentFirstPacket) {
		if (viewers.get(!sentFirstPacket).contains(viewer)) {
			viewers.get(!sentFirstPacket).remove(viewer);
		}
		if (!viewers.get(!sentFirstPacket).contains(viewer)) {
			viewers.get(!sentFirstPacket).add(viewer);
		}
	}

	public void addViewer(MonitorViewer viewer) {
		viewers.putIfAbsent(false, new ArrayList());
		viewers.get(false).add(viewer);
	}

	public void removeViewer(EntityPlayer player) {
		ArrayList<MonitorViewer> toRemove = new ArrayList();
		viewers.putIfAbsent(true, new ArrayList());
		for (MonitorViewer viewer : viewers.get(true)) {
			if (viewer.player.getGameProfile().getId().equals(player.getGameProfile().getId())) {
				toRemove.add(viewer);
			}
		}
		toRemove.forEach(remove -> viewers.remove(remove));
	}

	public UUID getIdentity() {
		if (uuid.getUUID() == null) {
			setUUID();
		}
		return uuid.getUUID();
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		if (network instanceof IMonitorCache) {
			IMonitorCache storageCache = (IMonitorCache) network;
			storageCache.<T>addMonitor(this);
		}
	}

	public MonitoredList<T> getMonitoredList() {
		return LogicMonitorCache.getMonitoredList(this);
	}

	public int getMaxInfo() {
		return 4;
	}

	public void addInfo(List<String> info) {
		super.addInfo(info);
		info.add("Channels Configured: " + !list.isEmpty());
		if (getIdentity() != null)
			info.add("Monitor UUID: " + this.getIdentity().hashCode());
		info.add("Max Info: " + getMaxInfo());
	}

	public final int ADD = -9, PAIRED = -10, ALL = 100;

	public void modifyCoords(MonitoredBlockCoords coords) {
		lastSelected = coords.syncCoords.getCoords();
		sendByteBufPacket(-3);
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -3:
			BlockCoords.writeToBuf(buf, lastSelected);
			break;
		case -2:
			uuid.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case -3:
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			list.modifyCoords(channelType(), coords);
			sendByteBufPacket(list.tagID);
			break;
		case -2:
			uuid.readFromBuf(buf);
			list.setIdentity(uuid.getUUID());
			LogicMonitorCache.monitoredLists.put(this, MonitoredList.<T>newMonitoredList());
			break;
		}
	}
}
