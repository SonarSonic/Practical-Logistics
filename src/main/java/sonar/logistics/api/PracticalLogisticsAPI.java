package sonar.logistics.api;

import java.lang.reflect.Method;

import sonar.logistics.api.data.EntityProvider;
import sonar.logistics.api.data.TileProvider;

public class PracticalLogisticsAPI {

	public static final String VERSION = "1.0";

	/** register your Tile Providers in the FMLPostInitializationEvent */
	public static void registerTileProvider(TileProvider provider) {
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

	/** register your Entity Providers in the FMLPostInitializationEvent */
	public static void registerEntityProvider(EntityProvider provider) {
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

	/** register your Info Types in the {@link}FMLPostInitializationEvent */
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

	/** passes in input and output, this can be an ItemStack, Item, Block, OreStack or Ore Dict String */
	public static void registerInfoType(Object... recipe) {
		if (recipe != null && recipe.length == 2) {
			try {
				Class recipeClass = Class.forName("sonar.logistics.utils.HammerRecipes");
				Method method = recipeClass.getMethod("addRecipe", Object[].class);
				method.invoke(null, recipe);
			} catch (Exception exception) {
				System.err.println("Practical Logistics API : FAILED TO ADD FORGING HAMMER RECIPE" + exception.getMessage());
			}

		}
	}
}
