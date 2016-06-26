package sonar.logistics.common.handlers;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.network.ByteBufUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.api.EnergyType;
import sonar.core.api.StoredEnergyStack;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.sync.SyncGeneric;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.utils.IByteBufTile;
import sonar.logistics.Logistics;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.INetworkCache;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.helpers.InfoHelper;
import sonar.logistics.info.types.StoredEnergyInfo;

public class EnergyReaderHandler extends TileHandler implements IByteBufTile {

	// generally assumed to be StoredEnergyInfo
	public ArrayList<ILogicInfo> stacks = new ArrayList();
	public ArrayList<ILogicInfo> lastStacks = new ArrayList();

	public SyncGeneric<ILogicInfo> primaryInfo = new SyncGeneric(Logistics.infoTypes, 0);
	// public SyncTagType.STRING energyFormat = (STRING) new SyncTagType.STRING(0).setDefault(EnergyType.RF.getStorageSuffix());
	public boolean changed = true;
	public EnergyType energyFormat = EnergyType.RF;

	public EnergyReaderHandler(boolean isMultipart, TileEntity tile) {
		super(isMultipart, tile);
	}

	@Override
	public void update(TileEntity te) {
		if (te.getWorldObj().isRemote) {
			return;
		}
		INetworkCache network = LogisticsAPI.getCableHelper().getNetwork(te, ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
		lastStacks = (ArrayList<ILogicInfo>) stacks.clone();
		stacks = (ArrayList<ILogicInfo>) LogisticsAPI.getEnergyHelper().getEnergyList(network).clone();
	}

	public boolean canConnect(TileEntity te, ForgeDirection dir) {
		return dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te))) || dir.equals(ForgeDirection.getOrientation(FMPHelper.getMeta(te)).getOpposite());
	}

	public ILogicInfo currentInfo(TileEntity te) {
		if (primaryInfo.getObject() != null && primaryInfo.getObject() instanceof StoredEnergyInfo) {
			StoredEnergyInfo info = (StoredEnergyInfo) primaryInfo.getObject();
			if (info.coords != null && stacks != null) {
				for (ILogicInfo stack : stacks) {
					if (stack != null && stack instanceof StoredEnergyInfo) {
						StoredEnergyInfo energyInfo = (StoredEnergyInfo) stack;
						if (energyInfo.coords != null && energyInfo.coords.equals(info.coords)) {
							if(energyInfo.stack.energyType!=EnergyType.RF){
								StoredEnergyStack energy = energyInfo.stack.copy();
								energy.convertEnergyType(EnergyType.RF);
								return StoredEnergyInfo.createInfo(energyInfo.coords, energy);
							}							
							return energyInfo;
						}
					}
				}
			}
		}
		return InfoHelper.empty;
	}

	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (nbt.hasKey("EnergyType")) {
			EnergyType energyType = SonarCore.energyTypes.getEnergyType(nbt.getString("EnergyType"));
			if (energyType == null) {
				energyFormat = EnergyType.RF;
				changed = true;
			} else {
				energyFormat = energyType;
			}
		}
		if (type.isType(SyncType.SAVE, SyncType.SYNC)) {
			primaryInfo.readFromNBT(nbt, type);
		}
		if (type == SyncType.SPECIAL || type == SyncType.SYNC) {
			InfoHelper.readStorageToNBT(nbt, (ArrayList<ILogicInfo>) stacks, type);
		}
	}

	public void writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (changed || type == SyncType.SAVE) {
			nbt.setString("EnergyType", energyFormat.getStorageSuffix());
			changed = false;
		}
		if (type == SyncType.SAVE || type == SyncType.SYNC) {
			primaryInfo.writeToNBT(nbt, type);
		}
		if (type == SyncType.SPECIAL || type == SyncType.SYNC) {
			InfoHelper.writeInfoToNBT(nbt, stacks, lastStacks, type);
		}
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		if (id == 0) {
			EnergyType format = SonarCore.energyTypes.getEnergyType(ByteBufUtils.readUTF8String(buf));
			List<EnergyType> types = SonarCore.energyTypes.getObjects();
			int i;
			for (i = 0; i < types.size(); i++) {
				EnergyType type = types.get(i);
				if (type.getStorageSuffix().equals(energyFormat)) {
					if (i + 1 < types.size()) {
						i = i + 1;
					} else {
						i = 0;
					}
					break;
				}
			}
			if (format != null)
				energyFormat = types.get(i);
			changed = true;
		}
	}
}