package sonar.logistics.common.multiparts;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.MCMultiPartMod;
import mcmultipart.multipart.IMultipart;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.ILogisticsNetwork;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IdentifiedCoordsList;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.connections.monitoring.ViewersList;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.network.PacketMonitoredList;
import sonar.logistics.network.SyncMonitoredType;

public abstract class MonitorMultipart<T extends IMonitorInfo> extends SidedMultipart implements ILogicMonitor<T>, IByteBufTile, IChannelledTile {

	public static final PropertyBool hasDisplay = PropertyBool.create("display");
	protected IdentifiedCoordsList list = new IdentifiedCoordsList(-1);
	protected SyncUUID uuid = new SyncUUID(-2); // CAN I USE THE MULTIPART UUID INSTEAD?
	protected LogicMonitorHandler handler = null;
	protected String handlerID;
	public SyncMonitoredType<T> selectedInfo;
	public BlockCoords lastSelected = null;
	public IMonitorInfo lastInfo = null;
	public SyncTagType.BOOLEAN hasMonitor = new SyncTagType.BOOLEAN(-2);
	public ViewersList viewers = new ViewersList(this, MonitorType.ALL);
	public int lastPos = -1;

	public MonitorMultipart(String handlerID, double width, double heightMin, double heightMax) {
		super(width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncList.addParts(list, uuid, hasMonitor);
		selectedInfo = new SyncMonitoredType<T>(-4);
	}

	public MonitorMultipart(String handlerID, EnumFacing face, double width, double heightMin, double heightMax) {
		super(face, width, heightMin, heightMax);
		this.handlerID = handlerID;
		this.syncList.addParts(list, uuid, hasMonitor);
		selectedInfo = new SyncMonitoredType<T>(-4);
	}

	public void updateAllInfo() {
		for (int i = 0; i < getMaxInfo(); i++) {
			IMonitorInfo info = getMonitorInfo(i);
			InfoUUID id = new InfoUUID(getIdentity().hashCode(), i);
			Logistics.getServerManager().changeInfo(id, info);
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
		if (isServer()) {
			Logistics.getServerManager().addMonitor(this);
			updateAllInfo();
		}
	}

	public void onRemoved() {
		super.onRemoved();
		Logistics.getInfoManager(this.getWorld().isRemote).removeMonitor(this);
	}

	public void onUnloaded() {
		super.onUnloaded();
		Logistics.getInfoManager(this.getWorld().isRemote).removeMonitor(this);
	}

	public void onFirstTick() {
		super.onFirstTick();
		if (isServer()) {
			setUUID();
			Logistics.getServerManager().addMonitor(this);
			hasMonitor.setObject(LogisticsAPI.getCableHelper().getDisplayScreen(getCoords(), face) != null);
		} else {
			this.sendByteBufPacket(-4); // request the monitor UUID
		}
	}

	public void setUUID() {
		if (this.getWorld() != null && !this.getWorld().isRemote) {
			if (uuid.getUUID() == null) {
				uuid.setObject(UUID.randomUUID());
				list.setIdentity(uuid.getUUID());
				Logistics.getServerManager().addMonitor(this);
			}
			sendByteBufPacket(-2);
		}
	}

	@Override
	public IdentifiedCoordsList getChannels(int channelID) {
		return list;
	}

	@Override
	public LogicMonitorHandler getHandler() {
		return handler == null ? handler = LogicMonitorHandler.instance(handlerID) : handler;
	}

	public ViewersList getViewersList() {
		return viewers;
	}

	public UUID getIdentity() {
		if (uuid.getUUID() == null) {
			setUUID();
		}
		return uuid.getUUID();
	}

	public void setLocalNetworkCache(INetworkCache network) {
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
		// TODO only using one INFO UUID
		return getNetworkID() == -1 ? MonitoredList.newMonitoredList(getNetworkID()) : Logistics.getInfoManager(this.getWorld().isRemote).getMonitoredList(getNetworkID(), new InfoUUID(this.getIdentity().hashCode(), 0));
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

	public void modifyCoords(MonitoredBlockCoords coords, int channelID) {
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
		case -4:
			break;
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
		case -4:
			sendByteBufPacket(-2);
			break;
		case -3:
			BlockCoords coords = BlockCoords.readFromBuf(buf);
			list.modifyCoords(channelType(), coords);
			sendByteBufPacket(list.tagID);
			break;
		case -2:
			uuid.readFromBuf(buf);
			list.setIdentity(uuid.getUUID());
			Logistics.getInfoManager(this.getWorld().isRemote).addMonitor(this);
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


	@Override
	public void onViewerAdded(EntityPlayer player, List<MonitorType> type) {
		SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
	}

	@Override
	public void onViewerRemoved(EntityPlayer player, List<MonitorType> type) {}
}
