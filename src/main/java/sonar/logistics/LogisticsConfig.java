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

	public static boolean sapphireOre;
	public static boolean displayMana;

	public static void initConfiguration(FMLPreInitializationEvent event) {
		loadMainConfig();
	}

	public static void loadMainConfig() {
		Configuration config = new Configuration(new File("config/Practical-Logistics/Main-Config.cfg"));
		config.load();
		sapphireOre = config.getBoolean("Generate Ore", "settings", true, "Sapphire Ore");
		displayMana = config.getBoolean("Mana", "settings", false, "Display Mana Percentage");
		config.save();

	}

}
