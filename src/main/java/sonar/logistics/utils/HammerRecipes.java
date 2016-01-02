package sonar.logistics.utils;

import net.minecraft.item.ItemStack;
import sonar.core.utils.helpers.RecipeHelper;
import sonar.logistics.registries.ItemRegistry;

public class HammerRecipes extends RecipeHelper {

	private static final HammerRecipes instance = new HammerRecipes();

	public HammerRecipes() {
		super(1, 1, false);
	}

	public static final RecipeHelper instance() {
		return instance;
	}

	@Override
	public void addRecipes() {
		this.addRecipe(new OreStack("gemSapphire", 1), new ItemStack(ItemRegistry.sapphire_dust));
		this.addRecipe(new OreStack("stone", 1), new ItemStack(ItemRegistry.stone_plate));
		this.addRecipe(new OreStack("oreSapphire", 1), new ItemStack(ItemRegistry.sapphire_dust, 2));
	}

	@Override
	public String getRecipeID() {
		return "Hammer Recipes";
	}

}
