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

public class TileEntityDataModifier extends TileEntityNode implements IDataConnection, ICableRenderer, ITextField {

	public SyncString subCategory = new SyncString(0);
	public SyncString prefix = new SyncString(1);
	public SyncString suffix = new SyncString(2);
	public Info info;

	public void updateEntity() {
		super.updateEntity();
		if (isClient()) {
			return;
		}
		CableHelper.updateAdjacentCoords(this, new BlockCoords(this.xCoord, this.yCoord, this.zCoord), false, new ForgeDirection[] { ForgeDirection.getOrientation(this.getBlockMetadata()).getOpposite() });
		updateData(ForgeDirection.getOrientation(this.getBlockMetadata()));
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.readFromNBT(nbt, type);
			prefix.readFromNBT(nbt, type);
			suffix.readFromNBT(nbt, type);
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
			prefix.writeToNBT(nbt, type);
			suffix.writeToNBT(nbt, type);
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
		return CableHelper.canRenderConnection(this, dir);
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return true;
	}

	@Override
	public void updateData(ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(this, dir.getOpposite());
		target = FMPHelper.checkObject(target);
		if (target == null) {
			return;
		} else {
			if (target instanceof IDataConnection) {
				IDataConnection infoNode = (IDataConnection) target;
				if (infoNode.currentInfo() != null) {
					this.info = infoNode.currentInfo();
				} else if (this.info != null) {
					this.info.emptyData();
				}
			}
		}
	}

	@Override
	public Info currentInfo() {
		if (this.info == null) {
			return null;
		}
		if (this.info.getProviderID() == -1 && this.info.getCategory().equals("PERCENT")) {
			return info;
		}
		String currentSub = this.subCategory.getString();
		String currentPre = this.subCategory.getString();
		String currentSuf = this.subCategory.getString();
		String subCat = (currentSub == null || currentSub.isEmpty() || currentSub.equals("")) ? info.getSubCategory() : currentSub;		
		String prefix = (currentPre == null || currentPre.isEmpty() || currentPre.equals("")) ? "" : currentPre;
		String suffix = (currentSuf == null || currentSuf.isEmpty() || currentSuf.equals("")) ? "" : currentSuf;
		Info modifiedInfo = new StandardInfo((byte) -1, info.getCategory(), subCat, prefix + " " + info.getDisplayableData() + " " + suffix);
		return modifiedInfo;
	}

	@Override
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

	public boolean maxRender() {
		return true;
	}
}
