package sonar.logistics.utils;

import net.minecraft.item.ItemStack;
import sonar.core.helpers.RecipeHelper;
import sonar.core.recipes.DefinedRecipeHelper;
import sonar.logistics.LogisticsItems;

public class HammerRecipes extends DefinedRecipeHelper {

	private static final HammerRecipes instance = new HammerRecipes();

	public HammerRecipes() {
		super(1, 1, false);
	}

	public static final HammerRecipes instance() {
		return instance;
	}

	@Override
	public void addRecipes() {
		this.addRecipe("gemSapphire", new ItemStack(LogisticsItems.sapphire_dust));
		this.addRecipe("stone", new ItemStack(LogisticsItems.stone_plate, 4));
		this.addRecipe("oreSapphire", new ItemStack(LogisticsItems.sapphire_dust, 2));
	}

	@Override
	public String getRecipeID() {
		return "Hammer Recipes";
	}

}
