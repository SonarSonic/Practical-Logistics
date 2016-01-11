package sonar.logistics.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.render.InfoRenderer;
import sonar.logistics.client.models.ModelHolographicDisplay;

public class RenderHolographicDisplay extends RenderDisplayScreen {

	public ModelHolographicDisplay model = new ModelHolographicDisplay();
	public String texture = RenderHandlers.modelFolder + "holographicDisplay.png";
	private static final ResourceLocation hologram = new ResourceLocation(RenderHandlers.modelFolder + "hologram.png");

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);

		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		if (entity.getWorldObj() != null) {
			GL11.glTranslated(-x, -y, -z);
			GL11.glTranslated(-0.5, 0.18, 0.4);
			float width = 0.96f;
			float height = 0.7F;
			Tessellator tessellator = Tessellator.instance;
			this.bindTexture(hologram);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
			GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glDepthMask(true);
			OpenGlHelper.glBlendFunc(770, 1, 1, 0);
			GL11.glTranslated(0.0, 0.70, 0.0);
			float f2 = 20;
			float f4 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
			byte b0 = 1;
			double d3 = (double) f2 * 0.025D * (1.0D - (double) (b0 & 1) * 2.5D);
			GL11.glTranslated(0.0, -0.70, 0.0);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glDepthMask(false);
			int br = 16 << 20 | 16 << 4;
			int var11 = br % 65536;
			int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			tessellator.startDrawingQuads();
			tessellator.setColorRGBA(255, 255, 255, 32);

			double remain = 1 - width;
			double offset = 0.2D - 1 / 4;
			double d18 = height;
			double d20 = 0.0D;
			double d22 = 1.0D;
			double d24 = (double) (-1.0F + f4);
			double d26 = d18 + d24;
			tessellator.addVertexWithUV(x + remain, y + d18, z + remain, d22, d26);
			tessellator.addVertexWithUV(x + remain, y, z + remain, d22, d24);

			tessellator.addVertexWithUV(x + width, y, z + remain, d20, d24);
			tessellator.addVertexWithUV(x + width, y + d18, z + remain, d20, d26);
			/* tessellator.addVertexWithUV(x + width, y + d18, z + width, d22, d26); tessellator.addVertexWithUV(x + width, y, z + width, d22, d24); tessellator.addVertexWithUV(x + remain, y, z + width, d20, d24); tessellator.addVertexWithUV(x + remain, y + d18, z + width, d20, d26); tessellator.addVertexWithUV(x + width, y + d18, z + remain, d22, d26); tessellator.addVertexWithUV(x + width, y, z
			 * + remain, d22, d24); tessellator.addVertexWithUV(x + width, y, z + width, d20, d24); tessellator.addVertexWithUV(x + width, y + d18, z + width, d20, d26); tessellator.addVertexWithUV(x + remain, y + d18, z + width, d22, d26); tessellator.addVertexWithUV(x + remain, y, z + width, d22, d24); tessellator.addVertexWithUV(x + remain, y, z + remain, d20, d24);
			 * tessellator.addVertexWithUV(x + remain, y + d18, z + remain, d20, d26); */

			tessellator.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glDepthMask(true);

		}

		GL11.glPopMatrix();
		GL11.glPopMatrix();

		final Tessellator tess = Tessellator.instance;

		if (entity.getWorldObj() != null) {
			GL11.glPushMatrix();
			ForgeDirection dir = ForgeDirection.getOrientation(RenderHelper.setMetaData(entity));

			GL11.glTranslated(x + 0.5, y + 1.0, z + 0.5);
			GL11.glTranslated(-0.0625 * 2 * dir.offsetX, 0, -0.0625 * 2 * dir.offsetZ);
			GL11.glScaled(1.5, 1.5, 1.5);
			int br = 16 << 20 | 16 << 4;
			int var11 = br % 65536;
			int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			this.tesrRenderScreen(tess, entity, dir);

			GL11.glPopMatrix();
		}

	}

	public void renderInfo(Tessellator tess, TileEntity tile, ForgeDirection side, Info info) {
		float pixel = 1.0F / 16F;
		if (info.hasSpecialRender()) {
			info.renderInfo(tess, tile, -pixel*4.5F, -0.1850F, (1.0f - (pixel) * 11.5F), (pixel * 6), -0.207F, 140F);
		} else {
			FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
			InfoRenderer.renderStandardInfo(info, rend, -pixel*4.5F, -0.1850F, (1.0f - (pixel) * 11.5F), (pixel * 6), -0.207F, 140F);
		}
	}
}
