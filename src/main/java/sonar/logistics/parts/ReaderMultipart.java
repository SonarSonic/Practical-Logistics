package sonar.logistics.parts;

import java.util.ArrayList;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import sonar.core.SonarCore;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketMultipartSync;
import sonar.core.network.sync.ISyncPart;
import sonar.core.utils.IGuiTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.ProgressInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IReader;
import sonar.logistics.api.info.monitor.MonitorHandler;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.network.SyncMonitoredType;

public abstract class ReaderMultipart<T extends IMonitorInfo> extends MonitorMultipart<T> implements ISlottedPart, IReader<T> {

	private SyncMonitoredType<T>[] selected = new SyncMonitoredType[getMaxInfo()], paired = new SyncMonitoredType[getMaxInfo()];
	private ArrayList<IMonitorInfo> cachedSelected, cachedPaired;
	private ArrayList<IMonitorInfo> cachedInfo; // final info list to be provided to the network...

	public void buildSyncList() {
		for (int i = 0; i < selected.length; i++) {
			selected[i] = new SyncMonitoredType<T>(handlerID, i + 1);
			paired[i] = new SyncMonitoredType<T>(handlerID, i + 1 + 10);
			syncParts.add(selected[i]);
			syncParts.add(paired[i]);
		}
	}

	public ReaderMultipart(String handlerID) {
		super(handlerID, 6 * 0.0625, 0.0625 * 1, 0.0625 * 6);
		buildSyncList();
	}

	public ReaderMultipart(String handlerID, EnumFacing face) {
		super(handlerID, face, 6 * 0.0625, 0.0625 * 1, 0.0625 * 6);
		buildSyncList();
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!this.getWorld().isRemote && this instanceof IGuiTile) {
			this.addViewer(new MonitorViewer(player, MonitorType.INFO));
			SonarCore.network.sendTo(new PacketMultipartSync(getPos(), this.writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE), SyncType.SYNC_OVERRIDE, getUUID()), (EntityPlayerMP) player);
			this.openGui(player, Logistics.instance);
		}
		return true;
	}

	@Override
	public ArrayList<IMonitorInfo> getSelectedInfo() {
		if (cachedSelected == null) {
			ArrayList<IMonitorInfo> info = new ArrayList();
			for (SyncMonitoredType<T> sync : selected) {
				info.add(sync.getMonitoredInfo());
			}
			cachedSelected = info;
		}
		return cachedSelected;
	}

	@Override
	public ArrayList<IMonitorInfo> getPairedInfo() {
		if (cachedPaired == null) {
			ArrayList<IMonitorInfo> info = new ArrayList();
			for (SyncMonitoredType<T> sync : paired) {
				info.add(sync.getMonitoredInfo());
			}
			cachedPaired = info;
		}
		return cachedPaired;
	}

	public ArrayList<IMonitorInfo> getCachedInfo(boolean forceUpdate) {
		if (cachedInfo == null || forceUpdate) {
			ArrayList<IMonitorInfo> infoList = new ArrayList();
			for (int i = 0; i < this.getMaxInfo(); i++) {
				IMonitorInfo info = cachedSelected.get(i);
				if (info != null) {
					IMonitorInfo paired = cachedPaired.get(i);
					if (paired != null) {
						infoList.add(new ProgressInfo(null, null));
						continue;
					}
				}
				infoList.add(info);
			}
			this.cachedInfo = (ArrayList<IMonitorInfo>) infoList;
		}
		return cachedInfo;

	}

	@Override
	public IMonitorInfo getMonitorInfo(int pos) {
		return getCachedInfo(false).get(pos);
	}

	// this is kind of messy, could be made better for sure
	@Override
	public void addInfo(T info, int type, int newPos) {
		SyncMonitoredType<T>[] syncInfo = type == 0 ? selected : paired;
		if (newPos == -1) {
			int pos = 0;
			for (SyncMonitoredType<T> sync : syncInfo) {
				if (sync.getMonitoredInfo() != null) {
					if (sync.getMonitoredInfo().isMatchingType(info) && sync.getMonitoredInfo().isMatchingInfo((T) info)) {
						sync.setInfo(null);
						(type != 0 ? selected : paired)[pos].setInfo(null);
						sendByteBufPacket(100);
						lastPos = -1;
						return;
					}
				} else if (newPos == -1) {
					newPos = pos;
				}
				pos++;
			}
		}
		if (newPos != -1)
			lastPos = newPos;
		syncInfo[newPos == -1 ? 0 : newPos].setInfo(info);
		sendByteBufPacket(100);
	}

	@Override
	public boolean canConnect(EnumFacing dir) {
		return true;// dir != face;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		ISyncPart part = syncParts.getPartByID(id);
		if (part != null)
			part.writeToBuf(buf);
		if (id == 100) {
			for (SyncMonitoredType<T> sync : selected) {
				sync.writeToBuf(buf);
			}
			for (SyncMonitoredType<T> sync : paired) {
				sync.writeToBuf(buf);
			}
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		ISyncPart part = syncParts.getPartByID(id);
		if (part != null)
			part.readFromBuf(buf);
		if (id == 100) {
			for (SyncMonitoredType<T> sync : selected) {
				sync.readFromBuf(buf);
			}
			for (SyncMonitoredType<T> sync : paired) {
				sync.readFromBuf(buf);
			}
			cachedSelected = null;
			cachedPaired = null;
		}

	}
}
