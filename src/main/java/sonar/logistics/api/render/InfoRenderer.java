package sonar.logistics.api.render;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;

public class InfoRenderer {

	public static void renderStandardInfo(Info info, FontRenderer rend, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		GL11.glTranslatef(minX + (maxX - minX) / 2, minY+(maxY-minY) / 2, 0.01f);
		int sizing = Math.round(Math.min((maxX - minX), (maxY-minY)*3));
		 GL11.glTranslatef(0.0f, (float) (scale >= 120 ? -0.1F : -0.2F + ((sizing-1)*-0.01)), zOffset);
		double itemScale = sizing>=2 ? getScale(sizing) : 120;
		GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale);
		String category = info.getSubCategory();
		String data = info.getDisplayableData();
		if (category.isEmpty() || category.equals(" ")) {
			rend.drawString(data, -rend.getStringWidth(data) / 2, -4, -1);
		} else {
			rend.drawString(category, -rend.getStringWidth(category) / 2, -8, -1);
			rend.drawString(data, -rend.getStringWidth(data) / 2, 4, -1);
		}
	}

	public static double getScale(int sizing) {
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
}
