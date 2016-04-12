package sonar.logistics.common.handlers;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.network.sync.SyncTagType;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;

import com.google.common.collect.Lists;

public class InfoCreatorHandler extends TileHandler {

	public InfoCreatorHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public SyncTagType.STRING subCategory = new SyncTagType.STRING(0);
	public SyncTagType.STRING data = new SyncTagType.STRING(1);
	public SyncGeneric<ILogicInfo> info = new SyncGeneric(Logistics.infoTypes, "currentInfo");

	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(subCategory, data, info));
	}


	public CableType canRenderConnection(TileEntity te, ForgeDirection dir) {
		if (dir == ForgeDirection.getOrientation(FMPHelper.getMeta(te))) {
			return LogisticsAPI.getCableHelper().canRenderConnection(te, dir, CableType.BLOCK_CONNECTION);
		} else {
			return CableType.NONE;
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir == ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite();
	}

	public ILogicInfo currentInfo() {
		return this.info.getObject();
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
		this.info.setObject(new LogicInfo((byte) -1, "CREATOR", this.subCategory.getObject(), this.data.getObject()));
	}

}
