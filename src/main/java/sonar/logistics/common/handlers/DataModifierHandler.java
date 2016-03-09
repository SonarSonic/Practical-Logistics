package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.network.sync.SyncTagType;
import sonar.core.utils.BlockCoords;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;

import com.google.common.collect.Lists;

public class DataModifierHandler extends TileHandler {

	public SyncTagType.STRING subCategory = new SyncTagType.STRING(0);
	public SyncTagType.STRING prefix = new SyncTagType.STRING(1);
	public SyncTagType.STRING suffix = new SyncTagType.STRING(2);
	public SyncGeneric<ILogicInfo> info = new SyncGeneric(Logistics.infoTypes, "currentInfo");

	public DataModifierHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		ArrayList<BlockCoords> emitters = network.getConnections(CacheTypes.EMITTER);
		if (emitters.isEmpty() || emitters.get(0) == null) {
			return;
		}
		Object target = FMPHelper.getTile(emitters.get(0).getTileEntity());
		if (target == null) {
			return;
		} else {
			// Info lastInfo = info;
			if (target instanceof IInfoEmitter) {
				IInfoEmitter infoNode = (IInfoEmitter) target;
				if (infoNode.currentInfo() != null) {
					if (!infoNode.currentInfo().equals(info)) {
						this.info.setDefault(infoNode.currentInfo());
					}
				} else if (this.info != null) {
					this.info = null;
				}
			}
		}

	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(subCategory, prefix, suffix, info));
	}

	public CableType canRenderConnection(ForgeDirection dir, TileEntity te) {
		return LogisticsAPI.getCableHelper().canRenderConnection(te, dir, CableType.BLOCK_CONNECTION);
	}

	public boolean canConnect(ForgeDirection dir) {
		return true;
	}

	public ILogicInfo currentInfo() {
		if (this.info == null) {
			return null;
		}
		if (this.info.getObject().getProviderID() == -1 && this.info.getObject().getCategory().equals("PERCENT")) {
			return info.getObject();
		}
		String currentSub = this.subCategory.getObject();
		String currentPre = this.prefix.getObject();
		String currentSuf = this.suffix.getObject();
		String subCat = (currentSub == null || currentSub.isEmpty() || currentSub.equals("")) ? info.getObject().getSubCategory() : currentSub;
		String prefix = (currentPre == null || currentPre.isEmpty() || currentPre.equals("")) ? "" : currentPre;
		String suffix = (currentSuf == null || currentSuf.isEmpty() || currentSuf.equals("")) ? "" : currentSuf;
		ILogicInfo modifiedInfo = new LogicInfo((byte) -1, info.getObject().getCategory(), subCat, prefix + " " + info.getObject().getDisplayableData() + " " + suffix);
		return modifiedInfo;
	}

	public void textTyped(String string, int id) {
		String text = (string == null || string.isEmpty()) ? " " : string;
		switch (id) {
		case 1:
			this.prefix.setObject(string);
			break;
		case 2:
			this.suffix.setObject(string);
			break;
		default:
			this.subCategory.setObject(string);
			break;
		}
	}
}
