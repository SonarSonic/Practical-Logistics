package sonar.logistics.parts;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import mcmultipart.multipart.ISlotOccludingPart;
import mcmultipart.multipart.PartSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import sonar.core.api.utils.BlockCoords;
import sonar.core.helpers.NBTHelper;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.sync.SyncUUID;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.api.connecting.IDataEmitter;
import sonar.logistics.connections.EmitterRegistry;
import sonar.logistics.connections.LogicMonitorCache;

public class DataEmitterPart extends FacingMultipart implements IDataEmitter, ISlotOccludingPart, IByteBufTile {

	public SyncTagType.STRING emitterName = (STRING) new SyncTagType.STRING(2).setDefault("Unnamed Emitter");
	public SyncUUID playerUUID = new SyncUUID(3);
	public SyncUUID emitterUUID = new SyncUUID(4);
	{
		syncParts.addAll(Lists.newArrayList(emitterName, playerUUID, emitterUUID));
	}

	public DataEmitterPart() {
		super();
	}

	public DataEmitterPart(EntityPlayer player, EnumFacing dir) {
		super(dir);
		playerUUID.setObject(player.getGameProfile().getId());
	}

	public void onLoaded() {
		super.onLoaded();
		// EmitterRegistry.addEmitter(this);
	}

	public void onRemoved() {
		super.onRemoved();
		if (isServer()) {
			EmitterRegistry.removeEmitter(this);
		}
	}

	public void onUnloaded() {
		super.onUnloaded();
		if (isServer()) {
			EmitterRegistry.removeEmitter(this);
		}
	}

	public void onFirstTick() {
		super.onFirstTick();
		if (isServer()) {
			if (emitterUUID.getUUID() == null) {
				emitterUUID.setObject(UUID.randomUUID());
			}
			sendByteBufPacket(playerUUID.id);
			EmitterRegistry.addEmitter(this);
		}
	}

	@Override
	public EnumSet<PartSlot> getSlotMask() {
		return getOccludedSlots();
	}

	@Override
	public EnumSet<PartSlot> getOccludedSlots() {
		return EnumSet.of(PartSlot.getFaceSlot(face.getOpposite()), PartSlot.CENTER);
	}

	@Override
	public boolean canPlayerConnect(UUID uuid) {
		return true;
	}

	@Override
	public UUID getIdentity() {
		return emitterUUID.getUUID();
	}

	public void setLocalNetworkCache(INetworkCache network) {
		super.setLocalNetworkCache(network);
		EmitterRegistry.emitterChanged(this);

	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		ISyncPart part = NBTHelper.getSyncPartByID(syncParts, id);
		if (part != null)
			part.writeToBuf(buf);
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		ISyncPart part = NBTHelper.getSyncPartByID(syncParts, id);
		if (part != null)
			part.readFromBuf(buf);
	}

	@Override
	public String getEmitterName() {
		return emitterName.getObject();
	}

	public void addSelectionBoxes(List<AxisAlignedBB> list) {
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
	}
}
