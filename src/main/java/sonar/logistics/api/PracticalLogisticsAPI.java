package sonar.logistics.api;

import java.lang.reflect.Method;

import sonar.logistics.api.data.EntityProvider;
import sonar.logistics.api.data.TileProvider;

/**all of the API is heavily W.I.P until the mod is out of Alpha, it'll change quite a lot so don't use it yet*/
public class PracticalLogisticsAPI {

	public static final String VERSION = "1.0";

	public static void registerInfoProvider(TileProvider provider) {
		if (provider != null) {
			try {
				Class recipeClass = Class.forName("sonar.logistics.info.providers.tile.TileProviderRegistry");
				Method method = recipeClass.getMethod("addProvider", TileProvider.class);
				method.invoke(null, provider);
			} catch (Exception exception) {
				System.err.println("Practical Logistics API : FAILED TO REGISTER TILE PROVIDER" + exception.getMessage());
			}

		}
	}
	public static void registerInfoProvider(EntityProvider provider) {
		if (provider != null) {
			try {
				Class recipeClass = Class.forName("sonar.logistics.info.providers.entity.EntityProviderRegistry");
				Method method = recipeClass.getMethod("addProvider", EntityProvider.class);
				method.invoke(null, provider);
			} catch (Exception exception) {
				System.err.println("Practical Logistics API : FAILED TO REGISTER ENTITY PROVIDER" + exception.getMessage());
			}

		}
	}


	public static void registerInfoType(Info format) {
		if (format != null) {
			try {
				Class recipeClass = Class.forName("sonar.logistics.info.types.InfoTypeRegistry");
				Method method = recipeClass.getMethod("addInfoType", Info.class);
				method.invoke(null, format);
			} catch (Exception exception) {
				System.err.println("Practical Logistics API : FAILED TO REGISTER INFO TYPE" + exception.getMessage());
			}

		}
	}
}
