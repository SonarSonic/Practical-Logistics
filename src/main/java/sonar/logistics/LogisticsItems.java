package sonar.logistics;

import java.util.ArrayList;

import net.minecraft.item.Item;
import sonar.core.SonarCore;
import sonar.core.registries.ISonarRegistryItem;
import sonar.core.registries.SonarRegistryItem;
import sonar.logistics.common.items.ItemDefaultMultipart;
import sonar.logistics.common.items.ItemFacingMultipart;
import sonar.logistics.common.items.ItemSidedMultipart;
import sonar.logistics.common.items.WirelessTransceiver;
import sonar.logistics.parts.ArrayPart;
import sonar.logistics.parts.DataCablePart;
import sonar.logistics.parts.DataEmitterPart;
import sonar.logistics.parts.DataReceiverPart;
import sonar.logistics.parts.DisplayScreenPart;
import sonar.logistics.parts.FluidReaderPart;
import sonar.logistics.parts.InfoReaderPart;
import sonar.logistics.parts.InventoryReaderPart;
import sonar.logistics.parts.LargeDisplayScreenPart;
import sonar.logistics.parts.NodePart;

public class LogisticsItems extends Logistics {

	public static ArrayList<ISonarRegistryItem> registeredItems = new ArrayList();

	public static Item register(SonarRegistryItem register) {
		Item item = register.getItem();
		item.setUnlocalizedName(register.getRegistryName());
		if(!register.ignoreNormalTab){
			item.setCreativeTab(Logistics.creativeTab);
		}
		register.setItem(item);
		registeredItems.add(register);
		return register.getItem();
	}
	
	public static Item energyScreen, displayScreen, largeDisplayScreen, digitalSign, sapphire, sapphire_dust, stone_plate, transceiver;

	public static Item partCable, partNode, partEntityNode, partArray, partRedstoneSignaller, partEmitter, partReceiver, infoReaderPart, inventoryReaderPart, fluidReaderPart, energyReaderPart;
	
	public static void registerItems() {
		//displayScreen = registerItem("DisplayScreenItem", new DisplayScreen());//.setTextureName(MODID + ":" + "display_screen");
		//digitalSign = registerItem("DisplayScreenItem", new DigitalSign());//.setTextureName(MODID + ":" + "digital_sign");
		sapphire = register(new SonarRegistryItem("Sapphire"));
		sapphire_dust = register(new SonarRegistryItem("SapphireDust"));
		stone_plate = register(new SonarRegistryItem("StonePlate"));
		partCable = register(new SonarRegistryItem(new ItemDefaultMultipart(DataCablePart.class), "DataCable"));
		partArray = register(new SonarRegistryItem(new ItemSidedMultipart(ArrayPart.class), "Array"));
		displayScreen = register(new SonarRegistryItem(new ItemFacingMultipart(DisplayScreenPart.class), "DisplayScreen"));
		largeDisplayScreen = register(new SonarRegistryItem(new ItemFacingMultipart(LargeDisplayScreenPart.class), "LargeDisplayScreen"));
		
		inventoryReaderPart = register(new SonarRegistryItem(new ItemSidedMultipart(InventoryReaderPart.class), "InventoryReader"));
		fluidReaderPart = register(new SonarRegistryItem(new ItemSidedMultipart(FluidReaderPart.class), "FluidReader"));
		partNode = register(new SonarRegistryItem(new ItemSidedMultipart(NodePart.class), "Node"));
		infoReaderPart = register(new SonarRegistryItem(new ItemSidedMultipart(InfoReaderPart.class), "InfoReader"));
		transceiver = register(new SonarRegistryItem(new WirelessTransceiver().setMaxStackSize(1), "Transceiver"));
		partEmitter = register(new SonarRegistryItem(new ItemFacingMultipart(DataEmitterPart.class), "DataEmitter"));
		partReceiver = register(new SonarRegistryItem(new ItemFacingMultipart(DataReceiverPart.class), "DataReceiver"));
		/*
		partCable = register(new SonarRegistryItem(new ItemDefaultMultipart(DataCablePart.class), "DataCable"));
		partEntityNode = register(new SonarRegistryItem(new ItemSidedMultipart(EntityNodePart.class), "EntityNode"));
		infoReaderPart = register(new SonarRegistryItem(new ItemSidedMultipart(InfoReaderPart.class), "InfoReader"));
		inventoryReaderPart = register(new SonarRegistryItem(new ItemSidedMultipart(InventoryReaderPart.class), "InventoryReader"));
		energyReaderPart = register(new SonarRegistryItem(new ItemSidedMultipart(EnergyReaderPart.class), "EnergyReader"));
		partRedstoneSignaller = register(new SonarRegistryItem(new ItemSidedMultipart(RedstoneSignallerPart.class), "RedstoneSignaller"));
		*/
		SonarCore.registerItems(registeredItems);
	}
}
