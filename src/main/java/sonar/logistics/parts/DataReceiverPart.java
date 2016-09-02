package sonar.logistics.parts;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.ISlotOccludingPart;
import mcmultipart.multipart.PartSlot;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.PacketMultipartSync;
import sonar.core.network.sync.SyncNBTAbstract;
import sonar.core.network.sync.SyncNBTAbstractList;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.cache.IRefreshCache;
import sonar.logistics.api.cache.RefreshType;
import sonar.logistics.api.connecting.ClientDataEmitter;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.api.info.monitor.ChannelType;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.MonitorType;
import sonar.logistics.api.info.monitor.MonitorViewer;
import sonar.logistics.client.gui.GuiDataReceiver;
import sonar.logistics.common.containers.ContainerDataReceiver;
import sonar.logistics.connections.EmitterRegistry;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.LogisticsHelper;
import sonar.logistics.network.SyncMonitoredType;

public class DataReceiverPart extends FacingMultipart implements IDataReceiver, ISlotOccludingPart, IFlexibleGui, IByteBufTile {

	public SyncNBTAbstractList<ClientDataEmitter> clientEmitters = new SyncNBTAbstractList<ClientDataEmitter>(ClientDataEmitter.class, 2);
	public SyncUUID playerUUID = new SyncUUID(3);
	public SyncNBTAbstract<ClientDataEmitter> selectedEmitter = new SyncNBTAbstract<ClientDataEmitter>(ClientDataEmitter.class, 4);
	// public ArrayList<IDataEmitter> emitters = new ArrayList();
	public ArrayList<Integer> networks = new ArrayList();
	{
		syncParts.addAll(Lists.newArrayList(clientEmitters, playerUUID, selectedEmitter));
	}

	public DataReceiverPart() {
		super();
	}

	public DataReceiverPart(EntityPlayer player, EnumFacing dir) {
		super(dir);
		playerUUID.setObject(player.getGameProfile().getId());
	}

	public void update() {
		super.update();
		if (isClient()) {
			return;
		}
		if (EmitterRegistry.dirty) {
			networks = getNetworks();
			network.markDirty(RefreshType.CONNECTED_NETWORKS);
		}
	}

	public ArrayList<Integer> getNetworks() {
		ArrayList<Integer> networks = new ArrayList();
		ArrayList<IDataEmitter> emitters = getEmitters();
		for (IDataEmitter emitter : emitters) {
			if (emitter.getNetworkID() != -1) {
				networks.add(emitter.getNetworkID());
			}
		}
		return networks;
	}

	public ArrayList<IDataEmitter> getEmitters() {
		ArrayList<IDataEmitter> emitters = new ArrayList();
		for (ClientDataEmitter dataEmitter : clientEmitters.getObjects()) {
			IDataEmitter emitter = EmitterRegistry.getEmitter(dataEmitter.getIdentity());
			if (emitter != null && emitter.canPlayerConnect(playerUUID.getUUID())) {
				emitters.add(emitter);
			}
		}
		return emitters;
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!LogisticsHelper.isPlayerUsingOperator(player)) {
			if (!getWorld().isRemote) {
				SonarCore.network.sendTo(new PacketMultipartSync(getPos(), writeData(new NBTTagCompound(), SyncType.SYNC_OVERRIDE), SyncType.SYNC_OVERRIDE, getUUID()), (EntityPlayerMP) player);
				EmitterRegistry.addViewer(player);
				openBasicGui(player, 0);
			}
			return true;
		}
		return false;
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		network.markDirty(RefreshType.CONNECTED_NETWORKS);
	}

	@Override
	public ArrayList<Integer> getConnectedNetworks() {
		return networks;
	}

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return getOccludedSlots();
	}

	@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		return EnumSet.of(PartSlot.getFaceSlot(face), PartSlot.CENTER);
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerDataReceiver(this);
		}
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiDataReceiver(this);
		}
		return null;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			selectedEmitter.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			selectedEmitter.readFromBuf(buf);
			clientEmitters.addObject(selectedEmitter.getObject());
			networks = getNetworks();
			network.markDirty(RefreshType.CONNECTED_NETWORKS);
			sendSyncPacket();
			break;
		}
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		double p = 0.0625;
		double height = p * 12, width = p * 8, length = p * 14;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(1, 0, (width) / 2, 1 - length, height, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, 0, length, 1 - width / 2, height, 0));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, 0, 1, 1 - width / 2, height, 1 - length));
			break;
		case WEST:
			list.add(new AxisAlignedBB(length, 0, (width) / 2, 0, height, 1 - width / 2));
			break;
		default:
			break;

		}
	}
}
