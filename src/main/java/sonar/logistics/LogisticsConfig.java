package sonar.logistics;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
