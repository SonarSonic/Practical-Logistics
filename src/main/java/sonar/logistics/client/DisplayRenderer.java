package sonar.logistics.client;

import org.lwjgl.opengl.GL11;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import sonar.logistics.parts.DisplayScreenPart;

//TWEAKED FAST MSR
public class DisplayRenderer extends MultipartSpecialRenderer<DisplayScreenPart> {

	@Override
	public void renderMultipartAt(DisplayScreenPart part, double x, double y, double z, float partialTicks, int destroyStage) {
		// ItemStack stack = part.getSword();
		Entity view = Minecraft.getMinecraft().getRenderViewEntity();
		double vX = view.lastTickPosX + (view.posX - view.lastTickPosX) * (double) partialTicks;
		double vY = view.lastTickPosY + (view.posY - view.lastTickPosY) * (double) partialTicks;
		double vZ = view.lastTickPosZ + (view.posZ - view.lastTickPosZ) * (double) partialTicks;
		GlStateManager.pushMatrix();
		GlStateManager.translate(part.getPos().getX() - vX, part.getPos().getY() - vY, part.getPos().getZ() - vZ);
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		EnumFacing face = part.face;
		GL11.glRotated(180, 0, 0, 1);
		//GL11.glRotated(face.getHorizontalAngle(), 0 , 1, 0);
		GL11.glTranslated(0, 0, -0.005);
		//GL11.glScaled(1, 1, 1);
		switch(face){
		case DOWN:
			break;
		case UP:
			GL11.glRotated(90, 1, 0, 0);
			break;
		default:
			break;
			
		}
		
		//GL11.glRotated(part.face.getHorizontalAngle(), 0, 1, 0);
		/*
		switch (part.face) {
		case EAST:
			GL11.glTranslated(0.41, 1 + 0.0425, -0.0625 * 2);
			break;
		case NORTH:
			GL11.glTranslated(-1 + 0.41, 1 + 0.0425, -1 + 0.0625 * 2);
			break;
		case SOUTH:
			GL11.glTranslated(0.41, 1 + 0.0425, 0.0625 * 2);
			break;
		case WEST:
			GL11.glTranslated(-1 + 0.41, 1 + 0.0425, 1 - 0.0625 * 2);
			break;
		default:
			break;
		}
		*/
		part.container().renderContainer();
		GlStateManager.popMatrix();
	}
}
