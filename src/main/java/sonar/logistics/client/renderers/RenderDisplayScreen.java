package sonar.logistics.client.renderers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
import sonar.logistics.api.connecting.IDataConnection;
import sonar.logistics.api.render.InfoRenderer;
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
		GL11.glTranslatef(0.0F, -0.3125F, -0.459F);
		this.modelSign.signStick.showModel = false;
		Minecraft.getMinecraft().getTextureManager().bindTexture(tex);
		GL11.glPushMatrix();
		GL11.glScalef(f1, -f1, -f1);
		this.modelSign.renderSign();

		RenderHelper.finishRender();

		// start of AE2 Rendering Code: all credit goes to them, I don't understand it

		final Tessellator tess = Tessellator.instance;

		if (entity.getWorldObj() != null) {
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
			this.tesrRenderScreen(tess, entity, ForgeDirection.getOrientation(RenderHelper.setMetaData(entity)), false);
			GL11.glPopMatrix();
		}
	}

	protected void tesrRenderScreen(Tessellator tess, TileEntity tile, ForgeDirection side, boolean normalSize) {
		if (tile == null || tile.getWorldObj() == null) {
			return;
		}
		int x = tile.xCoord, y = tile.yCoord, z = tile.zCoord;

		Object screen = FMPHelper.checkObject(tile);
		if (screen instanceof IDataConnection) {
			Info info = ((IDataConnection) screen).currentInfo();
			if (info == null) {
				return;
			}
			final ForgeDirection d = side;
			GL11.glTranslated(d.offsetX * 0.77, d.offsetY * 0.77, d.offsetZ * 0.77);

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
			GL11.glTranslated(0, 0, -(0.98));
			FontRenderer rend = this.func_147498_b();
			GL11.glDisable(GL11.GL_LIGHTING);
			if(info.hasSpecialRender()){
			info.renderInfo(tess, tile);
			}else{
				InfoRenderer.renderStandardInfo(info, rend);
			}
			/*
			if (info.getSubCategory().equals("ITEMREND") && info.getCategory().equals("ITEMREND") && screen instanceof IItemRenderer) {
				ItemStack stack = ((IItemRenderer) screen).getRenderStack();
				if (stack != null) {
					stack.stackSize = 1;

					final int br = 16 << 20 | 16 << 4;
					final int var11 = br % 65536;
					final int var12 = br / 65536;

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

					GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
					GL11.glScaled(0.8, 0.8, 0.8);
					GL11.glTranslatef(0.0f, 0.06f, 0f);
					RenderHelper.doRenderItem(stack, tile.getWorldObj(), normalSize);
					GL11.glEnable(GL12.GL_RESCALE_NORMAL);

					GL11.glTranslatef(0.0f, 0.0f, -0.24f);
					GL11.glScalef(1.0f / 40.0f, 1.0f / 40.0f, 1.0f / 40.0f);

					String s1 = FontHelper.formatStackSize(Long.parseLong(info.getDisplayableData()));

					final float scaleFactor = 0.5F;
					final float inverseScaleFactor = 1.0f / scaleFactor;
					GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
					final int X = (int) (((float) -8 + 15.0f - rend.getStringWidth(s1) * scaleFactor) * inverseScaleFactor);
					final int Y = (int) (((float) -12 + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);

					GL11.glDisable(GL11.GL_LIGHTING);
					rend.drawString(s1, X, Y, 16777215);
				}
			} else if (info.getProvider().equals("PERCENT") && info.getCategory().equals("PERCENT")) {

				GL11.glTranslated(-0.5, -0.2085, -0.205);
				float width = Integer.parseInt(info.getSubCategory()) * (1.0f - (0.0625f) * 2) / Integer.parseInt(info.getDisplayableData());
				float start = 0.0625f;
				float top = 0;
				float height = (float) (0.0625 * 6);
				Tessellator t = Tessellator.instance;
				if (info.getDataType() == 3) {
					IIcon icon = FluidRegistry.getFluid(2).getIcon();

					this.bindTexture(TextureMap.locationBlocksTexture);
					t.startDrawingQuads();

					double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU())));
					double heightnew = (icon.getMinV() + (height * (icon.getMaxV() - icon.getMinV())));

					t.addVertexWithUV((start + 0), (top + height), 0, (double) icon.getMinU(), heightnew);
					t.addVertexWithUV((start + width), (top + height), 0, widthnew, heightnew);
					t.addVertexWithUV((start + width), (top + 0), 0, widthnew, (double) icon.getMinV());
					t.addVertexWithUV((start + 0), (top + 0), 0, (double) icon.getMinU(), (double) icon.getMinV());

					t.draw();
				} else {
					this.bindTexture(progress);
					t.startDrawingQuads();
					t.addVertexWithUV(start, 0, 0, 0, 0); // Bottom left texture
					t.addVertexWithUV(start, height, 0, 0, height); // Top left
					t.addVertexWithUV(start + width, height, 0, width, height); // Top right
					t.addVertexWithUV(start + width, 0, 0, width, 0); // Bottom right
					t.draw();
				}

				GL11.glTranslated(+0.5, +0.2085, +0.205);
				GL11.glTranslatef(0.0f, -0.04f, -0.20f);
				GL11.glScalef(1.0f / 120.0f, 1.0f / 120.0f, 1.0f / 120.0f);
				String data = info.getSubCategory();
				rend.drawString(data, -rend.getStringWidth(data) / 2, 0, -1);

			} else {
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
			*/
			GL11.glEnable(GL11.GL_LIGHTING);
			
		}

	}

}