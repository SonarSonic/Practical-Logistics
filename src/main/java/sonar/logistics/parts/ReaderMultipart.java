package sonar.logistics.parts;

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
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.info.monitor.MonitorViewer;

public abstract class ReaderMultipart<T extends IMonitorInfo> extends MonitorMultipart<T> implements ISlottedPart {

	public ReaderMultipart(String handlerID) {
		super(handlerID, 6 * 0.0625, 0.0625 * 0, 0.0625 * 6);
	}

	public ReaderMultipart(String handlerID, EnumFacing face) {
		super(handlerID, face, 6 * 0.0625, 0.0625 * 0, 0.0625 * 6);
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
	public boolean canConnect(EnumFacing dir) {
		return true;// dir != face;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		super.writePacket(buf, id);
		ISyncPart part = syncParts.getPartByID(id);
		if (part != null)
			part.writeToBuf(buf);
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		super.readPacket(buf, id);
		ISyncPart part = syncParts.getPartByID(id);
		if (part != null)
			part.readFromBuf(buf);
	}
}
