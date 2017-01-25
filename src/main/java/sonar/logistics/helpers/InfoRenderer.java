package sonar.logistics.helpers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.helpers.SonarHelper;
import sonar.logistics.api.display.DisplayType;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.info.types.LogicInfo;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;

public class InfoRenderer {

	public static final double zLevel = 0, barOffset = 0.001;

	public static void renderNormalInfo(DisplayType type, String... toDisplay) {
		renderNormalInfo(type, type.width, type.height, type.scale, SonarHelper.convertArray(toDisplay));
	}

	public static void renderNormalInfo(DisplayType type, List<String> toDisplay) {
		renderNormalInfo(type, type.width, type.height, type.scale, toDisplay);
	}

	public static double getYCentre(DisplayType type, double height) {
		return ((0.12 * height) * (0.12 * height)) + (0.35 * height) - 0.58; // quadratic equation to solve the scale
	}

	public static void renderNormalInfo(DisplayType displayType, double width, double height, double scale, String... toDisplay) {
		renderNormalInfo(displayType, width, height, scale, SonarHelper.convertArray(toDisplay));
	}

	public static void renderNormalInfo(DisplayType displayType, double width, double height, double scale, List<String> toDisplay) {
		GlStateManager.disableLighting();
		GlStateManager.enableCull();
		float offset = (float) (12/(1/scale));
		double yCentre = 0;
		double centre =  (double)toDisplay.size() / 2  -0.5;
		int fontHeight = RenderHelper.fontRenderer.FONT_HEIGHT;
		//GlStateManager.translate(0, height/2 - scale/1, 0);
		for (int i = 0; i < toDisplay.size(); i++) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(0, (-1 + height / 2 + 0.23) + (i == centre ? 0 : i < centre ? yCentre - offset * -(i - centre) : yCentre + offset * (i - centre)), 0);
			GlStateManager.scale(scale, scale, 1.0f);
			String string = toDisplay.get(i);
			int length = RenderHelper.fontRenderer.getStringWidth(string);
			
			//renderCenteredString(toDisplay.get(i), -1, (float) (i == centre ? yCentre : i < centre ? yCentre - offset * -(i - centre) : yCentre + offset * (i - centre)), (float) (width + 0.0625 * 2), (float) scale, -1);

			RenderHelper.fontRenderer.drawString(string, (float) ((-1+0.0625 + width / 2) / scale - length / 2), (float) 0.625, -1, false);
			GlStateManager.popMatrix();
		}
		GlStateManager.disableCull();
		GlStateManager.enableLighting();

	}

	/** NEED TO WORK OUT A FORMULA! */
	public static double yCentreScale(double height) {
		/* int newHeight = (int) (height + 0.0625 * 2) - 1; switch (newHeight) { case 0: return -16; case 1: return 2.9375; case 2: return 9.4375; case 3: return 12.7375; case 4: return 14.6375; case 5: return 16.0375; case 6: return 16.9375; case 7: return 17.6375; case 8: return 18.1375; case 9: return 0; case 14: return 20; } return 0; */

		// y = -0.0023x^6 + 0.067x^5 - 0.774x^4 + 4.6276x^3 - 15.524x^2 + 30.49^5x - 15.993

		double y = -0.0023 * Math.pow(height, 6) + 0.067 * Math.pow(height, 5) - 0.774 * Math.pow(height, 4) + 4.6276 * Math.pow(height, 3) - 15.524 * Math.pow(height, 2) + 30.49 * (height) - 15.993;

		return y;
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

		double widthnew = (sprite.getMinU() + (barWidth * (sprite.getMaxU() - sprite.getMinU()) / (maxX - minX)));
		double heightnew = (sprite.getMinV() + ((maxY - minY) * (sprite.getMaxV() - sprite.getMinV()) / (maxX - minX)));
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
			FontHelper.text(directInfo.syncCoords.toString(), identifierLeft, yPos, colour);
		}
	}
}
