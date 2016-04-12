package sonar.logistics.registries;

import net.minecraft.block.Block;
import sonar.core.common.block.SonarBlockTip;
import sonar.logistics.Logistics;
import sonar.logistics.common.blocks.BlockChannelSelector;
import sonar.logistics.common.blocks.BlockClock;
import sonar.logistics.common.blocks.BlockDataCable;
import sonar.logistics.common.blocks.BlockDataEmitter;
import sonar.logistics.common.blocks.BlockDataModifier;
import sonar.logistics.common.blocks.BlockDataReceiver;
import sonar.logistics.common.blocks.BlockEnergyReader;
import sonar.logistics.common.blocks.BlockEntityNode;
import sonar.logistics.common.blocks.BlockFluidReader;
import sonar.logistics.common.blocks.BlockHammer;
import sonar.logistics.common.blocks.BlockHammerAir;
import sonar.logistics.common.blocks.BlockHolographicDisplay;
import sonar.logistics.common.blocks.BlockInfoCreator;
import sonar.logistics.common.blocks.BlockInfoReader;
import sonar.logistics.common.blocks.BlockInventoryReader;
import sonar.logistics.common.blocks.BlockItemRouter;
import sonar.logistics.common.blocks.BlockChannelledCable;
import sonar.logistics.common.blocks.BlockNode;
import sonar.logistics.common.blocks.BlockRedstoneSignaller;
import sonar.logistics.common.blocks.BlockSapphireOre;
import sonar.logistics.common.blocks.BlockTransceiverArray;
import sonar.logistics.common.blocks.DisplayScreen;
import sonar.logistics.common.blocks.LargeDisplayScreen;
import sonar.logistics.common.tileentity.TileEntityArray;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityChannelSelector;
import sonar.logistics.common.tileentity.TileEntityChannelledCable;
import sonar.logistics.common.tileentity.TileEntityClock;
import sonar.logistics.common.tileentity.TileEntityDataCable;
import sonar.logistics.common.tileentity.TileEntityDataEmitter;
import sonar.logistics.common.tileentity.TileEntityDataModifier;
import sonar.logistics.common.tileentity.TileEntityDataReceiver;
import sonar.logistics.common.tileentity.TileEntityDisplayScreen;
import sonar.logistics.common.tileentity.TileEntityEnergyReader;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.common.tileentity.TileEntityFluidReader;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.common.tileentity.TileEntityHolographicDisplay;
import sonar.logistics.common.tileentity.TileEntityInfoCreator;
import sonar.logistics.common.tileentity.TileEntityInfoReader;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
import sonar.logistics.common.tileentity.TileEntityLargeScreen;
import sonar.logistics.common.tileentity.TileEntityRedstoneSignaller;
import cpw.mods.fml.common.registry.GameRegistry;

public class BlockRegistry extends Logistics {

	// displays
	public static Block displayScreen, largeDisplayScreen, holographicDisplay, digitalSign_wall, digitalSign_standing;

	// readers
	public static Block infoReader, inventoryReader, fluidReader, energyReader;

	// connections
	public static Block node, entityNode, dataCable, channelledCable, dataReceiver, dataEmitter, dataModifier, infoCreator, channelSelector, transceiverArray, expulsion_port, admission_port;

	// misc
	public static Block redstoneSignaller_on, redstoneSignaller_off, sapphire_ore, hammer, hammer_air, itemRouter, clock;

	public static void registerBlocks() {

		hammer = new BlockHammer().setBlockName("Hammer").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "hammer_break");
		GameRegistry.registerBlock(hammer, SonarBlockTip.class, "Hammer");
		GameRegistry.registerTileEntity(TileEntityHammer.class, "Hammer");

		hammer_air = new BlockHammerAir().setBlockName("Hammer_Air").setHardness(1.0F).setResistance(20.0F).setBlockTextureName(MODID + ":" + "hammer_break");
		GameRegistry.registerBlock(hammer_air, SonarBlockTip.class, "Hammer_Air");

		sapphire_ore = new BlockSapphireOre().setBlockName("SapphireOre").setHardness(3.0F).setCreativeTab(Logistics.creativeTab).setResistance(5.0F).setBlockTextureName(MODID + ":" + "sapphire_ore");
		GameRegistry.registerBlock(sapphire_ore, "SapphireOre");

		displayScreen = new DisplayScreen().setBlockName("DisplayScreen").setHardness(0.2F).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(displayScreen, SonarBlockTip.class, "DisplayScreen");
		GameRegistry.registerTileEntity(TileEntityDisplayScreen.class, "DisplayScreen");
		
		dataCable = new BlockDataCable().setBlockName("DataCable").setHardness(0.1F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(dataCable, SonarBlockTip.class, "DataCable");
		GameRegistry.registerTileEntity(TileEntityDataCable.class, "DataCable");

		channelledCable = new BlockChannelledCable().setBlockName("MultiCable").setHardness(0.1F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(channelledCable, SonarBlockTip.class, "MultiCable");
		GameRegistry.registerTileEntity(TileEntityChannelledCable.class, "MultiCable");

		node = new BlockNode().setBlockName("Node").setHardness(1.0F).setResistance(20.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(node, SonarBlockTip.class, "Node");
		GameRegistry.registerTileEntity(TileEntityBlockNode.class, "Node");

		entityNode = new BlockEntityNode().setBlockName("EntityNode").setHardness(1.0F).setResistance(20.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(entityNode, SonarBlockTip.class, "EntityNode");
		GameRegistry.registerTileEntity(TileEntityEntityNode.class, "EntityNode");

		infoReader = new BlockInfoReader().setBlockName("InfoReader").setHardness(1.0F).setResistance(20.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(infoReader, SonarBlockTip.class, "InfoReader");
		GameRegistry.registerTileEntity(TileEntityInfoReader.class, "InfoReader");

		inventoryReader = new BlockInventoryReader().setBlockName("InventoryReader").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(inventoryReader, SonarBlockTip.class, "InventoryReader");
		GameRegistry.registerTileEntity(TileEntityInventoryReader.class, "InventoryReader");

		fluidReader = new BlockFluidReader().setBlockName("FluidReader").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(fluidReader, SonarBlockTip.class, "FluidReader");
		GameRegistry.registerTileEntity(TileEntityFluidReader.class, "FluidReader");

		energyReader = new BlockEnergyReader().setBlockName("EnergyReader").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(energyReader, SonarBlockTip.class, "EnergyReader");
		GameRegistry.registerTileEntity(TileEntityEnergyReader.class, "EnergyReader");

		dataReceiver = new BlockDataReceiver().setBlockName("DataReceiver").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(dataReceiver, SonarBlockTip.class, "DataReceiver");
		GameRegistry.registerTileEntity(TileEntityDataReceiver.class, "DataReceiver");

		dataEmitter = new BlockDataEmitter().setBlockName("DataEmitter").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(dataEmitter, SonarBlockTip.class, "DataEmitter");
		GameRegistry.registerTileEntity(TileEntityDataEmitter.class, "DataEmitter");

		redstoneSignaller_off = new BlockRedstoneSignaller(false).setBlockName("RedstoneSignaller").setHardness(0.1F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		redstoneSignaller_on = new BlockRedstoneSignaller(true).setBlockName("RedstoneSignaller").setHardness(0.1F).setLightLevel(0.5F).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(redstoneSignaller_on, SonarBlockTip.class, "RedstoneSignaller_ON");
		GameRegistry.registerBlock(redstoneSignaller_off, SonarBlockTip.class, "RedstoneSignaller_OFF");
		GameRegistry.registerTileEntity(TileEntityRedstoneSignaller.class, "RedstoneSignaller");

		clock = new BlockClock().setBlockName("Clock").setHardness(1.0F).setResistance(100.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(clock, SonarBlockTip.class, "Clock");
		GameRegistry.registerTileEntity(TileEntityClock.class, "Clock");

		dataModifier = new BlockDataModifier().setBlockName("DataModifier").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(dataModifier, SonarBlockTip.class, "DataModifier");
		GameRegistry.registerTileEntity(TileEntityDataModifier.class, "DataModifier");

		infoCreator = new BlockInfoCreator().setBlockName("InfoCreator").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(infoCreator, SonarBlockTip.class, "InfoCreator");
		GameRegistry.registerTileEntity(TileEntityInfoCreator.class, "InfoCreator");

		channelSelector = new BlockChannelSelector().setBlockName("ChannelSelector").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(channelSelector, SonarBlockTip.class, "ChannelSelector");
		GameRegistry.registerTileEntity(TileEntityChannelSelector.class, "ChannelSelector");

		transceiverArray = new BlockTransceiverArray().setBlockName("TransceiverArray").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(transceiverArray, SonarBlockTip.class, "TransceiverArray");
		GameRegistry.registerTileEntity(TileEntityArray.class, "TransceiverArray");
		
		holographicDisplay = new BlockHolographicDisplay().setBlockName("HolographicDisplay").setHardness(1.0F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(holographicDisplay, SonarBlockTip.class, "HolographicDisplay");
		GameRegistry.registerTileEntity(TileEntityHolographicDisplay.class, "HolographicDisplay");

		largeDisplayScreen = new LargeDisplayScreen().setBlockName("LargeDisplayScreen").setHardness(0.2F).setCreativeTab(Logistics.creativeTab).setResistance(20.0F).setBlockTextureName(MODID + ":" + "large_screen");
		GameRegistry.registerBlock(largeDisplayScreen, SonarBlockTip.class, "LargeDisplayScreen");
		GameRegistry.registerTileEntity(TileEntityLargeScreen.class, "LargeDisplayScreen");

		itemRouter = new BlockItemRouter().setBlockName("ItemRouter").setHardness(1.0F).setResistance(100.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(MODID + ":" + "data_cable");
		GameRegistry.registerBlock(itemRouter, SonarBlockTip.class, "ItemRouter");
		GameRegistry.registerTileEntity(TileEntityItemRouter.class, "ItemRouter");
		
		/*
		expulsion_port = new BlockExpulsionPort().setBlockName("ExpulsionPort").setHardness(1.0F).setResistance(100.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(modid + ":" + "data_cable");
		GameRegistry.registerBlock(expulsion_port, SonarBlockTip.class, "ExpulsionPort");
		GameRegistry.registerTileEntity(TileEntityExpulsionPort.class, "ExpulsionPort");		

		digitalSign_wall = new BlockDigitalSign(TileEntityDigitalScreen.class, false).setBlockName("DigitalScreen");
		digitalSign_standing = new BlockDigitalSign(TileEntityDigitalScreen.class, true).setBlockName("DigitalScreen");
		GameRegistry.registerBlock(digitalSign_wall, SonarBlockTip.class, "DigitalScreen_Wall");
		GameRegistry.registerBlock(digitalSign_standing, SonarBlockTip.class, "DigitalScreen_Standing");
		GameRegistry.registerTileEntity(TileEntityDigitalScreen.class, "DigitalScreen");

		*/
	}

}
