package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.INT;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.SonarHelper;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityEntityNode extends TileEntityChannelledCable implements IInfoEmitter, ICableRenderer, IEntityNode, IByteBufTile {

	public SyncTagType.INT entityTarget = new SyncTagType.INT(0);
	public SyncTagType.INT entityRange = (INT) new SyncTagType.INT(1).setDefault(10);
	public float rotate = 0;

	public void updateEntity() {
		super.updateEntity();
		if (this.worldObj.isRemote) {
			if (!(rotate >= 1)) {
				rotate += (float) 1 / 100;
			} else {
				rotate = 0;
			}
			return;
		}
	}

	@Override
	public CableType getCableType() {
		return CableType.BLOCK_CONNECTION;
	}

	@Override
	public boolean isBlocked(ForgeDirection dir) {
		return !canConnect(dir);
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != dir.UP;
	}

	@Override
	public CableType canRenderConnection(ForgeDirection dir) {
		if (dir == dir.UP) {
			return CableType.NONE;
		}
		return LogisticsAPI.getCableHelper().canRenderConnection(this, dir, CableType.BLOCK_CONNECTION);
	}

	public void addCable() {
		super.addCable();
		LogisticsAPI.getCableHelper().addConnection(registryID, this.getCoords());
	}

	public void removeCable() {
		super.removeCable();
		LogisticsAPI.getCableHelper().removeConnection(registryID, this.getCoords());
	}

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Entity Node", new BlockCoords(this));
	}

	@Override
	public void addConnections() {
	}

	@Override
	public void removeConnections() {
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(entityTarget, entityRange));
	}
	
	public Entity getNearestEntity() {

		switch (entityTarget.getObject()) {
		case 1:
			return SonarHelper.getNearestEntity(EntityPlayer.class, this, entityRange.getObject());
		case 2:
			return SonarHelper.getNearestEntity(EntityMob.class, this, entityRange.getObject());
		case 3:
			return SonarHelper.getNearestEntity(EntityAnimal.class, this, entityRange.getObject());
		default:
			return SonarHelper.getNearestEntity(Entity.class, this, entityRange.getObject());
		}
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			buf.writeInt(entityTarget.getObject());
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			entityTarget.setObject(buf.readInt());
			break;
		case 1:
			if (entityRange.getObject() != 64)
				entityRange.increaseBy(1);
			break;
		case 2:
			if (entityRange.getObject() != 1)
				entityRange.decreaseBy(1);
			break;
		}
	}

	@Override
	public List<Entity> getEntities() {
		return Arrays.asList(getNearestEntity());
	}
}
