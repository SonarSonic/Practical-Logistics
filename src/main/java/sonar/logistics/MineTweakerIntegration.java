package sonar.logistics;

import com.google.common.collect.Lists;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import sonar.core.integration.minetweaker.SonarAddRecipeV2;
import sonar.core.integration.minetweaker.SonarRemoveRecipeV2;
import sonar.core.recipes.RecipeObjectType;
import sonar.logistics.utils.HammerRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

public class MineTweakerIntegration {

	public static void init() {
		MineTweakerAPI.registerClass(HammerHandler.class);
	}

	@ZenClass("mods.logistics.hammer")
	public static class HammerHandler {

		@ZenMethod
		public static void addRecipe(IIngredient input1, IItemStack output) {
			MineTweakerAPI.apply(new SonarAddRecipeV2(HammerRecipes.instance(), Lists.newArrayList(input1), Lists.newArrayList(MineTweakerMC.getItemStack(output))));
		}

		@ZenMethod
		public static void removeRecipe(IIngredient output) {
			MineTweakerAPI.apply(new SonarRemoveRecipeV2(HammerRecipes.instance(), RecipeObjectType.OUTPUT, Lists.newArrayList(output)));
		}
	}
}