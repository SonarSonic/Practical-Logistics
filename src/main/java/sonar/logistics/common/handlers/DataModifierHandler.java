package sonar.logistics.common.handlers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.SyncString;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.NBTHelper.SyncType;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.helpers.InfoHelper;

public class DataModifierHandler extends TileHandler {

	public SyncString subCategory = new SyncString(0);
	public SyncString prefix = new SyncString(1);
	public SyncString suffix = new SyncString(2);
	public Info info;

	public DataModifierHandler(boolean isMultipart) {
		super(isMultipart);
	}

	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		CableHelper.updateAdjacentCoords(te, new BlockCoords(te.xCoord, te.yCoord, te.zCoord), false, new ForgeDirection[] { ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite() });
		updateData(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)));
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			subCategory.readFromNBT(nbt, type);
			prefix.readFromNBT(nbt, type);
			suffix.readFromNBT(nbt, type);
			if (nbt.hasKey("currentInfo")) {
				info = InfoHelper.readInfo(nbt.getCompoundTag("currentInfo"));
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
				InfoHelper.writeInfo(infoTag, info);
				nbt.setTag("currentInfo", infoTag);
			}

		}
	}

	public boolean canRenderConnection(TileEntity te, ForgeDirection dir) {
		return CableHelper.canRenderConnection(te, dir);
	}

	public boolean canConnect(ForgeDirection dir) {
		return true;
	}

	public void updateData(TileEntity te, ForgeDirection dir) {
		Object target = CableHelper.getConnectedTile(te, dir.getOpposite());
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

	@Override
	public void removed(World world, int x, int y, int z, int meta) {
		CableHelper.updateAdjacentCoords(world, x, y, z, null, true);
	}

}
