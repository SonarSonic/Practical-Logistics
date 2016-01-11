package sonar.logistics.api.render;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;

public class InfoRenderer {

	public static void renderStandardInfo(Info info, FontRenderer rend, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		GL11.glTranslatef(minX + (maxX - minX) / 2, minY + maxY/2, 0.01f);
		GL11.glTranslatef(0, 0, zOffset);		
		GL11.glScalef(1.0f / scale, 1.0f / scale, 1.0f / scale);
		String category = info.getSubCategory();
		String data = info.getDisplayableData();
		if (category.isEmpty() || category.equals(" ")) {
			rend.drawString(data, -rend.getStringWidth(data) / 2, -4, -1);
		} else {
			rend.drawString(category, -rend.getStringWidth(category) / 2, -8, -1);
			rend.drawString(data, -rend.getStringWidth(data) / 2, 4, -1);
		}
	}
}
