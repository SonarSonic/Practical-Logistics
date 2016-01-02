package sonar.logistics.registries;

import net.minecraftforge.oredict.OreDictionary;


public class OreDictRegistry {

	public static void registerOres(){		
		OreDictionary.registerOre("oreSapphire", BlockRegistry.sapphire_ore);
		OreDictionary.registerOre("gemSapphire", ItemRegistry.sapphire);
		OreDictionary.registerOre("dustSapphire", ItemRegistry.sapphire_dust);
	}
}
