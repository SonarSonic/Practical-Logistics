package sonar.logistics.common.tileentity;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.connecting.IMultiDataCable;
import sonar.logistics.helpers.CableHelper;

public class TileEntityMultiDataCable extends TileEntitySonar implements IMultiDataCable {

	public List<BlockCoords> coords;

	@Override
	public List<BlockCoords> getCoords() {
		return coords;
	}

	@Override
	public void addCoords(BlockCoords coords) {
		for (BlockCoords tile : this.coords) {
			if (BlockCoords.equalCoords(tile, coords)) {
				return;
			}
		}
		this.coords.add(coords);
	}

	@Override
	public void removeCoords(BlockCoords coords) {
		List<BlockCoords> removed = new ArrayList();
		for (BlockCoords tile : this.coords) {
			if (BlockCoords.equalCoords(tile, coords)) {
				removed.add(tile);
			}
		}
		for (BlockCoords remove : removed) {
			this.coords.remove(remove);
		}
	}

	/*
	 * @Override public void setCoords(BlockCoords coords) { if
	 * (!BlockCoords.equalCoords(this.coords, coords)) { this.coords = coords;
	 * CableHelper.updateAdjacentCoords(this, coords, true); } }
	 */
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
			coords = BlockCoords.readBlockCoords(nbt, "tiles");

		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			NBTTagCompound infoTag = new NBTTagCompound();
			BlockCoords.writeBlockCoords(nbt, coords, "tiles");

		}
	}

	public void validate() {
		this.coords = null;
		super.validate();
	}

	public void invalidate() {
		this.coords = null;
		super.invalidate();
		CableHelper.updateAdjacentCoords(worldObj, xCoord, yCoord, zCoord, null, true);
	}

}
