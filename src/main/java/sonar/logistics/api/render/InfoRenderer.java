package sonar.logistics.api.render;

import net.minecraft.client.gui.FontRenderer;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;

public class InfoRenderer {

	public static void renderStandardInfo(Info info, FontRenderer rend){
		GL11.glTranslatef(0.0f, -0.02f, -0.20f);
		GL11.glScalef(1.0f / 120.0f, 1.0f / 120.0f, 1.0f / 120.0f);
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
