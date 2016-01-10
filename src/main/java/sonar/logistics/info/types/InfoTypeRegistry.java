package sonar.logistics.info.types;

import java.util.ArrayList;
import java.util.List;

import sonar.logistics.Logistics;
import sonar.logistics.api.EntityInfo;
import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;

public class InfoTypeRegistry {

	private static List<Info> infoTypes = new ArrayList();

	public static void registerProviders() {
		addInfoType(new BlockCoordsInfo());
		addInfoType(new CategoryInfo());
		addInfoType(new StandardInfo());	
		addInfoType(new EntityInfo());	
		addInfoType(new StoredStackInfo());	
		addInfoType(new ProgressInfo());	
		addInfoType(new FluidInfo());	
		addInfoType(new ThaumcraftAspectInfo());	
		addInfoType(new ManaInfo());	
	}

	public static void removeAll() {
		infoTypes.clear();
	}

	public static List<Info> getInfoTypes() {
		return infoTypes;
	}

	public static Info getInfoType(String infoType) {
		if (infoType == null || infoType.isEmpty()) {
			return null;
		}
		for (Info info : infoTypes) {
			if (info.getType().equals(infoType)) {
				return info;
			}
		}
		return null;
	}

	public static void addInfoType(Info info) {
		if (info != null) {
			if (getInfoType(info.getType()) == null) {
				infoTypes.add(info);
				Logistics.logger.info("Loaded Info Type: " + info.getType());
			} else {
				Logistics.logger.warn("DUPLICATE INFO ID - skipping " + info.getType());
			}
		}
	}

	public static void removeInfoType(Info info) {
		if (info != null) {
			infoTypes.remove(info);
		}
	}	
}