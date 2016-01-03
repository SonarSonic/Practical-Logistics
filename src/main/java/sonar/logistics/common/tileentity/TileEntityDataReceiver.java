package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.DataEmitter;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataReceiver;
import sonar.logistics.helpers.CableHelper;

public class TileEntityDataReceiver extends TileEntityNode implements IDataReceiver {

	// client list
	public List<DataEmitter> emitters;

	public DataEmitter emitter;

	@Override
	public DataEmitter getEmitter() {
		if (emitter != null) {
			TileEntity tile = emitter.coords.getTileEntity();
			if (tile != null && tile instanceof TileEntityDataEmitter) {
				TileEntityDataEmitter dataEmitter = (TileEntityDataEmitter) tile;
				emitter = new DataEmitter(dataEmitter.clientName.getString(), emitter.coords);
			}
		}
		return emitter;
	}

	@Override
	public Info currentInfo() {
		if (emitter != null) {
			return new StandardInfo((byte) -1, "DEFAULT", "Connected: ", emitter.coords.getRender());
		} else {
			return new StandardInfo((byte) -1, "DEFAULT", "Connection: ", "NOT CONNECTED");
		}
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return ForgeDirection.getOrientation(this.getBlockMetadata()) == dir;
	}

	@Override
	public void updateData(ForgeDirection dir) {
	}

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		CableHelper.updateAdjacentCoord(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, ForgeDirection.getOrientation(this.getBlockMetadata()));

	}

	public boolean maxRender() {
		return true;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			if (nbt.hasKey("coords")) {
				if (nbt.getCompoundTag("coords").getBoolean("hasCoords")) {
					emitter = DataEmitter.readFromNBT(nbt.getCompoundTag("coords"));
				} else {
					emitter = null;
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			NBTTagCompound infoTag = new NBTTagCompound();
			if (emitter != null) {
				DataEmitter.writeToNBT(infoTag, emitter);
				infoTag.setBoolean("hasCoords", true);
			} else {
				infoTag.setBoolean("hasCoords", false);
			}
			nbt.setTag("coords", infoTag);
		}

	}
}