package sonar.logistics.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import sonar.logistics.Logistics;
import sonar.logistics.api.info.CustomEntityHandler;
import sonar.logistics.api.info.CustomTileHandler;
import sonar.logistics.api.info.ICustomEntityHandler;
import sonar.logistics.api.info.ICustomTileHandler;
import sonar.logistics.api.info.IInfoRegistry;
import sonar.logistics.api.info.InfoRegistry;

public class InfoLoaderRegistry {
	
	private InfoLoaderRegistry() {}

	public static List<IInfoRegistry> getInfoRegistries(@Nonnull ASMDataTable asmDataTable) {
		return getInstances(asmDataTable, InfoRegistry.class, IInfoRegistry.class);
	}

	public static List<ICustomTileHandler> getCustomTileHandlers(@Nonnull ASMDataTable asmDataTable) {
		return getInstances(asmDataTable, CustomTileHandler.class, ICustomTileHandler.class);
	}

	public static List<ICustomEntityHandler> getCustomEntityHandlers(@Nonnull ASMDataTable asmDataTable) {
		return getInstances(asmDataTable, CustomEntityHandler.class, ICustomEntityHandler.class);
	}

	private static <T> List<T> getInstances(@Nonnull ASMDataTable asmDataTable, Class annotation, Class<T> instanceClass) {
		String annotationClassName = annotation.getCanonicalName();
		Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
		List<T> instances = new ArrayList<>();
		for (ASMDataTable.ASMData asmData : asmDatas) {
			String modid = (String) asmData.getAnnotationInfo().get("modid");
			if (Loader.isModLoaded(modid)) {
				try {
					Class<?> asmClass = Class.forName(asmData.getClassName());
					Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
					T instance = asmInstanceClass.newInstance();
					instances.add(instance);
					Logistics.logger.info(instanceClass.getSimpleName() + " loaded successfully: {}", asmData.getClassName());
					continue;
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					Logistics.logger.error(instanceClass.getSimpleName() + " couldn't be loaded: {}", asmData.getClassName());
					continue;
				}				
			} else {
				Logistics.logger.error(String.format("Couldn't load" + instanceClass.getSimpleName() +  "%s for modid %s", asmData.getClassName(), modid));
			}
		}
		return instances;
	}
}
