package sonar.logistics.registries;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import sonar.logistics.Logistics;
import sonar.logistics.LogisticsConfig;
import cpw.mods.fml.common.registry.GameRegistry;

public class CraftingRegistry extends Logistics {

	public static void addRecipes() {
		addShapedOre(new ItemStack(BlockRegistry.dataCable, 16), new Object[] { "PPP", "DDD", "PPP", 'P', ItemRegistry.stone_plate, 'D', "dustSapphire" });
		addShapelessOre(new ItemStack(ItemRegistry.displayScreen, 1), new Object[] { ItemRegistry.stone_plate, "dustSapphire", BlockRegistry.dataCable });
		addShapedOre(new ItemStack(BlockRegistry.holographicDisplay, 1), new Object[] { "   ", "PDP", " C ", 'P', ItemRegistry.stone_plate, 'D', ItemRegistry.displayScreen, 'C', BlockRegistry.dataCable });
		addShapedOre(new ItemStack(BlockRegistry.dataModifier, 1), new Object[] { "DCD", "PDC", "DCD", 'P', ItemRegistry.stone_plate, 'D', "dustSapphire", 'C', BlockRegistry.dataCable });
		addShapedOre(new ItemStack(BlockRegistry.infoCreator, 1), new Object[] { "DPD", "PCD", "DPD", 'P', ItemRegistry.stone_plate, 'D', "dustSapphire", 'C', BlockRegistry.dataCable });
		addShapedOre(new ItemStack(BlockRegistry.redstoneSignaller_off, 1), new Object[] { "P  ", "CT ", "PPP", 'P', ItemRegistry.stone_plate, 'T', Blocks.redstone_torch, 'C', BlockRegistry.dataCable });
		addShapedOre(new ItemStack(BlockRegistry.dataEmitter, 1), new Object[] { "DPD", "PCP", "DDD", 'P', Items.redstone, 'D', ItemRegistry.stone_plate, 'C', Items.ender_pearl });
		addShapedOre(new ItemStack(BlockRegistry.dataReceiver, 1), new Object[] { "DPD", "PCP", "DDD", 'P', Items.redstone, 'D', ItemRegistry.stone_plate, 'C', BlockRegistry.infoReader });
		addShapedOre(new ItemStack(BlockRegistry.infoReader, 1), new Object[] { "PIP", "RDS", "PIP", 'R', Items.redstone, 'I', Items.iron_ingot, 'P', ItemRegistry.stone_plate, 'D', BlockRegistry.dataCable, 'S', "dustSapphire" });
		addShapelessOre(new ItemStack(BlockRegistry.inventoryReader, 1), new Object[] { BlockRegistry.infoReader, Blocks.chest });
		addShapedOre(new ItemStack(BlockRegistry.node, 1), new Object[] { "   ", " C ", "PDP", 'P', ItemRegistry.stone_plate, 'D', "dustSapphire", 'C', BlockRegistry.dataCable });
		addShapedOre(new ItemStack(BlockRegistry.hammer, 1), new Object[] { "ADA", "B B", "ACA", 'A', "logWood", 'B', "stickWood", 'C', "stone",'D', "slabWood"  });
		addShapelessOre(new ItemStack(BlockRegistry.entityNode, 1), new Object[] { ItemRegistry.stone_plate, "gemSapphire", BlockRegistry.dataCable });

	}

	public static void addShaped(ItemStack result, Object... input) {
		if (LogisticsConfig.isEnabled(result)) {
			GameRegistry.addRecipe(result, input);
		}
	}

	public static void addShapedOre(ItemStack result, Object... input) {
		ShapedOreRecipe recipe = new ShapedOreRecipe(result, input);
		if (LogisticsConfig.isEnabled(recipe.getRecipeOutput())) {
			GameRegistry.addRecipe(recipe);
		}
	}

	public static void addShapeless(ItemStack result, Object... input) {
		if (LogisticsConfig.isEnabled(result)) {
			GameRegistry.addShapelessRecipe(result, input);
		}
	}

	public static void addShapelessOre(ItemStack result, Object... input) {
		if (LogisticsConfig.isEnabled(result)) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(result, input));
		}
	}
}