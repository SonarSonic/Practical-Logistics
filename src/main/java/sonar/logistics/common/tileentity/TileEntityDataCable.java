package sonar.logistics.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.IDataCable;
import sonar.logistics.helpers.CableHelper;

public class TileEntityDataCable extends TileEntitySonar implements IDataCable {

	public BlockCoords coords;

	@Override
	public BlockCoords getCoords() {
		return coords;
	}

	@Override
	public void setCoords(BlockCoords coords) {
		if (!BlockCoords.equalCoords(this.coords, coords)) {
			this.coords = coords;
			CableHelper.updateAdjacentCoords(this, coords, true);
		}
	}

	@Override
	public boolean isBlocked(ForgeDirection dir) {
		return false;
	}

	public boolean canRenderConnection(ForgeDirection dir) {
		return CableHelper.canRenderConnection(this, dir);
	}

	public boolean maxRender() {
		return true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			if (nbt.hasKey("coords")) {
				if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
					coords = BlockCoords.readFromNBT(nbt.getCompoundTag("coords"));
				} else {
					coords = null;
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			if (coords != null) {
				BlockCoords.writeToNBT(infoTag, coords);
				infoTag.setBoolean("hasCoords", true);
			} else {
				infoTag.setBoolean("hasCoords", false);
			}
			nbt.setTag("coords", infoTag);

		}
	}
	public void validate(){
		this.coords=null;
		super.validate();
	}
	public void invalidate(){
		this.coords=null;
		super.invalidate();
		CableHelper.updateAdjacentCoords(worldObj, xCoord, yCoord, zCoord, null, true);
	}
}
