package sonar.logistics.client;

import org.lwjgl.opengl.GL11;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.parts.ArrayPart;

public class RenderArray extends MultipartSpecialRenderer<ArrayPart> {

	@Override
	public void renderMultipartAt(ArrayPart part, double x, double y, double z, float partialTicks, int destroyStage) {
		RenderHelper.offsetRendering(part.getPos(), partialTicks);
		InfoRenderer.rotateDisplayRendering(part.face, EnumFacing.NORTH);
		GL11.glRotated(90, 1, 0, 0);
		GL11.glScaled(0.7, 0.7, 0.7);
		GL11.glTranslated(-8.91, -8.0, 0.45);
		for (int i = 0; i < part.inventory.getSizeInventory(); i++) {
			ItemStack stack = part.inventory.getStackInSlot(i);
			if (stack != null) {
				GlStateManager.pushMatrix();
				if (i < 4) {
					GlStateManager.translate(0, 0, i * 0.18);
				} else
					GlStateManager.translate(0.36, 0, (i - 4) * 0.18);
				RenderHelper.renderItem(stack, TransformType.NONE);
				GlStateManager.popMatrix();
			}
		}
		GlStateManager.popMatrix();
	}

}
