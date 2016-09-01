package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import mcmultipart.raytrace.PartMOP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import sonar.core.helpers.NBTHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.display.IInfoDisplay;
import sonar.logistics.api.display.ScreenLayout;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.RenderInfoProperties;
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

	public static double[] getScaling(DisplayType type, ScreenLayout layout, int pos) {
		switch (layout) {
		case DUAL:
			return new double[] { type.width, type.height / 2, type.scale };
		case GRID:
			return new double[] { type.width / 2, type.height / 2, type.scale / 1.5 };
		case LIST:
			return new double[] { type.width, type.height / 4, type.scale / 1.5 };
		default:
			return new double[] { type.width, type.height, type.scale * 1.2 };
		}
	}

	public static double[] getTranslation(DisplayType type, ScreenLayout layout, int pos) {
		switch (layout) {
		case DUAL:
			return new double[] { 0, pos == 1 ? type.height / 2 : 0, 0 };
		case GRID:
			return new double[] { pos == 1 || pos == 3 ? type.height / 2 : 0, pos > 1 ? type.height / 2 : 0, 0 };
		case LIST:
			return new double[] { 0, pos * (type.height / 4), 0 };
		default:
			return new double[] { 0, 0, 0 };
		}
	}

	public static double[] getIntersect(DisplayType type, ScreenLayout layout, int pos) {
		switch (layout) {
		case DUAL:
			return new double[] { 0, pos == 1 ? type.height / 2 : 0, pos == 1 ? 1 : type.width / 2, pos == 1 ? 1 : type.height / 2 };
		case GRID:
			return new double[] { (pos == 1 || pos == 3 ? type.width / 2 : 0), (pos == 1 || pos == 3 ? type.height / 2 : 0), (pos == 1 || pos == 3 ? 1 : type.width / 2), (pos == 1 || pos == 3 ? 1 : type.height / 2) };
		case LIST:
			return new double[] { 0, pos * (type.height / 4), 0, (pos + 1) * (type.height / 4) };

		default:
			return new double[] { 0, 0, type.width, type.height };
		}
	}

	public static boolean canBeClickedStandard(RenderInfoProperties renderInfo, EntityPlayer player, EnumHand hand, ItemStack stack, PartMOP hit) {
		if (renderInfo.container.getMaxCapacity() == 1) {
			return true;
		}
		IInfoDisplay display = renderInfo.container.getDisplay();
		double[] intersect = getIntersect(display.getDisplayType(), display.getLayout(), renderInfo.infoPos);
		BlockPos pos = hit.getBlockPos();
		double x = hit.hitVec.xCoord - pos.getX();
		double y = hit.hitVec.yCoord - pos.getY();
		if (x >= intersect[0] && x <= intersect[2] && y >= intersect[1] && y <= intersect[3]) {
			return true;
		}
		return false;
	}

	public void getInfoPosition() {

	}

	public boolean hasInfoChanged(IMonitorInfo info, IMonitorInfo newInfo) {
		if (info == null && newInfo == null) {
			return false;
		} else if (info == null && newInfo != null || info != null && newInfo == null) {
			return true;
		}
		return info.isMatchingType(newInfo) && info.isIdenticalInfo(newInfo) && info.isIdenticalInfo(newInfo);
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
