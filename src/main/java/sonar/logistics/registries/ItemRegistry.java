package sonar.logistics.registries;

import net.minecraft.item.Item;
import sonar.logistics.Logistics;
import sonar.logistics.common.items.DigitalSign;
import sonar.logistics.common.items.DisplayScreen;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegistry extends Logistics {

	public static Item energyScreen, displayScreen, digitalSign, sapphire, sapphire_dust, stone_plate;

	public static void registerItems() {
		displayScreen = new DisplayScreen().setUnlocalizedName("DisplayScreenItem").setCreativeTab(Logistics.creativeTab).setTextureName(modid + ":" + "display_screen");
		GameRegistry.registerItem(displayScreen, "DisplayScreenItem");
		/*
		digitalSign = new DigitalSign().setUnlocalizedName("DigitalSignItem").setCreativeTab(Logistics.creativeTab).setTextureName(modid + ":" + "digital_sign");
		GameRegistry.registerItem(digitalSign, "DigitalSignItem");	
		*/	
		sapphire = new Item().setUnlocalizedName("Sapphire").setCreativeTab(Logistics.creativeTab).setTextureName(modid + ":" + "sapphire");
		GameRegistry.registerItem(sapphire, "Sapphire");
		sapphire_dust = new Item().setUnlocalizedName("SapphireDust").setCreativeTab(Logistics.creativeTab).setTextureName(modid + ":" + "sapphire_dust");
		GameRegistry.registerItem(sapphire_dust, "SapphireDust");
		stone_plate = new Item().setUnlocalizedName("StonePlate").setCreativeTab(Logistics.creativeTab).setTextureName(modid + ":" + "stone_plate");
		GameRegistry.registerItem(stone_plate, "StonePlate");
	}
}
