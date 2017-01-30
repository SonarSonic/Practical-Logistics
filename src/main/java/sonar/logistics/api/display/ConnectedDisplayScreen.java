package sonar.logistics.api.display;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import sonar.core.api.nbt.INBTSyncable;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.IDirtyPart;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.ISyncableListener;
import sonar.core.network.sync.SyncCoords;
import sonar.core.network.sync.SyncEnum;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncableList;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.ConnectableType;
import sonar.logistics.api.connecting.INetworkConnectable;
import sonar.logistics.api.info.IInfoContainer;
import sonar.logistics.api.info.monitor.ILogicMonitor;
import sonar.logistics.api.viewers.ViewerTally;
import sonar.logistics.api.viewers.ViewerType;
import sonar.logistics.api.viewers.ViewersList;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.network.PacketConnectedDisplayScreen;

/** used with Large Display Screens so they all have one uniform InfoContainer, Viewer list etc. */
public class ConnectedDisplayScreen implements IInfoDisplay, INetworkConnectable, INBTSyncable, IScaleableDisplay, ISyncPart {

	public ViewersList viewers = new ViewersList(this, Lists.newArrayList(ViewerType.INFO, ViewerType.FULL_INFO));
	private int registryID = -1;
	public ILargeDisplay topLeftScreen = null;
	public SyncableList syncParts = new SyncableList(this);
	public SyncEnum<EnumFacing> face = new SyncEnum(EnumFacing.VALUES, 0);
	public SyncEnum<ScreenLayout> layout = new SyncEnum(ScreenLayout.values(), 1);
	public SyncTagType.INT width = new SyncTagType.INT(2), height = new SyncTagType.INT(3);
	public SyncTagType.BOOLEAN canBeRendered = new SyncTagType.BOOLEAN(4);
	public InfoContainer container = new InfoContainer(this);
	public SyncCoords topLeftCoords = new SyncCoords(5);
	// public double[] scaling = null;
	public boolean hasChanged = true;
	public boolean sendViewers;

	// server side
	public ArrayList<ILargeDisplay> displays = new ArrayList(); // cached

	{
		syncParts.addParts(face, layout, width, height, canBeRendered, topLeftCoords, container);
	}

	public ConnectedDisplayScreen(ILargeDisplay display) {
		registryID = display.getRegistryID();
		face.setObject(display.getFace());
		this.hasChanged = true;
	}

	public ConnectedDisplayScreen(int registryID) {
		this.registryID = registryID;
		this.hasChanged = true;
	}

	public void update(int registryID) {
		if (sendViewers) {
			sendViewers();
		}
		if (hasChanged || this.registryID != registryID) {
			this.registryID = registryID;
			displays = Logistics.getDisplayManager().getConnections(registryID);
			if (!displays.isEmpty()) {
				if (!displays.get(0).getCoords().getWorld().isRemote) {
					setDisplayScaling(displays.get(0), displays);
				}
			}
			hasChanged = false;
			sendViewers = true;
		}
	}

	public void sendViewers() {
		ArrayList<EntityPlayer> players = getViewersList().getViewers(false, ViewerType.INFO, ViewerType.FULL_INFO);
		if (!players.isEmpty()) {
			players.forEach(player -> Logistics.network.sendTo(new PacketConnectedDisplayScreen(this, registryID), (EntityPlayerMP) player));
			sendViewers = false;
		} else {
			sendViewers = true;
		}
	}

	public void setDisplayScaling(ILargeDisplay primary, ArrayList<ILargeDisplay> displays) {
		BlockCoords primaryCoords = primary.getCoords();
		int minX = primaryCoords.getX();
		int maxX = primaryCoords.getX();
		int minY = primaryCoords.getY();
		int maxY = primaryCoords.getY();
		int minZ = primaryCoords.getZ();
		int maxZ = primaryCoords.getZ();

		EnumFacing meta = primary.getFace();
		boolean north = meta == EnumFacing.NORTH;
		for (ILargeDisplay display : displays) {
			BlockCoords coords = display.getCoords();

			if (coords.getX() > maxX) {
				maxX = coords.getX();
			} else if (coords.getX() < minX) {
				minX = coords.getX();
			}
			if (coords.getY() > maxY) {
				maxY = coords.getY();
			} else if (coords.getY() < minY) {
				minY = coords.getY();
			}
			if (coords.getZ() > minZ) {
				maxZ = coords.getZ();
			} else if (coords.getZ() < minZ) {
				minZ = coords.getZ();
			}

		}
		switch (meta.getAxis()) {
		case X:
			this.width.setObject(maxZ - minZ);
			this.height.setObject(maxY - minY);
			break;
		case Y:
			this.width.setObject(maxX - minX);
			this.height.setObject(maxZ - minZ);
			break;
		case Z:
			this.width.setObject(maxX - minX);
			this.height.setObject(maxY - minY);
			break;
		default:
			break;
		}

		for (int x = Math.min(minX, maxX); x <= Math.max(minX, maxX); x++) {
			for (int y = Math.min(minY, maxY); y <= Math.max(minY, maxY); y++) {
				for (int z = Math.min(minZ, maxZ); z <= Math.max(minZ, maxZ); z++) {
					BlockCoords coords = new BlockCoords(x, y, z);
					IInfoDisplay display = LogisticsAPI.getCableHelper().getDisplayScreen(coords, meta);
					if (display == null && !(display instanceof ILargeDisplay)) {
						this.canBeRendered.setObject(false);
						return;
					}
					AxisDirection dir = meta.getAxisDirection();
					if (meta.getAxis() == Axis.X) {
						dir = dir == AxisDirection.POSITIVE ? AxisDirection.NEGATIVE : AxisDirection.POSITIVE;
					}
					boolean isTopLeft = (dir == AxisDirection.POSITIVE && x == minX && y == maxY && z == minZ) || (dir == AxisDirection.NEGATIVE && x == maxX && y == maxY && z == maxZ);
					setTopLeftScreen((ILargeDisplay) display, isTopLeft);
				}
			}
		}
		this.canBeRendered.setObject(true);
	}

	public ArrayList<ILogicMonitor> getLogicMonitors(ArrayList<ILogicMonitor> monitors) {
		displays = Logistics.getDisplayManager().getConnections(registryID);
		for (ILargeDisplay display : displays) {
			if (display instanceof ScreenMultipart) {
				monitors = Logistics.getServerManager().getLocalMonitors(monitors, (ScreenMultipart) display);
			}
		}
		return monitors;
	}

	public void setHasChanged() {
		hasChanged = true;
	}

	public void setTopLeftScreen(ILargeDisplay display, boolean isTopLeft) {
		display.setConnectedDisplay(this);
		if (isTopLeft) {
			topLeftScreen = display;
			this.topLeftCoords.setCoords(display.getCoords());
			display.setShouldRender(true);
			face.setObject(display.getFace());
			if (!display.getCoords().getWorld().isRemote)
				Logistics.getServerManager().addDisplay(display);
		} else {
			display.setShouldRender(false);
			if (!display.getCoords().getWorld().isRemote)
				Logistics.getServerManager().removeDisplay(display);
		}

	}

	@Override
	public IInfoContainer container() {
		return container;
	}

	@Override
	public ScreenLayout getLayout() {
		return layout.getObject();
	}

	@Override
	public DisplayType getDisplayType() {
		return DisplayType.LARGE;
	}

	@Override
	public int maxInfo() {
		return topLeftScreen != null ? topLeftScreen.maxInfo() : 4;
	}

	@Override
	public EnumFacing getFace() {
		return topLeftScreen != null ? topLeftScreen.getFace() : EnumFacing.NORTH;
	}

	@Override
	public ConnectionType canConnect(EnumFacing dir) {
		return ConnectionType.NETWORK;
	}

	@Override
	public BlockCoords getCoords() {
		return topLeftScreen != null ? topLeftScreen.getCoords() : null;
	}

	@Override
	public int getNetworkID() {
		return topLeftScreen != null ? topLeftScreen.getNetworkID() : -1;
	}

	@Override
	public void setLocalNetworkCache(INetworkCache network) {
	}

	@Override
	public ConnectableType getCableType() {
		return ConnectableType.CONNECTION;
	}

	@Override
	public void addToNetwork() {
	}

	@Override
	public void removeFromNetwork() {
	}

	@Override
	public int getRegistryID() {
		return registryID;
	}

	@Override
	public void setRegistryID(int id) {
		this.registryID = id;
		this.hasChanged = true;
	}

	@Override
	public boolean canConnectOnSide(EnumFacing dir) {
		return true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		if (nbt.hasKey(this.getTagName())) {
			NBTTagCompound tag = nbt.getCompoundTag(this.getTagName());
			NBTHelper.readSyncParts(tag, type, this.syncParts);
			// layout.readData(tag, type);
			container.resetRenderProperties();
		}
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		NBTTagCompound tag = new NBTTagCompound();
		NBTHelper.writeSyncParts(tag, type, this.syncParts, true);
		// layout.writeData(tag, type);
		if (!tag.hasNoTags())
			nbt.setTag(this.getTagName(), tag);
		return nbt;
	}

	public ILargeDisplay getTopLeftScreen() {
		if (topLeftCoords.getCoords() != null) {
			this.topLeftScreen = (ILargeDisplay) LogisticsAPI.getCableHelper().getDisplayScreen(topLeftCoords.getCoords(), face.getObject());
		}
		return topLeftScreen;
	}

	@Override
	public double[] getScaling() {
		double max = Math.min(this.height.getObject().intValue() + 1, this.width.getObject().intValue() + 1);
		return new double[] { this.getDisplayType().width + this.width.getObject().intValue(), this.getDisplayType().height + this.height.getObject().intValue(), max / 100 };
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, this.writeData(new NBTTagCompound(), SyncType.SAVE));
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		readData(ByteBufUtils.readTag(buf), SyncType.SAVE);
		container.resetRenderProperties();
	}

	@Override
	public boolean canSync(SyncType sync) {
		return SyncType.isGivenType(sync, SyncType.DEFAULT_SYNC, SyncType.SAVE);
	}

	@Override
	public String getTagName() {
		return "connected";
	}

	@Override
	public ISyncableListener getListener() {
		return this.getTopLeftScreen();
	}

	@Override
	public IDirtyPart setListener(ISyncableListener listener) {
		if (listener instanceof ILargeDisplay) {
			if (listener != topLeftScreen) {
				setTopLeftScreen((ILargeDisplay) listener, true);
			}
		}
		return this;
	}

	@Override
	public void markChanged(IDirtyPart part) {
		syncParts.markSyncPartChanged(part);
		if (this.getTopLeftScreen() != null) {
			this.getTopLeftScreen().markChanged(this);
		}
	}

	@Override
	public ViewersList getViewersList() {
		return viewers;
	}

	@Override
	public UUID getIdentity() {
		return null;
	}

	@Override
	public void onViewerAdded(EntityPlayer player, List<ViewerTally> type) {
	}

	@Override
	public void onViewerRemoved(EntityPlayer player, List<ViewerTally> type) {
	}
}