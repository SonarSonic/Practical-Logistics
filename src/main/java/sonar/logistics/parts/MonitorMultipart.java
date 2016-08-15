package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IMonitorCache;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.connections.LogicMonitorCache;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.network.SyncMonitoredType;

public abstract class MonitorMultipart<T extends IMonitorInfo> extends SidedMultipart implements ILogicMonitor<T>, IByteBufTile {

	protected IdentifiedCoordsList list = new IdentifiedCoordsList(-1);
	protected ArrayList<MonitorViewer> viewers = new ArrayList<MonitorViewer>();
	protected SyncUUID uuid = new SyncUUID(-2);
	protected MonitorHandler handler = null;
	protected String handlerID;
	public SyncMonitoredType<T> selectedInfo;
	public BlockCoords lastSelected = null;
	public IMonitorInfo lastInfo = null;

	public MonitorMultipart(String handlerID, double width, double heightMin, double heightMax) {
		super(width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncParts.addAll(Lists.newArrayList(list, uuid));
		selectedInfo = new SyncMonitoredType<T>(handlerID, 0);
	}

	public MonitorMultipart(String handlerID, EnumFacing face, double width, double heightMin, double heightMax) {
		super(face, width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncParts.addAll(Lists.newArrayList(list, uuid));
		selectedInfo = new SyncMonitoredType<T>(handlerID, 0);
	}

	public void onFirstTick() {
		super.onFirstTick();
		this.setUUID();
	}

	public void setUUID() {
		if (!this.getWorld().isRemote) {
			if (uuid.getUUID() == null) {
				uuid.setObject(UUID.randomUUID());
				list.setIdentity(uuid.getUUID());
				LogicMonitorCache.monitors.add(this);
			}
			sendByteBufPacket(-2);
		}
	}

	@Override
	public IdentifiedCoordsList getMonitoringCoords() {
		return list;
	}

	@Override
	public MonitorHandler getHandler() {
		return handler == null ? handler = Logistics.monitorHandlers.getRegisteredObject(handlerID) : handler;
	}

	@Override
	public ArrayList<MonitorViewer> getViewers() {
		return viewers;
	}

	public void addViewer(MonitorViewer viewer) {
		viewers.add(viewer);
	}

	public void removeViewer(EntityPlayer player) {
		ArrayList<MonitorViewer> toRemove = new ArrayList();
		for (MonitorViewer viewer : viewers) {
			if (viewer.player.getGameProfile().getId().equals(player.getGameProfile().getId())) {
				toRemove.add(viewer);
			}
		}
		toRemove.forEach(remove -> viewers.remove(remove));
	}

	public UUID getMonitorUUID() {
		return uuid.getUUID();
	}

	public abstract ArrayList<IMonitorInfo> getSelectedInfo();

	public abstract void addInfo(T info);
	
	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		return super.writeData(nbt, type);
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

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case -3:
			BlockCoords.writeToBuf(buf, lastSelected);
			break;
		case -2:
			uuid.writeToBuf(buf);
			break;
		case 0:
			selectedInfo.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case -3:
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			if (coords != null) {
				if (!list.contains(coords)) {
					if (channelType() == ChannelType.SINGLE) {
						list.clear();
					}
					list.add(coords);
				}else{
					list.remove(coords);
				}
			}
			this.sendByteBufPacket(-1);
			break;
		case -2:
			uuid.readFromBuf(buf);
			list.setIdentity(uuid.getUUID());
			LogicMonitorCache.monitoredLists.put(this, MonitoredList.<T>newMonitoredList());
			break;
		case 0:
			selectedInfo.readFromBuf(buf);
			addInfo((T) selectedInfo.info);
			break;
		}
	}

}
