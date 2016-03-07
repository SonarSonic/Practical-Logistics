package sonar.logistics.registries;

import net.minecraft.item.Item;
import sonar.logistics.Logistics;
import sonar.logistics.common.items.DisplayScreen;
import sonar.logistics.common.items.WirelessTransceiver;
import cpw.mods.fml.common.registry.GameRegistry;

public class ItemRegistry extends Logistics {

	public static Item energyScreen, displayScreen, digitalSign, sapphire, sapphire_dust, stone_plate, transceiver;

	public static void registerItems() {
		displayScreen = new DisplayScreen().setUnlocalizedName("DisplayScreenItem").setCreativeTab(Logistics.creativeTab).setTextureName(MODID + ":" + "display_screen");
		GameRegistry.registerItem(displayScreen, "DisplayScreenItem");
		/*
		digitalSign = new DigitalSign().setUnlocalizedName("DigitalSignItem").setCreativeTab(Logistics.creativeTab).setTextureName(modid + ":" + "digital_sign");
		GameRegistry.registerItem(digitalSign, "DigitalSignItem");	
		*/	
		sapphire = new Item().setUnlocalizedName("Sapphire").setCreativeTab(Logistics.creativeTab).setTextureName(MODID + ":" + "sapphire");
		GameRegistry.registerItem(sapphire, "Sapphire");
		sapphire_dust = new Item().setUnlocalizedName("SapphireDust").setCreativeTab(Logistics.creativeTab).setTextureName(MODID + ":" + "sapphire_dust");
		GameRegistry.registerItem(sapphire_dust, "SapphireDust");
		stone_plate = new Item().setUnlocalizedName("StonePlate").setCreativeTab(Logistics.creativeTab).setTextureName(MODID + ":" + "stone_plate");
		GameRegistry.registerItem(stone_plate, "StonePlate");
		transceiver = new WirelessTransceiver().setUnlocalizedName("Transceiver").setCreativeTab(Logistics.creativeTab).setMaxStackSize(1).setTextureName(MODID + ":" + "Transceiver");
		GameRegistry.registerItem(transceiver, "Transceiver");
	}
}
