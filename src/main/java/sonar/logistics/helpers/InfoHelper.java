package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.registries.InfoLoaderRegistry;

public class InfoHelper {

	public static ArrayList<LogicInfo> sortInfoList(ArrayList<LogicInfo> oldInfo) {
		ArrayList<LogicInfo> providerInfo = (ArrayList<LogicInfo>) oldInfo.clone();
		Collections.sort(providerInfo, new Comparator<LogicInfo>() {
			public int compare(LogicInfo str1, LogicInfo str2) {
				return Integer.compare(str1.getRegistryType().sortOrder, str2.getRegistryType().sortOrder);
			}
		});
		ArrayList<LogicInfo> sortedInfo = new ArrayList();
		LogicInfo lastInfo = null;
		for (LogicInfo blockInfo : (ArrayList<LogicInfo>) providerInfo.clone()) {
			if (lastInfo == null || !lastInfo.getRegistryType().equals(blockInfo.getRegistryType())) {
				sortedInfo.add(LogicInfo.buildCategoryInfo(blockInfo.getRegistryType()));
			}
			sortedInfo.add(blockInfo);
			lastInfo = blockInfo;
		}
		return sortedInfo;
	}

	public boolean hasInfoChanged(IMonitorInfo info, IMonitorInfo newInfo) {
		if(info==null && newInfo==null){
			return false;
		}else if (info == null && newInfo != null || info!=null && newInfo==null) {
			return true;
		} 
		return info.isMatchingType(newInfo)&&info.isIdenticalInfo(newInfo) && info.isIdenticalInfo(newInfo);
	}

	public static int getName(String name) {
		return InfoLoaderRegistry.infoIds.get(name);
	}

	public static Class<? extends IMonitorInfo> getInfoType(int id) {
		return InfoLoaderRegistry.infoClasses.get(InfoLoaderRegistry.infoNames.get(id));
	}

	public static NBTTagCompound writeInfoToNBT(NBTTagCompound tag, IMonitorInfo info, SyncType type) {
		tag.setInteger("iiD", InfoLoaderRegistry.infoIds.get(info.getID()));
		info.writeData(tag, type);
		return tag;
	}

	public static IMonitorInfo readInfoFromNBT(NBTTagCompound tag) {
		return loadInfo(tag.getInteger("iiD"), tag);
	}

	public static IMonitorInfo loadInfo(int id, NBTTagCompound tag) {
		return NBTHelper.instanceNBTSyncable(getInfoType(id), tag);
	}
}
