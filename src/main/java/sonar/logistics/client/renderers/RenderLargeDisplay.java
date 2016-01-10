package sonar.logistics.client.renderers;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.client.models.ModelLargeDisplay;

public class RenderLargeDisplay extends RenderDisplayScreen {

	public ModelLargeDisplay model = new ModelLargeDisplay();
	public String texture = RenderHandlers.modelFolder + "large_display.png";

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		RenderHelper.finishRender();

		final Tessellator tess = Tessellator.instance;

		if (entity.getWorldObj() != null) {
			GL11.glPushMatrix();
			ForgeDirection dir = ForgeDirection.getOrientation(RenderHelper.setMetaData(entity));

			int br = 16 << 20 | 16 << 4;
			int var11 = br % 65536;
			int var12 = br / 65536;
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var11 * 0.8F, var12 * 0.8F);
			this.tesrRenderScreen(tess, entity, dir, false);

			GL11.glPopMatrix();
		}

	}
}
