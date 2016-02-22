package sonar.logistics.common.handlers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.SyncTagType;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.CableType;

public class InfoCreatorHandler extends TileHandler {

	public InfoCreatorHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public SyncTagType.STRING subCategory = new SyncTagType.STRING(0);
	public SyncTagType.STRING data = new SyncTagType.STRING(1);
	public Info info;

	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.readFromNBT(nbt, type);
			data.readFromNBT(nbt, type);

			if (type == SyncType.SAVE) {
				if (nbt.hasKey("currentInfo")) {
					info = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag("currentInfo"));
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
					Logistics.infoTypes.writeToNBT(infoTag, info);
					nbt.setTag("currentInfo", infoTag);
				}
			}
		}
	}

	public CableType canRenderConnection(TileEntity te, ForgeDirection dir) {
		if (dir == ForgeDirection.getOrientation(FMPHelper.getMeta(te))) {
			return LogisticsAPI.getCableHelper().canRenderConnection(te, dir, CableType.BLOCK_CONNECTION);
		} else {
			return CableType.NONE;
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir == ForgeDirection.getOrientation(FMPHelper.getMeta(te));
	}

	public Info currentInfo() {
		return this.info;
	}

	public void textTyped(String string, int id) {
		String text = (string == null || string.isEmpty()) ? " " : string;
		switch (id) {
		case 0:
			this.subCategory.setObject(string);
			break;
		case 1:
			this.data.setObject(string);
			break;
		}
		this.info = new StandardInfo((byte) -1, "CREATOR", this.subCategory.getObject(), this.data.getObject());
	}

}
