package sonar.logistics.common.multiparts;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.ISlottedPart;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import sonar.core.api.IFlexibleGui;
import sonar.core.helpers.NBTHelper;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.network.sync.ISyncPart;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.InfoUUID;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.IReader;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.helpers.LogisticsHelper;

public abstract class ReaderMultipart<T extends IMonitorInfo> extends MonitorMultipart<T> implements ISlottedPart, IReader<T>, IFlexibleGui {

	public ReaderMultipart(String handlerID) {
		super(handlerID, 6 * 0.0625, 0.0625 * 1, 0.0625 * 6);
	}

	public ReaderMultipart(String handlerID, EnumFacing face) {
		super(handlerID, face, 6 * 0.0625, 0.0625 * 1, 0.0625 * 6);
	}

	public void update() {
		super.update();
		// ILogicMonitor monitor = network.getLocalMonitor();

	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!LogisticsHelper.isPlayerUsingOperator(player)) {
			if (!getWorld().isRemote) {
				SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
				viewers.addViewer(player, MonitorType.FULL_INFO);
				openFlexibleGui(player, 0);
			}
			return true;
		}
		return false;
	}

	@Override
	public IMonitorInfo getMonitorInfo(int pos) {
		return Logistics.getInfoManager(this.getWorld().isRemote).getInfoList().get(new InfoUUID(getIdentity().hashCode(), pos));
	}

	@Override
	public ConnectionType canConnect(EnumFacing dir) {
		return dir != face ? ConnectionType.NETWORK : ConnectionType.VISUAL;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		ISyncPart part = NBTHelper.getSyncPartByID(syncList.getStandardSyncParts(), id);
		if (part != null)
			part.writeToBuf(buf);
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		ISyncPart part = NBTHelper.getSyncPartByID(syncList.getStandardSyncParts(), id);
		if (part != null)
			part.readFromBuf(buf);
	}
}
