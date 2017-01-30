package sonar.logistics;

import java.util.LinkedHashMap;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.discovery.ASMDataTable;
import sonar.core.helpers.ASMLoader;
import sonar.core.utils.Pair;
import sonar.logistics.api.asm.CustomEntityHandler;
import sonar.logistics.api.asm.CustomTileHandler;
import sonar.logistics.api.asm.EntityMonitorHandler;
import sonar.logistics.api.asm.InfoRegistry;
import sonar.logistics.api.asm.LogicInfoType;
import sonar.logistics.api.asm.TileMonitorHandler;
import sonar.logistics.api.info.ICustomEntityHandler;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.IEntityMonitorHandler;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.api.info.ITileMonitorHandler;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.monitor.LogicMonitorHandler;

public class LogisticsASMLoader {

	public static LinkedHashMap<Integer, String> infoNames = new LinkedHashMap();
	public static LinkedHashMap<String, Integer> infoIds = new LinkedHashMap();
	public static LinkedHashMap<String, Class<? extends IMonitorInfo>> infoClasses = new LinkedHashMap();
	public static LinkedHashMap<String, ITileMonitorHandler> tileMonitorHandlers = new LinkedHashMap();
	public static LinkedHashMap<String, IEntityMonitorHandler> entityMonitorHandlers = new LinkedHashMap();

	private LogisticsASMLoader() {}

	public static List<IInfoRegistry> getInfoRegistries(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, InfoRegistry.class, IInfoRegistry.class, true, false);
	}

	public static List<ICustomTileHandler> getCustomTileHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, CustomTileHandler.class, ICustomTileHandler.class, true, false);
	}

	public static List<ICustomEntityHandler> getCustomEntityHandlers(@Nonnull ASMDataTable asmDataTable) {
		return ASMLoader.getInstances(asmDataTable, CustomEntityHandler.class, ICustomEntityHandler.class, true, false);
	}

	public static void loadTileMonitorHandlers(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends ITileMonitorHandler>>> infoTypes = ASMLoader.getClasses(asmDataTable, TileMonitorHandler.class, ITileMonitorHandler.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends ITileMonitorHandler>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("handlerID");
			try {
				tileMonitorHandlers.put(name, info.b.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				Logistics.logger.error("FAILED: To Load Tile Monitor Handler - " + name);
			}
		}
		Logistics.logger.info("Loaded: " + tileMonitorHandlers.size() + " Tile Monitor Handlers");
	}

	public static void loadEntityMonitorHandlers(@Nonnull ASMDataTable asmDataTable) {
		List<Pair<ASMDataTable.ASMData, Class<? extends IEntityMonitorHandler>>> infoTypes = ASMLoader.getClasses(asmDataTable, EntityMonitorHandler.class, IEntityMonitorHandler.class, true);
		for (Pair<ASMDataTable.ASMData, Class<? extends IEntityMonitorHandler>> info : infoTypes) {
			String name = (String) info.a.getAnnotationInfo().get("handlerID");
			try {
				entityMonitorHandlers.put(name, info.b.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				Logistics.logger.error("FAILED: To Load Entity Monitor Handler - " + name);
			}
		}
		Logistics.logger.info("Loaded: " + entityMonitorHandlers.size() + " Entity Monitor Handlers");
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
