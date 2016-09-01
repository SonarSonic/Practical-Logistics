package sonar.logistics.helpers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class InfoRenderer {

	public static final double zLevel = 0, barOffset = 0.001;

	public static void renderCenteredString(String string, float x, float y, float width, float scale, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1.0f);
		int length = RenderHelper.fontRenderer.getStringWidth(string);
		int height = RenderHelper.fontRenderer.FONT_HEIGHT;
		RenderHelper.fontRenderer.drawString(string, Math.round((x + width / 2) / scale - length / 2), Math.round(y / scale - height / 2), color);
		GlStateManager.popMatrix();
	}

	public static void renderNormalInfo(DisplayType type, String... toDisplay) {
		renderNormalInfo(type, type.width, type.height, type.scale, toDisplay);
	}

	public static double getYCentre(DisplayType type, double height) {
		return ((0.1205 * height) * (0.1205 * height)) + (0.4089 * height) - 0.56; // quadratic equation to solve the scale
	}

	public static void renderNormalInfo(DisplayType displayType, double width, double height, double scale, String... toDisplay) {
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		double yCentre = getYCentre(displayType, height);
		float offset = (float) ((float) (0.09F + scale * 2));
		double centre = ((double) (toDisplay.length) / 2) - yCentre;
		for (int i = 0; i < toDisplay.length; i++) {
			renderCenteredString(toDisplay[i], -1, (float) (i == centre ? yCentre : i < centre ? yCentre - offset * -(i - centre) : yCentre + offset * (i - centre)), (float) (width + 0.0625*2), (float) scale, -1);
		}
		GlStateManager.disableCull();
		GlStateManager.enableLighting();
	}

	public static void renderProgressBar(double width, double height, double scale, double d, double e) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

		double minX = -barOffset + 0.0625, minY = -barOffset + 0.0625 * 1, maxX = width + barOffset + 0.0625 * 1, maxY = height + barOffset + 0.0625 * 1;
		double barWidth = d * (maxX - minX) / e;
		double divide = Math.max((maxX - minX), (maxY - minY));
		double minU = 0, minV = 0, maxU = 1, maxV = 1;

		double widthnew = (minU + (barWidth * (maxU - minU) / 1));
		double heightnew = (minV + ((maxY - minY) * (maxV - minV) / 1));
		vertexbuffer.pos((double) (minX + 0), maxY, zLevel).tex((double) minU, heightnew).endVertex();
		vertexbuffer.pos((double) (minX + barWidth), maxY, zLevel).tex(widthnew, heightnew).endVertex();
		vertexbuffer.pos((double) (minX + barWidth), (double) (minY + 0), zLevel).tex(widthnew, (double) minV).endVertex();
		vertexbuffer.pos((double) (minX + 0), (double) (minY + 0), zLevel).tex((double) minU, (double) minV).endVertex();
		tessellator.draw();
	}

	public static void renderProgressBarWithSprite(TextureAtlasSprite sprite, double width, double height, double scale, double progress, double maxProgress) {
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);

		double minX = -barOffset + 0.0625, minY = -barOffset + 0.0625 * 1, maxX = width + barOffset + 0.0625 * 1, maxY = height + barOffset + 0.0625 * 1;
		double barWidth = ((double) progress * (maxX - minX)) / maxProgress;
		double divide = Math.max((maxX - minX), (maxY - minY));

		double widthnew = (sprite.getMinU() + (barWidth * (sprite.getMaxU() - sprite.getMinU()) / 1));
		double heightnew = (sprite.getMinV() + ((maxY - minY) * (sprite.getMaxV() - sprite.getMinV()) / 1));
		vertexbuffer.pos((double) (minX + 0), maxY, zLevel).tex((double) sprite.getMinU(), heightnew).endVertex();
		vertexbuffer.pos((double) (minX + barWidth), maxY, zLevel).tex(widthnew, heightnew).endVertex();
		vertexbuffer.pos((double) (minX + barWidth), (double) (minY + 0), zLevel).tex(widthnew, (double) sprite.getMinV()).endVertex();
		vertexbuffer.pos((double) (minX + 0), (double) (minY + 0), zLevel).tex((double) sprite.getMinU(), (double) sprite.getMinV()).endVertex();
		tessellator.draw();
	}

	public static final int[] rotate = new int[] { 0, 0, 0, 180, 270, 90 };
	public static final double[][] matrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 }, { 1, 0, -1 }, { 1, 0, 0 }, { 0, 0, -1 } };
	public static final double[][] downMatrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 1, 0 }, { 1, 0, 0 }, { 0, 0, 0 }, { 1, 1, 0 } };
	public static final double[][] upMatrix = new double[][] { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, -1 }, { 1, 1, -1 }, { 1, 0, -1 }, { 0, 1, -1 } };

	public static void rotateDisplayRendering(EnumFacing face, EnumFacing rotation) {
		double[] translate = matrix[face.ordinal()];
		GL11.glRotated(180, 0, 0, 1);
		switch (face) {
		case DOWN:
			GL11.glRotated(90, 1, 0, 0);
			int ordinal = rotation.ordinal();
			ordinal = ordinal == 4 ? 5 : ordinal == 5 ? 4 : ordinal;
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
	}

	public static int identifierLeft = (int) ((1.0 / 0.75) * 10);
	public static int objectLeft = (int) ((1.0 / 0.75) * (10 + 92));
	public static int kindLeft = (int) ((1.0 / 0.75) * (10 + 92 + 92));

	public static void renderMonitorInfoInGUI(IMonitorInfo info, int yPos, int colour) {
		if (info instanceof INameableInfo) {
			INameableInfo directInfo = (INameableInfo) info;
			if (!directInfo.isHeader() && directInfo.isValid()) {
				FontHelper.text(directInfo.getClientIdentifier(), identifierLeft, yPos, colour);
				FontHelper.text(directInfo.getClientObject(), objectLeft, yPos, colour);
				FontHelper.text(directInfo.getClientType(), kindLeft, yPos, colour);
			} else {
				if (directInfo instanceof LogicInfo) {
					String category = ((LogicInfo) directInfo).getRegistryType().name();
					FontHelper.text(category.substring(0, 1) + category.substring(1).toLowerCase(), identifierLeft + 4, yPos, colour);
				}
			}
		} else if (info instanceof MonitoredBlockCoords) {
			MonitoredBlockCoords directInfo = (MonitoredBlockCoords) info;
			FontHelper.text(directInfo.coords.toString(), identifierLeft, yPos, colour);
		}
	}
}
