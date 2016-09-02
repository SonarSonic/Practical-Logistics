package sonar.logistics.network;

import mcmultipart.client.multipart.MultipartRegistryClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sonar.logistics.api.info.InfoContainer;
import sonar.logistics.client.BlockRenderRegister;
import sonar.logistics.client.DisplayRenderer;
import sonar.logistics.client.ItemRenderRegister;
import sonar.logistics.client.RenderArray;
import sonar.logistics.client.RenderBlockSelection;
import sonar.logistics.client.RenderHammer;
import sonar.logistics.client.RenderOperatorOverlay;
import sonar.logistics.common.blocks.tileentity.TileEntityHammer;
import sonar.logistics.parts.ArrayPart;
import sonar.logistics.parts.DisplayScreenPart;
import sonar.logistics.parts.LargeDisplayScreenPart;

public class LogisticsClient extends LogisticsCommon {

	public void registerRenderThings() {
		ItemRenderRegister.register();
		BlockRenderRegister.register();

		MultipartRegistryClient.bindMultipartSpecialRenderer(DisplayScreenPart.class, new DisplayRenderer());
		MultipartRegistryClient.bindMultipartSpecialRenderer(LargeDisplayScreenPart.class, new DisplayRenderer());
		MultipartRegistryClient.bindMultipartSpecialRenderer(ArrayPart.class, new RenderArray());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHammer.class, new RenderHammer());
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void registerTextures() {
		// Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(InfoContainer.progressGreen);
	}

	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent evt) {
		RenderBlockSelection.tick(evt);
	}

	@SubscribeEvent
	public void renderHighlight(DrawBlockHighlightEvent evt) {
		RenderOperatorOverlay.tick(evt);
	}

	public void setUsingOperator(boolean bool) {
		RenderOperatorOverlay.isUsing = bool;
	}

	public boolean isUsingOperator(){
		return RenderOperatorOverlay.isUsing;
	}
	
}
