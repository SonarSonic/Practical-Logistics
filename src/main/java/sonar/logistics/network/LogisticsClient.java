package sonar.logistics.network;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import sonar.logistics.client.renderers.*;
import sonar.logistics.common.tileentity.*;
import sonar.logistics.registries.BlockRegistry;
import cpw.mods.fml.client.registry.ClientRegistry;

public class LogisticsClient extends LogisticsCommon {

	public void registerRenderThings() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisplayScreen.class, new RenderDisplayScreen());
		
		TileEntitySpecialRenderer blockNode = new RenderHandlers.BlockNode();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockNode.class, blockNode);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.node), new RenderItemHandlers.Node(blockNode, new TileEntityBlockNode()));
		
		TileEntitySpecialRenderer entityNode = new RenderHandlers.EntityNode();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEntityNode.class, entityNode);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.entityNode), new RenderItemHandlers.EntityNode(entityNode, new TileEntityEntityNode()));

		TileEntitySpecialRenderer dataCable = new RenderHandlers.BlockCable();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDataCable.class, dataCable);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataCable), new RenderItemHandlers.Cable(dataCable, new TileEntityDataCable()));
		
		TileEntitySpecialRenderer dataModifier = new RenderHandlers.DataModifier();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDataModifier.class, dataModifier);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.dataModifier), new RenderItemHandlers.DataModifier(dataModifier, new TileEntityDataModifier()));
		
		TileEntitySpecialRenderer infoCreator = new RenderHandlers.InfoCreator();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoCreator.class, infoCreator);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.infoCreator), new RenderItemHandlers.DataModifier(infoCreator, new TileEntityInfoCreator()));
		
		
		TileEntitySpecialRenderer infoNode = new RenderHandlers.InfoNode();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInfoReader.class, infoNode);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.infoReader), new RenderItemHandlers.Reader(infoNode, new TileEntityInfoReader()));
		

		TileEntitySpecialRenderer inventoryReader = new RenderHandlers.InventoryReader();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInventoryReader.class, inventoryReader);
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockRegistry.inventoryReader), new RenderItemHandlers.Reader(inventoryReader, new TileEntityInventoryReader()));
		
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

	}
}
