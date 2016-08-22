package sonar.logistics.registries;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import sonar.core.helpers.ASMLoader;
import sonar.core.utils.Pair;
import sonar.logistics.api.asm.CustomEntityHandler;
import sonar.logistics.api.asm.CustomTileHandler;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.info.ICustomEntityHandler;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.api.info.monitor.IMonitorInfo;

public class InfoLoaderRegistry {

	private static int currentID = 0;
	public static LinkedHashMap<Integer, String> infoNames = new LinkedHashMap();
	public static LinkedHashMap<String, Integer> infoIds = new LinkedHashMap();
	public static LinkedHashMap<String, Class<? extends IMonitorInfo>> infoClasses = new LinkedHashMap();

	private InfoLoaderRegistry() {}

	public static List<IInfoRegistry> getInfoRegistries(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, InfoRegistry.class, IInfoRegistry.class, true);
	}

	public static List<ICustomTileHandler> getCustomTileHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, CustomTileHandler.class, ICustomTileHandler.class, true);
	}

	public static List<ICustomEntityHandler> getCustomEntityHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, CustomEntityHandler.class, ICustomEntityHandler.class, true);
	}

	public static void loadInfoTypes(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends IMonitorInfo>>> infoTypes = ASMLoader.getClasses(asmDataTable, LogicInfoType.class, IMonitorInfo.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends IMonitorInfo>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("id");
			int id = currentID++;
			infoNames.put(id, name);
			infoIds.put(name, id);
			infoClasses.put(name, info.b);
		}
	}
}
