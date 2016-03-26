package sonar.logistics.integration;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import minetweaker.api.minecraft.MineTweakerMC;
import minetweaker.api.oredict.IOreDictEntry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import sonar.core.helpers.RecipeHelper;
import sonar.logistics.utils.HammerRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.logistics.hammer")
public class HammerHandler {

	@ZenMethod
	public static void addRecipe(IIngredient input1, IItemStack output) {
		MineTweakerAPI.apply(new AddRecipeAction(input1, MineTweakerMC.getItemStack(output)));
	}

	@ZenMethod
	public static void removeRecipe(IIngredient input1) {
		MineTweakerAPI.apply(new RemoveRecipeAction(input1));
	}

	private static class AddRecipeAction implements IUndoableAction {
		private Object input;
		private ItemStack output;

		public AddRecipeAction(Object input1, ItemStack output) {
			if (input1 instanceof IItemStack)
				input1 = MineTweakerMC.getItemStack((IItemStack) input1);
			if (input1 instanceof IOreDictEntry)
				input1 = new RecipeHelper.OreStack(((IOreDictEntry) input1).getName(), 1);

			if (input1 instanceof ILiquidStack) {
				MineTweakerAPI.logError("A liquid was passed into a Forging Hammer Recipe, aborting!");
				input1 = output = null;
			}

			this.input = input1;
			this.output = output;
		}

		@Override
		public void apply() {
			if (input == null || output == null)
				return;
			HammerRecipes.instance().addRecipe(input, output);
		}

		@Override
		public void undo() {
			if (input == null || output == null)
				return;
			HammerRecipes.instance().removeRecipe(input);
		}

		@Override
		public String describe() {
			return String.format("Adding Forging Hammer recipe (%s = %s)", input, output);
		}

		@Override
		public String describeUndo() {
			return String.format("Reverting /%s/", describe());
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}

	}

	private static class RemoveRecipeAction implements IUndoableAction {
		private Object input;
		private ItemStack output;

		public RemoveRecipeAction(Object input) {
			if (input instanceof IItemStack)
				input = MineTweakerMC.getItemStack((IItemStack) input);
			if (input instanceof IOreDictEntry)
				input = new RecipeHelper.OreStack(((IOreDictEntry) input).getName(), 1);

			if (input instanceof ILiquidStack) {
				MineTweakerAPI.logError("A liquid was passed into a Forging Hammer Recipe, aborting!");
				input = output = null;
			}

			this.input = input;

			ItemStack dummyInput1 = null;

			if (input instanceof ItemStack)
				dummyInput1 = (ItemStack) input;
			if (input instanceof RecipeHelper.OreStack)
				dummyInput1 = OreDictionary.getOres(((RecipeHelper.OreStack) input).oreString).get(0);

			output = HammerRecipes.instance().getCraftingResult(dummyInput1);
		}

		@Override
		public void apply() {
			if (input == null || output == null)
				return;
			HammerRecipes.instance().removeRecipe(input);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			if (input == null || output == null)
				return;
			HammerRecipes.instance().addRecipe(input, output);
		}

		@Override
		public String describe() {
			return String.format("Removing Forging Hammer Recipe (%s = %s)", input, output);
		}

		@Override
		public String describeUndo() {
			return String.format("Reverting /%s/", describe());
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

}
