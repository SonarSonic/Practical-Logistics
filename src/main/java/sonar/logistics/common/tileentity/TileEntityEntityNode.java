package sonar.logistics.common.tileentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.network.sync.SyncInt;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.IMachineButtons;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.core.utils.helpers.SonarHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityEntityNode extends TileEntitySonar implements ICableRenderer, IDataConnection, IMachineButtons {

	public SyncInt entityTarget = new SyncInt(0);
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
	public boolean canRenderConnection(ForgeDirection dir) {
		if (dir == dir.UP) {
			return false;
		}
		return CableHelper.canRenderConnection(this, dir);
	}

	@Override
	public void updateData(ForgeDirection dir) {

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
		CableHelper.updateAdjacentCoords(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, new ForgeDirection[] { ForgeDirection.UP });
		
	}

	public boolean maxRender() {
		return true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			entityTarget.readFromNBT(nbt, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			entityTarget.writeToNBT(nbt, type);
		}
	}

	@Override
	public void buttonPress(int buttonID, int value) {
		switch (buttonID) {
		case 0:
			entityTarget.setInt(value);
			break;
		}
	}

	public Entity getNearestEntity() {

		switch (entityTarget.getInt()) {
		case 1: return SonarHelper.getNearestEntity(EntityPlayer.class, this, 10);
		case 2: return SonarHelper.getNearestEntity(EntityMob.class, this, 10);
		case 3: return SonarHelper.getNearestEntity(EntityAnimal.class, this, 10);
		default:
			return SonarHelper.getNearestEntity(Entity.class, this, 10);
		}
	}
}
