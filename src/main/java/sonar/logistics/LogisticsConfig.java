package sonar.logistics;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class LogisticsConfig extends Logistics {

	public static boolean sapphireOre;
	public static int sapphireOreveinmin;
	public static int sapphireOreveinmax;
	public static int sapphireOreveinchance;
	public static boolean displayMana;

	public static void initConfiguration(FMLPreInitializationEvent event) {
		loadMainConfig();
	}

	public static void loadMainConfig() {
		Configuration config = new Configuration(new File("config/Practical-Logistics/Main-Config.cfg"));
		config.load();
		sapphireOre = config.getBoolean("Generate Ore", "settings", true, "Sapphire Ore");
		sapphireOreveinmin = config.get("Minimum sapphire Ore vein size",2).getInt();
		sapphireOreveinmax = config.get("Maximum sapphire Ore vein size",5).getInt();
		sapphireOreveinchance = config.get("Maximum sapphire Ore vein chance",5).getInt();
		displayMana = config.getBoolean("Mana", "settings", false, "Display Mana Percentage");
		config.save();

	}

}
