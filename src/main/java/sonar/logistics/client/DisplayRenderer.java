package sonar.logistics.client;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.client.renderer.GlStateManager;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.helpers.InfoRenderer;

//TWEAKED FAST MSR
public class DisplayRenderer extends MultipartSpecialRenderer<ScreenMultipart> {

	@Override
	public void renderMultipartAt(ScreenMultipart part, double x, double y, double z, float partialTicks, int destroyStage) {
		RenderHelper.offsetRendering(part.getPos(), partialTicks);
		InfoRenderer.rotateDisplayRendering(part.face, part.rotation);
		part.container().renderContainer();
		GlStateManager.popMatrix();
	}
}
