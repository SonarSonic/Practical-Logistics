package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.ILogisticsNetwork;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.TransferMode;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.connections.managers.LogicMonitorManager;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.network.SyncMonitoredType;

public abstract class MonitorMultipart<T extends IMonitorInfo> extends SidedMultipart implements ILogicMonitor<T>, IByteBufTile, IChannelledTile {

	public static final PropertyBool hasDisplay = PropertyBool.create("display");
	protected IdentifiedCoordsList list = new IdentifiedCoordsList(-1);
	protected HashMap<Boolean, ArrayList<MonitorViewer>> viewers = new HashMap<Boolean, ArrayList<MonitorViewer>>();
	protected SyncUUID uuid = new SyncUUID(-2); // CAN I USE THE MULTIPART UUID INSTEAD?
	protected LogicMonitorHandler handler = null;
	protected String handlerID;
	public SyncMonitoredType<T> selectedInfo;
	public BlockCoords lastSelected = null;
	public IMonitorInfo lastInfo = null;
	public SyncTagType.BOOLEAN hasMonitor = new SyncTagType.BOOLEAN(-2);
	public int lastPos = -1;

	public MonitorMultipart(String handlerID, double width, double heightMin, double heightMax) {
		super(width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncParts.addAll(Lists.newArrayList(list, uuid, hasMonitor));
		selectedInfo = new SyncMonitoredType<T>(-4);
	}

	public MonitorMultipart(String handlerID, EnumFacing face, double width, double heightMin, double heightMax) {
		super(face, width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncParts.addAll(Lists.newArrayList(list, uuid, hasMonitor));
		selectedInfo = new SyncMonitoredType<T>(-4);
	}

	public void updateAllInfo() {
		for (int i = 0; i < getMaxInfo(); i++) {
			IMonitorInfo info = getMonitorInfo(i);
			InfoUUID id = new InfoUUID(getIdentity().hashCode(), i);
			LogicMonitorManager.changeInfo(id, info);
		}
	}

	@Override
	public void onPartChanged(IMultipart changedPart) {
		if (!this.getWorld().isRemote) {
			if (changedPart instanceof ScreenMultipart) {
				ScreenMultipart screen = (ScreenMultipart) changedPart;
				if (screen.face == this.face) {
					hasMonitor.setObject(!screen.wasRemoved());
					sendUpdatePacket(true);
				}
			}
		}
	}

	public void onLoaded() {
		super.onLoaded();
		LogicMonitorManager.addMonitor(this);
		updateAllInfo();
	}

	public void onRemoved() {
		super.onRemoved();
		LogicMonitorManager.removeMonitor(this);
	}

	public void onUnloaded() {
		super.onUnloaded();
		LogicMonitorManager.removeMonitor(this);
	}

	public void onFirstTick() {
		super.onFirstTick();
		setUUID();
		LogicMonitorManager.addMonitor(this);
		hasMonitor.setObject(LogisticsAPI.getCableHelper().getDisplayScreen(getCoords(), face) != null);
	}

	public void setUUID() {
		if (this.getWorld() != null && !this.getWorld().isRemote) {
			if (uuid.getUUID() == null) {
				uuid.setObject(UUID.randomUUID());
				list.setIdentity(uuid.getUUID());
				LogicMonitorManager.monitors.add(this);
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
		boolean isClient = this.isClient();
		ArrayList<MonitorViewer> toRemove = new ArrayList();
		for (Boolean bool : new Boolean[] { true, false }) {
			viewers.putIfAbsent(bool, new ArrayList());
			for (MonitorViewer viewer : viewers.get(bool)) {
				if (viewer.player.getGameProfile().getId().equals(player.getGameProfile().getId())) {
					toRemove.add(viewer);
				}
			}
			toRemove.forEach(viewers.get(bool)::remove);
		}
	}

	public UUID getIdentity() {
		if (uuid.getUUID() == null) {
			setUUID();
		}
		return uuid.getUUID();
	}

	public void setLocalNetworkCache(INetworkCache network) {
		setMonitoredInfo(MonitoredList.newMonitoredList(getNetworkID()));		
		if (!this.network.isFakeNetwork() && this.network.getNetworkID() != network.getNetworkID()) {
			((ILogisticsNetwork) this.network).removeMonitor(this);
		}
		super.setLocalNetworkCache(network);
		if (network instanceof ILogisticsNetwork) {
			ILogisticsNetwork storageCache = (ILogisticsNetwork) network;
			storageCache.<T>addMonitor(this);
		}		
	}

	public MonitoredList<T> getMonitoredList() {
		return this.getNetworkID() == -1 ? MonitoredList.newMonitoredList(getNetworkID()) : LogicMonitorManager.getMonitoredList(this);
	}

	public int getMaxInfo() {
		return 4;
	}

	public void addInfo(List<String> info) {
		super.addInfo(info);
		info.add("Channels Configured: " + !list.isEmpty());
		if (getIdentity() != null)
			info.add("Monitor UUID: " + this.getIdentity());
		info.add("Max Info: " + getMaxInfo());
	}

	public final int ADD = -9, PAIRED = -10, ALL = 100;

	public void modifyCoords(MonitoredBlockCoords coords) {
		lastSelected = coords.syncCoords.getCoords();
		sendByteBufPacket(-3);
	}

	@Override
	public void writeUpdatePacket(PacketBuffer buf) {
		super.writeUpdatePacket(buf);
		hasMonitor.writeToBuf(buf);
	}

	@Override
	public void readUpdatePacket(PacketBuffer buf) {
		super.readUpdatePacket(buf);
		hasMonitor.readFromBuf(buf);
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
			LogicMonitorManager.monitoredLists.put(this, MonitoredList.<T>newMonitoredList(getNetworkID()));
			break;
		}
	}

	@Override
	public IBlockState getActualState(IBlockState state) {
		World w = getContainer().getWorldIn();
		BlockPos pos = getContainer().getPosIn();
		return state.withProperty(ORIENTATION, face).withProperty(hasDisplay, this.hasMonitor.getObject());
	}

	public BlockStateContainer createBlockState() {
		return new BlockStateContainer(MCMultiPartMod.multipart, new IProperty[] { ORIENTATION, hasDisplay });
	}
}
