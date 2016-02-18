package sonar.logistics.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import sonar.core.integration.SonarAPI;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IInfoTile;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.integration.multipart.DisplayScreenPart;
import codechicken.multipart.TileMultipart;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDisplayScreen extends TileEntitySpecialRenderer {
	private static final ResourceLocation tex = new ResourceLocation(RenderHandlers.modelFolder + "displayScreen.png");
	private final ModelSign modelSign = new ModelSign();
	private Integer dspList;
	private boolean updateList;

	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float var) {

		GL11.glPushMatrix();

		float f1 = 0.6666667F;
		float f3;

		int j = entity.getBlockMetadata();

		if (SonarAPI.forgeMultipartLoaded() && entity != null && entity.getWorldObj() != null && entity instanceof TileMultipart) {
			DisplayScreenPart screen = (DisplayScreenPart) ((TileMultipart) entity).jPartList().get(0);
			j = screen.meta;
		}

		f3 = 0.0F;

		if (j == 2) {
			f3 = 180.0F;
		}

		if (j == 4) {
			f3 = 90.0F;
		}

		if (j == 5) {
			f3 = -90.0F;
		}

		GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F * f1, (float) z + 0.5F);
		GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(0.0F, -0.3325F, -0.459F);
		this.modelSign.signStick.showModel = false;
		GL11.glPushMatrix();
		GL11.glScalef(f1, -f1, -f1);
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		GL11.glEnable(GL11.GL_LIGHTING);
		this.modelSign.renderSign();

		RenderHelper.finishRender();

		final Tessellator tess = Tessellator.instance;
		
		if (entity.getWorldObj() != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
			ForgeDirection d = ForgeDirection.getOrientation(RenderHelper.setMetaData(entity));
			float move = -0.21F;
			GL11.glTranslated(d.offsetX * move, d.offsetY * move, d.offsetZ * move);

			this.tesrRenderScreen(tess, entity, d);
			GL11.glPopMatrix();
		}
	}

	protected void tesrRenderScreen(Tessellator tess, TileEntity tile, ForgeDirection side) {
		if (tile == null || tile.getWorldObj() == null) {
			return;
		}
		int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;

		Object screen = FMPHelper.checkObject(tile);
		if (screen instanceof IInfoTile) {
			Info info = ((IInfoTile) screen).currentInfo();
			if (info == null) {
				return;
			}
			final ForgeDirection d = side;

			switch (d) {
			case UP:
				GL11.glScalef(1.0f, -1.0f, 1.0f);
				GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(90.0F, 0, 0, 1);
				break;
			case DOWN:
				GL11.glScalef(1.0f, -1.0f, 1.0f);
				GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(-90.0F, 0, 0, 1);
				break;
			case EAST:
				GL11.glScalef(-1.0f, -1.0f, -1.0f);
				GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
				break;
			case WEST:
				GL11.glScalef(-1.0f, -1.0f, -1.0f);
				GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
				break;
			case NORTH:
				GL11.glScalef(-1.0f, -1.0f, -1.0f);
				break;
			case SOUTH:
				GL11.glScalef(-1.0f, -1.0f, -1.0f);
				GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				break;

			default:
				break;
			}
			GL11.glDisable(GL11.GL_LIGHTING);
			renderInfo(tess, tile, d, info);
			GL11.glEnable(GL11.GL_LIGHTING);

		}

	}

	public void renderInfo(Tessellator tess, TileEntity tile, ForgeDirection side, Info info) {
		float pixel = 1.0F / 16F;
		//GL11.glTranslatef(0.0F, -0.03F, 0F);
		info.renderInfo(tess, tile, -0.5F + pixel-0.001f, -pixel*3-0.001f, (1.0f - (pixel) * 9)+0.001f, (pixel * 5)+0.061f, -0.207F, ScreenType.NORMAL);

	}

}