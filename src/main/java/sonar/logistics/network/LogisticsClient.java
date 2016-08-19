package sonar.logistics.network;

import mcmultipart.client.multipart.MultipartRegistryClient;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sonar.logistics.client.BlockRenderRegister;
import sonar.logistics.client.DisplayRenderer;
import sonar.logistics.client.ItemRenderRegister;
import sonar.logistics.client.RenderBlockSelection;
import sonar.logistics.client.RenderHammer;
import sonar.logistics.common.blocks.tileentity.TileEntityHammer;
import sonar.logistics.parts.DisplayScreenPart;

public class LogisticsClient extends LogisticsCommon {

	public void registerRenderThings() {
		ItemRenderRegister.register();
		BlockRenderRegister.register();

		MultipartRegistryClient.bindMultipartSpecialRenderer(DisplayScreenPart.class, new DisplayRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityHammer.class, new RenderHammer());
        MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent evt) {
		RenderBlockSelection.tick(evt);
	}

}
