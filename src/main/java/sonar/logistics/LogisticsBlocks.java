package sonar.logistics;

import java.util.ArrayList;

import net.minecraft.block.Block;
import sonar.core.SonarCore;
import sonar.core.registries.ISonarRegistryBlock;
import sonar.core.registries.SonarRegistryBlock;
import sonar.logistics.common.blocks.BlockHammer;
import sonar.logistics.common.blocks.BlockHammerAir;
import sonar.logistics.common.blocks.BlockSapphireOre;
import sonar.logistics.common.tileentity.TileEntityHammer;

public class LogisticsBlocks extends Logistics {

	public static ArrayList<ISonarRegistryBlock> registeredBlocks = new ArrayList();

	// displays
	public static Block displayScreen, largeDisplayScreen, holographicDisplay, digitalSign_wall, digitalSign_standing;

	// readers
	//public static Block infoReader, inventoryReader, fluidReader, energyReader;

	// connections
	public static Block /*node, entityNode, dataCable, channelledCable,*/ dataReceiver, dataEmitter, dataModifier, infoCreator, channelSelector, /*transceiverArray, */ expulsion_port, admission_port;

	// misc
	public static Block redstoneSignaller_on, redstoneSignaller_off, sapphire_ore, hammer, hammer_air, itemRouter, clock;

	public static Block register(SonarRegistryBlock register) {
		Block block = register.getBlock();
		block.setUnlocalizedName(register.getRegistryName());
		if (!register.ignoreNormalTab) {
			block.setCreativeTab(Logistics.creativeTab);
		}
		register.setBlock(block);
		registeredBlocks.add(register);
		return register.getBlock();
	}

	public static void registerBlocks() {
		/// HAMMER
		hammer = register(new SonarRegistryBlock(new BlockHammer(), "Hammer", TileEntityHammer.class).setProperties());
		hammer_air = register(new SonarRegistryBlock(new BlockHammerAir(), "Hammer_Air").setProperties().removeCreativeTab());		
		
		/// ORE		
		sapphire_ore = register(new SonarRegistryBlock(new BlockSapphireOre(), "SapphireOre").setProperties(3.0F, 5.0F));	

		/*
		/// CABLES
		dataCable = register(new SonarRegistryBlock(new BlockDataCable(), "DataCable", TileEntityDataCable.class).setProperties());		
		channelledCable = register(new SonarRegistryBlock(new BlockChannelledCable(), "ChannelledCable", TileEntityChannelledCable.class).setProperties());

		/// SCREENS
		displayScreen = register(new SonarRegistryBlock(new DisplayScreen(), "DisplayScreen", TileEntityDisplayScreen.class).setProperties(0.2F, 20.0F));
		largeDisplayScreen = register(new SonarRegistryBlock(new LargeDisplayScreen(), "LargeDisplayScreen", TileEntityLargeScreen.class).setProperties());		
		holographicDisplay = register(new SonarRegistryBlock(new BlockHolographicDisplay(), "HolographicDisplay", TileEntityHolographicDisplay.class).setProperties());

		/// NODES
		node = register(new SonarRegistryBlock(new BlockNode(), "Node", TileEntityBlockNode.class).setProperties());
		entityNode = register(new SonarRegistryBlock(new BlockEntityNode(), "EntityNode", TileEntityEntityNode.class).setProperties());
		transceiverArray = register(new SonarRegistryBlock(new BlockTransceiverArray(), "TransceiverArray", TileEntityArray.class).setProperties());		

		/// READERS
		infoReader = register(new SonarRegistryBlock(new BlockInfoReader(), "InfoReader", TileEntityInfoReader.class).setProperties());
		inventoryReader = register(new SonarRegistryBlock(new BlockInventoryReader(), "InventoryReader", TileEntityInventoryReader.class).setProperties());
		fluidReader = register(new SonarRegistryBlock(new BlockFluidReader(), "FluidReader", TileEntityFluidReader.class).setProperties());
		energyReader = register(new SonarRegistryBlock(new BlockEnergyReader(), "EnergyReader", TileEntityEnergyReader.class).setProperties());
		
		/// WIRELESS
		dataReceiver = register(new SonarRegistryBlock(new BlockDataReceiver(), "DataReceiver", TileEntityDataReceiver.class).setProperties());
		dataEmitter = register(new SonarRegistryBlock(new BlockDataEmitter(), "DataEmitter", TileEntityDataEmitter.class).setProperties());

		/// SIGNALLERS
		redstoneSignaller_off = register(new SonarRegistryBlock(new BlockRedstoneSignaller(false), "RedstoneSignaller_Off").setProperties());
		redstoneSignaller_off = register(new SonarRegistryBlock(new BlockRedstoneSignaller(true).setLightLevel(0.5F), "RedstoneSignaller_On").removeCreativeTab().setProperties());
		GameRegistry.registerTileEntity(TileEntityRedstoneSignaller.class, "RedstoneSignaller");
		
		/// DATA HANDLING
		clock = register(new SonarRegistryBlock(new BlockClock(), "Clock", TileEntityClock.class).setProperties());
		dataModifier = register(new SonarRegistryBlock(new BlockDataModifier(), "DataModifier", TileEntityDataModifier.class).setProperties());
		infoCreator = register(new SonarRegistryBlock(new BlockInfoCreator(), "InfoCreator", TileEntityInfoCreator.class).setProperties());
		channelSelector = register(new SonarRegistryBlock(new BlockChannelSelector(), "ChannelSelector", TileEntityChannelSelector.class).setProperties());
		// ITEM HANDLING
		itemRouter = register(new SonarRegistryBlock(new BlockItemRouter(), "ItemRouter", TileEntityItemRouter.class).setProperties());

		
		expulsion_port = new BlockExpulsionPort().setBlockName("ExpulsionPort").setHardness(1.0F).setResistance(100.0F).setCreativeTab(Logistics.creativeTab).setBlockTextureName(modid + ":" + "data_cable"); GameRegistry.registerBlock(expulsion_port, SonarBlockTip.class, "ExpulsionPort"); GameRegistry.registerTileEntity(TileEntityExpulsionPort.class, "ExpulsionPort");
		digitalSign_wall = new BlockDigitalSign(TileEntityDigitalScreen.class, false).setBlockName("DigitalScreen"); digitalSign_standing = new BlockDigitalSign(TileEntityDigitalScreen.class, true).setBlockName("DigitalScreen"); GameRegistry.registerBlock(digitalSign_wall, SonarBlockTip.class, "DigitalScreen_Wall"); GameRegistry.registerBlock(digitalSign_standing, SonarBlockTip.class, "DigitalScreen_Standing"); GameRegistry.registerTileEntity(TileEntityDigitalScreen.class, "DigitalScreen");
		
		*/
		SonarCore.registerBlocks(registeredBlocks);
	}

}
