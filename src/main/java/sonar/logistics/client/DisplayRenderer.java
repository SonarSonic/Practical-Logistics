package sonar.logistics.client;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.client.renderer.GlStateManager;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.api.display.ConnectedDisplayScreen;
import sonar.logistics.api.display.ILargeDisplay;
import sonar.logistics.common.multiparts.LargeDisplayScreenPart;
import sonar.logistics.common.multiparts.ScreenMultipart;
import sonar.logistics.helpers.InfoRenderer;

//TWEAKED FAST MSR
public class DisplayRenderer extends MultipartSpecialRenderer<ScreenMultipart> {

	@Override
	public void renderMultipartAt(ScreenMultipart part, double x, double y, double z, float partialTicks, int destroyStage) {
		if (part instanceof ILargeDisplay && !((ILargeDisplay) part).shouldRender()) {
			return;
		}
		RenderHelper.offsetRendering(part.getPos(), partialTicks);
		if (part instanceof ILargeDisplay) {
			ConnectedDisplayScreen screen = ((ILargeDisplay) part).getDisplayScreen();
			InfoRenderer.rotateDisplayRendering(part.face, part.rotation, screen.width.getObject(), screen.height.getObject());
		} else {
			InfoRenderer.rotateDisplayRendering(part.face, part.rotation, 0, 0);
		}
		part.container().renderContainer();
		GlStateManager.popMatrix();
	}
}
