package sonar.logistics.common.multiparts;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import sonar.core.api.IFlexibleGui;
import sonar.core.helpers.NBTHelper;
import sonar.core.integration.multipart.SonarMultipartHelper;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.LogisticsItems;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.client.gui.GuiDataEmitter;
import sonar.logistics.connections.managers.EmitterManager;
import sonar.logistics.helpers.LogisticsHelper;

public class DataEmitterPart extends SidedMultipart implements IDataEmitter, IFlexibleGui, IByteBufTile {

	public ArrayList<IDataReceiver> receivers = new ArrayList();

	public SyncTagType.STRING emitterName = (STRING) new SyncTagType.STRING(2).setDefault("Unnamed Emitter");
	public SyncUUID playerUUID = new SyncUUID(3);
	public SyncUUID emitterUUID = new SyncUUID(4);
	{
		syncList.addParts(emitterName, playerUUID, emitterUUID);
	}

	public DataEmitterPart() {
		super(0.0625*5, 0.0625/2, 0.0625*4);
	}

	public DataEmitterPart(EntityPlayer player, EnumFacing dir) {
		super(dir, 0.0625*5, 0.0625/2, 0.0625*4);
		playerUUID.setObject(player.getGameProfile().getId());
	}

	public void onLoaded() {
		super.onLoaded();
		// EmitterRegistry.addEmitter(this);
	}

	public void onRemoved() {
		super.onRemoved();
		if (isServer()) {
			EmitterManager.removeEmitter(this);
		}
	}

	public void onUnloaded() {
		super.onUnloaded();
		if (isServer()) {
			EmitterManager.removeEmitter(this);
		}
	}

	public void onFirstTick() {
		super.onFirstTick();
		if (isServer()) {
			if (emitterUUID.getUUID() == null) {
				emitterUUID.setObject(UUID.randomUUID());
			}
			sendByteBufPacket(playerUUID.id);
			EmitterManager.addEmitter(this);
		}
	}

	/*
	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return getOccludedSlots();
	}

	@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		return EnumSet.of(PartSlot.getFaceSlot(face.getOpposite()), PartSlot.CENTER);
	}
	*/
	
	@Override
	public boolean canPlayerConnect(UUID uuid) {
		return true;
	}

	@Override
	public boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, PartMOP hit) {
		if (!LogisticsHelper.isPlayerUsingOperator(player)) {
			if (!getWorld().isRemote) {
				SonarMultipartHelper.sendMultipartSyncToPlayer(this, (EntityPlayerMP) player);
				openFlexibleGui(player, 0);
			}
			return true;
		}
		return false;
	}

	@Override
	public UUID getIdentity() {
		return emitterUUID.getUUID();
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		if (network.getNetworkID() != this.getNetworkID())
			EmitterManager.emitterChanged(this);
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		ISyncPart part = NBTHelper.getSyncPartByID(syncList.getStandardSyncParts(), id);
		if (part != null)
			part.writeToBuf(buf);
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		ISyncPart part = NBTHelper.getSyncPartByID(syncList.getStandardSyncParts(), id);
		if (part != null)
			part.readFromBuf(buf);
	}

	@Override
	public String getEmitterName() {
		return emitterName.getObject();
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
		super.addSelectionBoxes(list);
		/*
		double p = 0.0625;
		double height = p * 12, width = p * 8, length = p * 14;

		switch (face) {
		case EAST:
			list.add(new AxisAlignedBB(length, 0, (width) / 2, 0, height, 1 - width / 2));
			break;
		case NORTH:
			list.add(new AxisAlignedBB((width) / 2, 0, 1, 1 - width / 2, height, 1 - length));
			break;
		case SOUTH:
			list.add(new AxisAlignedBB((width) / 2, 0, length, 1 - width / 2, height, 0));
			break;
		case WEST:
			list.add(new AxisAlignedBB(1, 0, (width) / 2, 1 - length, height, 1 - width / 2));
			break;
		default:
			break;

		}
		*/
	}

	@Override
	public void connect(IDataReceiver receiver) {
		receivers.add(receiver);
	}

	@Override
	public void disconnect(IDataReceiver receiver) {
		receivers.remove(receiver);
	}

	@Override
	public ArrayList<Integer> getNetworks() {
		ArrayList<Integer> networks = new ArrayList();
		for (IDataReceiver receiver : receivers) {
			int id = receiver.getNetworkID();
			if (!networks.contains(id)) {
				networks.add(id);
			}
		}
		return networks;
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new ContainerMultipartSync(this);
		}
		return null;
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		switch (id) {
		case 0:
			return new GuiDataEmitter(this);
		}
		return null;
	}

	@Override
	public ItemStack getItemStack() {
		return new ItemStack(LogisticsItems.partEmitter);
	}
}
