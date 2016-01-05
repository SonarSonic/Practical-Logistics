package sonar.logistics;

import java.io.File;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class LogisticsConfig extends Logistics {

	public static Property blocksblackList;
	public static Property itemsblackList;

	public static boolean sapphireOre;

	public static void initConfiguration(FMLPreInitializationEvent event) {
		loadMainConfig();
		loadBlocks();
		loadItems();
	}

	public static void loadMainConfig() {
		Configuration config = new Configuration(new File("config/Practical-Logistics/Main-Config.cfg"));
		config.load();
		sapphireOre = config.getBoolean("Generate Ore", "settings", true, "Sapphire Ore");
		config.save();

	}

	public static void loadBlocks() {
		Configuration blocks = new Configuration(new File("config/Practical-Logistics/Blocks-BlackList.cfg"));
		blocks.load();
		String[] blockExamples = new String[2];
		blockExamples[0] = "ExampleBlock";
		blockExamples[1] = "ExampleBlock2";
		blocksblackList = blocks.get("Block Config", "Disabled", blockExamples);
		blocks.save();
	}

	public static void loadItems() {
		Configuration items = new Configuration(new File("config/Practical-Logistics/Items-BlackList.cfg"));
		items.load();
		String[] itemExamples = new String[2];
		itemExamples[0] = "ExampleItem";
		itemExamples[1] = "ExampleItem2";
		itemsblackList = items.get("Item Config", "Disabled", itemExamples);
		items.save();
	}

	private static boolean isBlockEnabled(String block) {
		if (block != null) {
			String[] blacklisted = blocksblackList.getStringList();
			for (int i = 0; i < blacklisted.length; i++) {
				if (blacklisted[i] != null && blacklisted[i].equals(block)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private static boolean isItemEnabled(String item) {
		if (item != null) {
			String[] blacklisted = itemsblackList.getStringList();
			for (int i = 0; i < blacklisted.length; i++) {
				if (blacklisted[i] != null && blacklisted[i].equals(item)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param stack the ItemStack you wish to check
	 * @return if the Item/Block is enabled
	 */
	public static boolean isEnabled(ItemStack stack) {
		if (stack == null) {
			return true;
		}
		Block block = Block.getBlockFromItem(stack.getItem());
		Item item = stack.getItem();
		if (block != null && item instanceof ItemBlock && GameRegistry.findUniqueIdentifierFor(block) != null) {
			if (LogisticsConfig.isBlockEnabled(GameRegistry.findUniqueIdentifierFor(block).name)) {
				return true;
			} else {
				return false;
			}
		} else if (item != null && GameRegistry.findUniqueIdentifierFor(item) != null) {
			if (LogisticsConfig.isItemEnabled(GameRegistry.findUniqueIdentifierFor(item).name)) {
				return true;

			} else {
				return false;
			}
		} else {
			return true;
		}
	}
}
