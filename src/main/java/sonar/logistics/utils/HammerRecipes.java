package sonar.logistics.utils;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import sonar.core.helpers.RecipeHelper;
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
		this.addRecipe("gemSapphire", new ItemStack(ItemRegistry.sapphire_dust));
		this.addRecipe("stone", new ItemStack(ItemRegistry.stone_plate, 4));
		this.addRecipe("oreSapphire", new ItemStack(ItemRegistry.sapphire_dust, 2));
	}

	@Override
	public String getRecipeID() {
		return "Hammer Recipes";
	}

}
