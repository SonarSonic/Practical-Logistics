package sonar.logistics.helpers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sonar.logistics.api.info.LogicInfo;

public class InfoHelper {

	public static ArrayList<LogicInfo> sortInfoList(ArrayList<LogicInfo> oldInfo) {
		ArrayList<LogicInfo> providerInfo = (ArrayList<LogicInfo>) oldInfo.clone();
		Collections.sort(providerInfo, new Comparator<LogicInfo>() {
			public int compare(LogicInfo str1, LogicInfo str2) {
				return Integer.compare(str1.registryType.sortOrder, str2.registryType.sortOrder);
			}
		});
		ArrayList<LogicInfo> sortedInfo = new ArrayList();
		LogicInfo lastInfo = null;
		for (LogicInfo blockInfo : (ArrayList<LogicInfo>) providerInfo.clone()) {
			if (lastInfo == null || !lastInfo.registryType.equals(blockInfo.registryType)) {
				sortedInfo.add(LogicInfo.buildCategoryInfo(blockInfo.registryType));
			}
			sortedInfo.add(blockInfo);
			lastInfo = blockInfo;
		}
		return sortedInfo;
	}
}
