package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.api.BlockCoords;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.network.utils.ITextField;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.connecting.ILogicTile;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.registries.EmitterRegistry;

public class TileEntityDataEmitter extends TileEntityNode implements ITextField, ILogicTile, IByteBufTile {

	public SyncTagType.STRING clientName = (STRING) new SyncTagType.STRING(0).setDefault("Unnamed Emitter");

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.add(clientName);
	}

	public void addToFrequency() {
		if (!this.worldObj.isRemote) {
			EmitterRegistry.addEmitters(playerName, new BlockCoords(this, this.worldObj.provider.dimensionId), isPrivate.getObject());
		}
	}

	public void removeFromFrequency() {
		if (!this.worldObj.isRemote) {
			EmitterRegistry.removeEmitter(playerName, new BlockCoords(this, this.worldObj.provider.dimensionId), isPrivate.getObject());
		}
	}

	public void onChunkUnload() {
		super.onChunkUnload();
		if (!this.worldObj.isRemote) {
			this.removeFromFrequency();
		}
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote) {
			this.addToFrequency();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.worldObj.isRemote) {
			this.removeFromFrequency();
		}
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public void textTyped(String string, int id) {
		if (id == 0) {
			if (string == null || string.isEmpty()) {
				this.clientName.setObject("Unnamed Emitter");
			} else {
				this.clientName.setObject(string);
			}
		}
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir == ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite();
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		if (id == 0) {
			this.isPrivate.invert();
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			this.removeFromFrequency();
			this.isPrivate.invert();
			this.addToFrequency();
		}
	}
}