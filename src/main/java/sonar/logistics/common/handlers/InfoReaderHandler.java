package sonar.logistics.common.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.IWailaInfo;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.ISyncPart;
import sonar.core.network.sync.SyncGeneric;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.connecting.IEntityNode;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.helpers.InfoHelper;

import com.google.common.collect.Lists;

public class InfoReaderHandler extends TileHandler implements IWailaInfo {

	public InfoReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	public ArrayList<ILogicInfo> cachedInfo = new ArrayList();
	public ArrayList<ILogicInfo> lastInfo = new ArrayList();
	// public List<ILogicInfo> clientInfo;
	// public List<ILogicInfo> lastInfo;

	private int primaryUpdate = 0;
	private int secondaryUpdate = 0;
	public SyncGeneric<ILogicInfo> primaryInfo = (SyncGeneric<ILogicInfo>) new SyncGeneric(Logistics.infoTypes, 0).addSyncType(SyncType.SPECIAL);
	public SyncGeneric<ILogicInfo> secondaryInfo = (SyncGeneric<ILogicInfo>) new SyncGeneric(Logistics.infoTypes, 1).addSyncType(SyncType.SPECIAL);
	public boolean emptyPrimary = false, emptySecondary = false;

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		lastInfo = (ArrayList<ILogicInfo>) cachedInfo.clone();
		cachedInfo = (ArrayList<ILogicInfo>) LogisticsAPI.getInfoHelper().getTileInfo(network).clone();
		boolean primary = false;
		boolean secondary = false;
		if (primaryInfo.getObject() == null || primaryInfo.getObject().updateTicks() == 1 || primaryUpdate >= primaryInfo.getObject().updateTicks()) {
			primary = true;
			primaryUpdate = 0;
		}
		if (secondaryInfo.getObject() == null || secondaryInfo.getObject().updateTicks() == 1 || secondaryUpdate >= secondaryInfo.getObject().updateTicks()) {
			secondary = true;
			secondaryUpdate = 0;
		}
		if (primary || secondary)
			updateData(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)), primary, secondary);

		primaryUpdate++;
		secondaryUpdate++;
	}

	public void updateData(TileEntity te, ForgeDirection dir, boolean primary, boolean secondary) {
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, dir.getOpposite());
		if (!network.getExternalBlocks(true).isEmpty()) {
			if (primary)
				this.setData(te, LogisticsAPI.getInfoHelper().getLatestTileInfo(primaryInfo.getObject(), network), true);
			if (secondary)
				this.setData(te, LogisticsAPI.getInfoHelper().getLatestTileInfo(secondaryInfo.getObject(), network), false);

		} else {
			TileEntity target = network.getFirstTileEntity(CacheTypes.ENTITY_NODES);
			if (target != null && target instanceof IEntityNode) {
				if (primary) {
					ILogicInfo info = LogisticsAPI.getInfoHelper().getLatestEntityInfo(primaryInfo.getObject(), (IEntityNode) target);
					this.setData(te, info, true);
				}
				if (secondary) {
					ILogicInfo info = LogisticsAPI.getInfoHelper().getLatestEntityInfo(secondaryInfo.getObject(), (IEntityNode) target);
					this.setData(te, info, false);
				}

			}
		}
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te))) || dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public ILogicInfo currentInfo(TileEntity te) {
		if (secondaryInfo.getObject() == null || !te.getWorldObj().isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) {
			if (primaryInfo.getObject() == null || emptyPrimary) {
				return new LogicInfo((byte) -1, "", "", "NO DATA");
			}
			return primaryInfo.getObject();
		} else {
			if (secondaryInfo.getObject() == null || emptySecondary) {
				return new LogicInfo((byte) -1, "", "", "NO DATA");
			}
			return secondaryInfo.getObject();
		}
	}

	public ILogicInfo getSecondaryInfo(TileEntity te) {
		if (primaryInfo.getObject() == null || !te.getWorldObj().isBlockIndirectlyGettingPowered(te.xCoord, te.yCoord, te.zCoord)) {
			if (secondaryInfo.getObject() == null || emptySecondary) {
				return new LogicInfo((byte) -1, "", "", "NO DATA");
			}
			return secondaryInfo.getObject();
		} else {
			if (primaryInfo.getObject() == null || emptyPrimary) {
				return new LogicInfo((byte) -1, "", "", "NO DATA");
			}
			return primaryInfo.getObject();
		}
	}

	public void setData(TileEntity te, ILogicInfo info, boolean primary) {
		if (info != null) {
			if (primary) {
				this.primaryInfo.setObject(info);
				emptyPrimary = false;
			} else {
				this.secondaryInfo.setObject(info);
				emptySecondary = false;
			}
		} else if (primary && this.primaryInfo.getObject() != null) {
			emptyPrimary = true;
		} else if (!primary && this.secondaryInfo.getObject() != null) {
			emptySecondary = true;
		}

	}

	public void sendAvailableData(TileEntity te, EntityPlayer player) {
		if (player != null && player instanceof EntityPlayerMP) {
			/*
			 * INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite()); List<ILogicInfo> info = new ArrayList(); if (!network.getExternalBlocks(true).isEmpty()) { info = LogisticsAPI.getInfoHelper().getTileInfo(network); } else { TileEntity target = network.getFirstTileEntity(CacheTypes.ENTITY_NODES); if (target != null && target instanceof IEntityNode) { info = LogisticsAPI.getInfoHelper().getEntityInfo((IEntityNode) target); } } this.lastInfo = clientInfo; List<ILogicInfo> newInfo = new ArrayList(); ILogicInfo lastInfo = null; for (ILogicInfo blockInfo : info) { if (lastInfo == null || !lastInfo.getCategory().equals(blockInfo.getCategory())) { newInfo.add(CategoryInfo.createInfo(blockInfo.getCategory())); }
			 * newInfo.add(blockInfo); lastInfo = blockInfo; } clientInfo = newInfo; NBTTagCompound tag = new NBTTagCompound(); this.writeData(tag, SyncType.SPECIAL); if (!tag.hasNoTags()) SonarCore.network.sendTo(new PacketTileSync(te.xCoord, te.yCoord, te.zCoord, tag, SyncType.SPECIAL), (EntityPlayerMP) player);
			 */
		}
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type == SyncType.SAVE) {
			emptyPrimary = nbt.getBoolean("emptyP");
			emptySecondary = nbt.getBoolean("emptyS");
		}
		if (type == SyncType.SPECIAL || type == SyncType.SYNC) {
			InfoHelper.readStorageToNBT(nbt, cachedInfo, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type == SyncType.SAVE) {
			nbt.setBoolean("emptyP", emptyPrimary);
			nbt.setBoolean("emptyS", emptySecondary);
		}
		if (type == SyncType.SPECIAL || type == SyncType.SYNC) {
			InfoHelper.writeInfoToNBT(nbt, cachedInfo, lastInfo, type);
		}
	}

	public void addSyncParts(List<ISyncPart> parts) {
		super.addSyncParts(parts);
		parts.addAll(Lists.newArrayList(primaryInfo, secondaryInfo));
	}

	@Override
	public List<String> getWailaInfo(List<String> currenttip) {
		return currenttip;
	}

}
