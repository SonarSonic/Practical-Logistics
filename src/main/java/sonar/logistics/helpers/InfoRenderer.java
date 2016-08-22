package sonar.logistics.helpers;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.LogicInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.api.wrappers.RenderWrapper;
import sonar.logistics.monitoring.MonitoredBlockCoords;

public class InfoRenderer extends RenderWrapper {

	public void renderStandardInfo(LogicInfo info, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		FontRenderer rend = Minecraft.getMinecraft().fontRendererObj;
		GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, 0.01f);
		int sizing = Math.round(Math.min((maxX - minX), (maxY - minY) * 3));
		GL11.glTranslatef(0.0f, (float) (type.isNormalSize() ? -0.1F : -0.2F + ((sizing - 1) * -0.01)), zOffset);
		double itemScale = sizing >= 2 ? getScale(sizing) : 120;
		GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale);

		String category = info.getClientIdentifier();
		String data = info.getClientObject();
		if (category.isEmpty() || category.equals(" ")) {
			rend.drawString(data, -rend.getStringWidth(data) / 2, -4, -1);
		} else {
			rend.drawString(category, -rend.getStringWidth(category) / 2, -8, -1);
			rend.drawString(data, -rend.getStringWidth(data) / 2, 4, -1);
		}
		GL11.glScaled(itemScale, itemScale, itemScale);
	}

	/* public void renderCenteredString(String string, float minX, float minY, float maxX, float maxY, ScreenType type) { /* drawCenteredString(string, (int)minX, minY + (maxY - minY) / 2)); /* FontRenderer rend = Minecraft.getMinecraft().fontRendererObj; GL11.glTranslatef(minX + (maxX - minX) / 2, minY + ((maxY - minY) / 2), 0.01f); GL11.glTranslatef(0.0f, /* (float) (type.isNormalSize() ? -0.08F : -0.2F + ((sizing - 1) * 0.001)) 0.0f, 0); GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale); rend.drawString(string, -rend.getStringWidth(string) / 2, -Math.round(((maxY - minY) * 3)), -1);
	 * 
	 * 
	 * int sizing = Math.round(Math.min((maxX - minX), (maxY - minY) * 3)); double scale = sizing >= 2 ? getScale(sizing) : 120; GlStateManager.pushMatrix(); GlStateManager.scale(scale, scale, 1.0f); int titleLength = RenderHelper.fontRenderer.getStringWidth(string); int titleHeight = RenderHelper.fontRenderer.FONT_HEIGHT; RenderHelper.fontRenderer.drawString(string, Math.round((minX + (maxX - minX) / 2) / scale - titleLength / 2), Math.round((minY + ((maxY - minY) / 2)) / scale - titleHeight / 2), -1, false); GlStateManager.popMatrix(); } */
	public void renderCenteredString(String string, float x, float y, float width, float scale, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, 1.0f);
		int length = RenderHelper.fontRenderer.getStringWidth(string);
		int height = RenderHelper.fontRenderer.FONT_HEIGHT;
		RenderHelper.fontRenderer.drawString(string, Math.round((x + width / 2) / scale - length / 2), Math.round(y / scale - height / 2), color);
		GlStateManager.popMatrix();
	}

	public float getScaling(ScreenType type) {
		switch (type) {
		case LARGE:
			return 110F;
		case CONNECTED:
			return 40F;
		default:
			return 120F;
		}
	}

	public double getScale(int sizing) {
		switch (sizing) {
		case 0 & 1:
			return 120;
		case 2:
			return 70;
		case 3:
			return 40;
		case 4:
			return 30;
		case 5:
			return 22;
		default:
			return 18;
		}
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
					String category = ((LogicInfo)directInfo).getRegistryType().name();
					FontHelper.text(category.substring(0, 1) + category.substring(1).toLowerCase(), identifierLeft + 4, yPos, colour);
				}
			}
		} else if (info instanceof MonitoredBlockCoords) {
			MonitoredBlockCoords directInfo = (MonitoredBlockCoords) info;
			FontHelper.text(directInfo.coords.toString(), identifierLeft, yPos, colour);
		}
	}
}
