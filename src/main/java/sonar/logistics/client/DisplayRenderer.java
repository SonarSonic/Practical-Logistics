package sonar.logistics.client;

import org.lwjgl.opengl.GL11;

import mcmultipart.client.multipart.MultipartSpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import sonar.logistics.parts.DisplayScreenPart;

//TWEAKED FAST MSR
public class DisplayRenderer extends MultipartSpecialRenderer<DisplayScreenPart> {

	public static final int[] rotate = new int[] { 0, 0, 0, 180, 270, 90 };
	public static final double[][] matrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 1, 0, -1 }, { 1, 0, 0 }, { 0, 0, -1 } };
	public static final double[][] downMatrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 1, 0 }, { 1, 0, 0 }, { 0, 0, 0 }, { 1, 1, 0 } };
	public static final double[][] upMatrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, -1 }, { 1, 1, -1 }, { 1, 0, -1 }, { 0, 1, -1 } };
	
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
		EnumFacing rotation = part.rotation;
		double[] translate = matrix[face.ordinal()];
		GL11.glRotated(180, 0, 0, 1);
		switch (face) {
		case DOWN:
			GL11.glRotated(90, 1, 0, 0);
			int ordinal = rotation.ordinal();
			ordinal = ordinal == 4?5 : ordinal==5?4 : ordinal;
			GL11.glRotated(rotate[ordinal], 0, 0, 1);
			translate = downMatrix[ordinal];
			break;
		case UP:
			GL11.glRotated(270, 1, 0, 0);
			GL11.glRotated(rotate[rotation.ordinal()], 0, 0, 1);
			translate = upMatrix[rotation.ordinal()];
			break;
		default:
			GL11.glRotated(rotate[face.ordinal()], 0, 1, 0);
			break;

		}
		GL11.glTranslated(translate[0], translate[1], translate[2] - 0.005);
		part.container().renderContainer();
		GlStateManager.popMatrix();
	}
}
