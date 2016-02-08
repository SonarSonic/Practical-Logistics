package sonar.logistics.common.handlers;

import java.util.List;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.SyncString;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.Logistics;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.helpers.CableHelper;

public class DataModifierHandler extends TileHandler {

	public SyncString subCategory = new SyncString(0);
	public SyncString prefix = new SyncString(1);
	public SyncString suffix = new SyncString(2);
	public Info info;

	public DataModifierHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		List<BlockCoords> connections = CableHelper.getConnections(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		if (connections.isEmpty() || connections.get(0) == null) {
			return;
		}
		Object target = FMPHelper.getTile(connections.get(0).getTileEntity());
		if (target == null) {
			return;
		} else {
			// Info lastInfo = info;
			if (target instanceof IInfoEmitter) {
				IInfoEmitter infoNode = (IInfoEmitter) target;
				if (infoNode.currentInfo() != null) {
					if (!infoNode.currentInfo().equals(info)) {
						this.info = infoNode.currentInfo();
					}
				} else if (this.info != null) {
					this.info = null;
				}
			}
		}

	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.readFromNBT(nbt, type);
			prefix.readFromNBT(nbt, type);
			suffix.readFromNBT(nbt, type);
			if (nbt.hasKey("currentInfo")) {
				info = Logistics.infoTypes.readFromNBT(nbt.getCompoundTag("currentInfo"));
			}

		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.writeToNBT(nbt, type);
			prefix.writeToNBT(nbt, type);
			suffix.writeToNBT(nbt, type);
			if (info != null) {
				NBTTagCompound infoTag = new NBTTagCompound();
				Logistics.infoTypes.writeToNBT(infoTag, info);
				nbt.setTag("currentInfo", infoTag);
			}

		}
	}

	public int canRenderConnection(ForgeDirection dir, TileEntity te) {
		return CableHelper.canRenderConnection(te, dir);
	}

	public boolean canConnect(ForgeDirection dir) {
		return true;
	}

	public Info currentInfo() {
		if (this.info == null) {
			return null;
		}
		if (this.info.getProviderID() == -1 && this.info.getCategory().equals("PERCENT")) {
			return info;
		}
		String currentSub = this.subCategory.getString();
		String currentPre = this.prefix.getString();
		String currentSuf = this.suffix.getString();
		String subCat = (currentSub == null || currentSub.isEmpty() || currentSub.equals("")) ? info.getSubCategory() : currentSub;
		String prefix = (currentPre == null || currentPre.isEmpty() || currentPre.equals("")) ? "" : currentPre;
		String suffix = (currentSuf == null || currentSuf.isEmpty() || currentSuf.equals("")) ? "" : currentSuf;
		Info modifiedInfo = new StandardInfo((byte) -1, info.getCategory(), subCat, prefix + " " + info.getDisplayableData() + " " + suffix);
		return modifiedInfo;
	}

	public void textTyped(String string, int id) {
		String text = (string == null || string.isEmpty()) ? " " : string;
		switch (id) {
		case 1:
			this.prefix.setString(string);
			break;
		case 2:
			this.suffix.setString(string);
			break;
		default:
			this.subCategory.setString(string);
			break;
		}
	}
}
