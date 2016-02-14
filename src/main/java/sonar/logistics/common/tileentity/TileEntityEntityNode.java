package sonar.logistics.common.tileentity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import scala.actors.threadpool.Arrays;
import sonar.core.network.sync.SyncInt;
import sonar.core.network.utils.IByteBufTile;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityEntityNode extends TileEntityConnection implements ICableRenderer, IInfoEmitter, IByteBufTile, IEntityNode {

	public SyncInt entityTarget = new SyncInt(0);
	public SyncInt entityRange = new SyncInt(1, 10);
	public float rotate = 0;

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Entity Node", new BlockCoords(this));
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir != dir.UP;
	}

	@Override
	public int canRenderConnection(ForgeDirection dir) {
		if (dir == dir.UP) {
			return 0;
		}
		return LogisticsAPI.getCableHelper().canRenderConnection(this, dir);
	}

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

	public boolean maxRender() {
		return true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			entityTarget.readFromNBT(nbt, type);
			entityRange.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			entityTarget.writeToNBT(nbt, type);
			entityRange.writeToNBT(nbt, type);
		}
	}

	public Entity getNearestEntity() {

		switch (entityTarget.getInt()) {
		case 1:
			return SonarHelper.getNearestEntity(EntityPlayer.class, this, entityRange.getInt());
		case 2:
			return SonarHelper.getNearestEntity(EntityMob.class, this, entityRange.getInt());
		case 3:
			return SonarHelper.getNearestEntity(EntityAnimal.class, this, entityRange.getInt());
		default:
			return SonarHelper.getNearestEntity(Entity.class, this, entityRange.getInt());
		}
	}

	@Override
	public void addConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != dir.UP) {
				LogisticsAPI.getCableHelper().addConnection(this, dir);
			}
		}
	}

	@Override
	public void removeConnections() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.getOrientation(i);
			if (dir != dir.UP) {
				LogisticsAPI.getCableHelper().removeConnection(this, dir);
			}
		}
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			buf.writeInt(entityTarget.getInt());
			break;
		case 1:
			//buf.writeInt(entityRange.getInt());
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			entityTarget.setInt(buf.readInt());
			break;
		case 1:
			
			if (entityRange.getInt() != 64)
				entityRange.increaseBy(1);
			break;
		case 2:
			if (entityRange.getInt() != 1)
				entityRange.decreaseBy(1);
			break;
		}
	}

	@Override
	public List<Entity> getEntities() {
		return Arrays.asList(new Object[]{getNearestEntity()});
	}
}
