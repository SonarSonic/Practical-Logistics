package sonar.logistics.network;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import sonar.logistics.client.renderers.RenderDisplayScreen;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.client.renderers.RenderHolographicDisplay;
import sonar.logistics.client.renderers.RenderItemHandlers;
import sonar.logistics.client.renderers.RenderLargeDisplay;
import sonar.logistics.common.tileentity.TileEntityBlockNode;
import sonar.logistics.common.tileentity.TileEntityChannelSelector;
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
import sonar.logistics.common.tileentity.TileEntityMultiDataCable;
import sonar.logistics.common.tileentity.TileEntityRedstoneSignaller;
import sonar.logistics.registries.BlockRegistry;
import cpw.mods.fml.client.registry.ClientRegistry;

public class LogisticsClient extends LogisticsCommon {

	public void registerRenderThings() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisplayScreen.class, new RenderDisplayScreen());
		
		TileEntitySpecialRenderer blockNode = new RenderHandlers.BlockNode();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockNode.class, blockNode);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.node), new RenderItemHandlers.Node(blockNode, new TileEntityBlockNode()));
		
		TileEntitySpecialRenderer largeDisplay = new RenderLargeDisplay();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLargeScreen.class, largeDisplay);
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.largeDisplayScreen), new RenderItemHandlers.Node(largeDisplay, new TileEntityLargeScreen()));
		
		TileEntitySpecialRenderer entityNode = new RenderHandlers.EntityNode();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEntityNode.class, entityNode);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.entityNode), new RenderItemHandlers.EntityNode(entityNode, new TileEntityEntityNode()));

		TileEntitySpecialRenderer dataCable = new RenderHandlers.BlockCable();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDataCable.class, dataCable);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataCable), new RenderItemHandlers.Cable(dataCable, new TileEntityDataCable()));
		
		TileEntitySpecialRenderer dataMultiCable = new RenderHandlers.BlockMultiCable();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMultiDataCable.class, dataMultiCable);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataMultiCable), new RenderItemHandlers.Cable(dataMultiCable, new TileEntityMultiDataCable()));
		
		TileEntitySpecialRenderer dataModifier = new RenderHandlers.DataModifier();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDataModifier.class, dataModifier);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataModifier), new RenderItemHandlers.FramedCable(dataModifier, new TileEntityDataModifier()));
		
		TileEntitySpecialRenderer infoCreator = new RenderHandlers.InfoCreator();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoCreator.class, infoCreator);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.infoCreator), new RenderItemHandlers.FramedCable(infoCreator, new TileEntityInfoCreator()));
		
		TileEntitySpecialRenderer channelSelector = new RenderHandlers.ChannelSelector();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChannelSelector.class, channelSelector);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.channelSelector), new RenderItemHandlers.FramedCable(channelSelector, new TileEntityChannelSelector()));
			
		TileEntitySpecialRenderer infoReader = new RenderHandlers.InfoReader();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoReader.class, infoReader);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.infoReader), new RenderItemHandlers.Reader(infoReader, new TileEntityInfoReader()));
		
		TileEntitySpecialRenderer inventoryReader = new RenderHandlers.InventoryReader();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInventoryReader.class, inventoryReader);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.inventoryReader), new RenderItemHandlers.Reader(inventoryReader, new TileEntityInventoryReader()));
		
		TileEntitySpecialRenderer fluidReader = new RenderHandlers.FluidReader();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFluidReader.class, fluidReader);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.fluidReader), new RenderItemHandlers.Reader(fluidReader, new TileEntityFluidReader()));
		
		TileEntitySpecialRenderer energyReader = new RenderHandlers.EnergyReader();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyReader.class, energyReader);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.energyReader), new RenderItemHandlers.Reader(energyReader, new TileEntityEnergyReader()));
		
		TileEntitySpecialRenderer dataReceiver = new RenderHandlers.DataReceiver();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDataReceiver.class, dataReceiver);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataReceiver), new RenderItemHandlers.DataBlock(dataReceiver, new TileEntityDataReceiver()));
		
		TileEntitySpecialRenderer dataEmitter = new RenderHandlers.DataEmitter();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDataEmitter.class, dataEmitter);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataEmitter), new RenderItemHandlers.DataBlock(dataEmitter, new TileEntityDataEmitter()));

		TileEntitySpecialRenderer signaller = new RenderHandlers.RedstoneSignaller();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRedstoneSignaller.class, signaller);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.redstoneSignaller_off), new RenderItemHandlers.DataBlock(signaller, new TileEntityRedstoneSignaller()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.redstoneSignaller_on), new RenderItemHandlers.DataBlock(signaller, new TileEntityRedstoneSignaller()));
	
		TileEntitySpecialRenderer holographicDisplay = new RenderHolographicDisplay();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHolographicDisplay.class, holographicDisplay);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.holographicDisplay), new RenderItemHandlers.HolographicDisplay(holographicDisplay, new TileEntityHolographicDisplay()));
		
		TileEntitySpecialRenderer hammer = new RenderHandlers.Hammer();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHammer.class, hammer);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.hammer), new RenderItemHandlers.Hammer(hammer, new TileEntityHammer()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.hammer_air), new RenderItemHandlers.Hammer(hammer, new TileEntityHammer()));
		
		TileEntitySpecialRenderer itemRouter = new RenderHandlers.ItemRouter();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityItemRouter.class, itemRouter);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.itemRouter), new RenderItemHandlers.ItemRouter(itemRouter, new TileEntityItemRouter()));
		
		TileEntitySpecialRenderer clock = new RenderHandlers.Clock();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClock.class, clock);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.clock), new RenderItemHandlers.Clock(clock, new TileEntityClock()));
		
	}
}
