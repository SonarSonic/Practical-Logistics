package sonar.logistics;

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
import sonar.logistics.api.asm.MonitorHandler;
import sonar.logistics.api.info.ICustomEntityHandler;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;

public class LogisticsASMLoader {

	public static LinkedHashMap<Integer, String> infoNames = new LinkedHashMap();
	public static LinkedHashMap<String, Integer> infoIds = new LinkedHashMap();
	public static LinkedHashMap<String, Class<? extends IMonitorInfo>> infoClasses = new LinkedHashMap();
	public static LinkedHashMap<String, LogicMonitorHandler> monitorHandlers = new LinkedHashMap();

	private LogisticsASMLoader() {
	}

	public static List<IInfoRegistry> getInfoRegistries(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, InfoRegistry.class, IInfoRegistry.class, true);
	}

	public static List<ICustomTileHandler> getCustomTileHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, CustomTileHandler.class, ICustomTileHandler.class, true);
	}

	public static List<ICustomEntityHandler> getCustomEntityHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, CustomEntityHandler.class, ICustomEntityHandler.class, true);
	}

	public static void loadMonitorHandlers(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends LogicMonitorHandler>>> infoTypes = ASMLoader.getClasses(asmDataTable, MonitorHandler.class, LogicMonitorHandler.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends LogicMonitorHandler>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("handlerID");
			try {
				monitorHandlers.put(name, info.b.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				Logistics.logger.error("FAILED: To Load Monitor Handler - " + name);
			}
		}
		Logistics.logger.info("Loaded: " + monitorHandlers.size() + " Monitor Handlers");
	}

	public static void loadInfoTypes(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends IMonitorInfo>>> infoTypes = ASMLoader.getClasses(asmDataTable, LogicInfoType.class, IMonitorInfo.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends IMonitorInfo>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("id");
			int hashCode = name.hashCode();
			infoNames.put(hashCode, name);
			infoIds.put(name, hashCode);
			infoClasses.put(name, info.b);
		}
		Logistics.logger.info("Loaded: " + infoIds.size() + " Info Types");
	}
}
