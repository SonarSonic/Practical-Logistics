package sonar.logistics.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.network.sync.SyncString;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;

public class TileEntityInfoCreator extends TileEntityNode implements IDataConnection, ICableRenderer, ITextField {

	public SyncString subCategory = new SyncString(0);
	public SyncString data = new SyncString(1);
	public Info info;

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		CableHelper.updateAdjacentCoord(worldObj, this.xCoord, this.yCoord, this.zCoord, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, ForgeDirection.getOrientation(this.getBlockMetadata()));
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.readFromNBT(nbt, type);
			data.readFromNBT(nbt, type);
			
			if (type == SyncType.SAVE) {
				if (nbt.hasKey("currentInfo")) {
					info = InfoHelper.readInfo(nbt.getCompoundTag("currentInfo"));
				}
			}
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.writeToNBT(nbt, type);
			data.writeToNBT(nbt, type);
			if (type == SyncType.SAVE) {
				if (info != null) {
					NBTTagCompound infoTag = new NBTTagCompound();
					InfoHelper.writeInfo(infoTag, info);
					nbt.setTag("currentInfo", infoTag);
				}
			}
		}
	}

	public boolean canRenderConnection(ForgeDirection dir) {
		if (dir == ForgeDirection.getOrientation(this.getBlockMetadata())) {
			return CableHelper.canRenderConnection(this, dir);
		} else {
			return false;
		}
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return dir == ForgeDirection.getOrientation(this.getBlockMetadata());
	}

	@Override
	public void updateData(ForgeDirection dir) {

	}

	@Override
	public Info currentInfo() {
		return this.info;
	}

	@Override
	public void textTyped(String string, int id) {
		String text = (string == null || string.isEmpty()) ? " " : string;
		switch (id) {
		case 1:
			this.data.setString(string);
			break;
		default:
			this.subCategory.setString(string);
			break;
		}
		this.info = new StandardInfo((byte) -1, "CREATOR", this.subCategory.getString(), this.data.getString());
	}

	public boolean maxRender() {
		return true;
	}
}
