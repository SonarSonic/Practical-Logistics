package sonar.logistics.network;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import sonar.logistics.client.BlockRenderRegister;
import sonar.logistics.client.ItemRenderRegister;
import sonar.logistics.client.RenderBlockSelection;

public class LogisticsClient extends LogisticsCommon {

	public void registerRenderThings() {
		ItemRenderRegister.register();
		BlockRenderRegister.register();
        MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent evt) {
		RenderBlockSelection.tick(evt);
	}

}
