package sonar.logistics.client.renderers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import sonar.core.integration.fmp.FMPHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.render.InfoRenderer;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.client.models.ModelLargeDisplay;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.registries.BlockRegistry;

public class RenderLargeDisplay extends RenderDisplayScreen {

	public ModelLargeDisplay model = new ModelLargeDisplay();
	public String none = RenderHandlers.modelFolder + "screen-parts/" + "large_display_none.png";
	public String all = RenderHandlers.modelFolder + "screen-parts/" + "large_display_all.png";
	public String one = RenderHandlers.modelFolder + "screen-parts/" + "large_display_one.png";
	public String opposite = RenderHandlers.modelFolder + "screen-parts/" + "large_display_opposite.png";
	public String two = RenderHandlers.modelFolder + "screen-parts/" + "large_display_two.png";
	public String three = RenderHandlers.modelFolder + "screen-parts/" + "large_display_three.png";

	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
		List<ForgeDirection> connections = connectedDir(entity);
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), this.getTexture(connections));
		float rotate = getRotation(connections, RenderHelper.setMetaData(entity));

		if (rotate == 90.0F) {
			GL11.glTranslated(1, 1, 0);
		}
		if (rotate == -90.0F) {
			GL11.glTranslated(-1, 1, 0);
		}
		if (rotate == 180.0F) {
			GL11.glTranslated(0, 2, 0);
		}
		GL11.glRotated(rotate, 0, 0, 1);

		model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		// RenderHelper.finishRender();

		GL11.glPopMatrix();
		GL11.glPopMatrix();
		final Tessellator tess = Tessellator.instance;

		if (entity.getWorldObj() != null) {
			Object target = FMPHelper.checkObject(entity);
			if (!(target instanceof ILargeDisplay)) {
				target = FMPHelper.getHandler(target);
			}

			if (target instanceof ILargeDisplay) {
				ILargeDisplay display = (ILargeDisplay) target;
				if (display.isHandler()) {
					GL11.glPushMatrix();
					GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
					ForgeDirection d = ForgeDirection.getOrientation(RenderHelper.setMetaData(entity));
					float move = -0.23F;
					GL11.glTranslated(d.offsetX * move, d.offsetY * move, d.offsetZ * move);
					this.tesrRenderScreen(tess, entity, d);
					GL11.glPopMatrix();
				}
			}

		}

	}

	public float getRotation(List<ForgeDirection> dirs, int meta) {
		if (dirs == null || dirs.isEmpty() || dirs.size() == 0) {
			return 0.0F;
		}
		// 2 north, 3, south, 4, west, 5 east
		ForgeDirection metaDir = ForgeDirection.getOrientation(meta);
		switch (dirs.size()) {
		case 1:

			if (dirs.get(0) == metaDir.getRotation(ForgeDirection.UP)) {
				return 90.0F;
			}
			if (dirs.get(0) == metaDir.getRotation(ForgeDirection.UP).getOpposite()) {
				return -90.0F;
			}
			if (dirs.get(0) == ForgeDirection.DOWN) {
				return 0.0F;
			}
			if (dirs.get(0) == ForgeDirection.UP) {
				return 180F;
			}

			return 90.0F;
		case 2:
			if (dirs.get(0).getOpposite() == dirs.get(1)) {
				if (dirs.get(0) == ForgeDirection.DOWN && dirs.get(1) == ForgeDirection.UP) {
					return 90.0F;
				} else {
					return 0.0F;
				}
			} else {
				if (dirs.get(0) == ForgeDirection.DOWN) {
					if (dirs.get(1) == metaDir.getRotation(ForgeDirection.UP)) {
						return 180.0F;
					}
					if (dirs.get(1) == metaDir.getRotation(ForgeDirection.UP).getOpposite()) {
						return 90.0F;
					}
				}
				if (dirs.get(0) == ForgeDirection.UP) {
					if (dirs.get(1) == metaDir.getRotation(ForgeDirection.UP)) {
						return -90.0F;
					}
					if (dirs.get(1) == metaDir.getRotation(ForgeDirection.UP).getOpposite()) {
						return 0.0F;
					}
				}
				return 0.0F;
			}
		case 3:
			if (!dirs.contains(ForgeDirection.DOWN)) {
				return 0.0F;
			}
			if (!dirs.contains(ForgeDirection.UP)) {
				return 180.0F;
			}
			if (!dirs.contains(metaDir.getRotation(ForgeDirection.UP))) {
				return 90.0F;
			}
			if (!dirs.contains(metaDir.getRotation(ForgeDirection.UP).getOpposite())) {
				return -90.0F;
			}
			return 0.0F;
		case 4:
			return 0.0F;

		}
		return 0.0F;
	}

	public String getTexture(List<ForgeDirection> dirs) {
		if (dirs == null || dirs.isEmpty() || dirs.size() == 0) {
			return none;
		}
		switch (dirs.size()) {
		case 1:
			return one;
		case 2:
			if (dirs.get(0).getOpposite() == dirs.get(1)) {
				return opposite;
			} else {
				return two;
			}
		case 3:
			return three;
		case 4:
			return all;

		}
		return none;
	}

	public List<ForgeDirection> connectedDir(TileEntity tile) {
		List<ForgeDirection> dirs = new ArrayList();
		if (tile != null && tile.getWorldObj() != null) {
			int meta = tile.getBlockMetadata();
			for (int i = 0; i < 6; i++) {
				ForgeDirection dir = ForgeDirection.getOrientation(i);
				if (dir != ForgeDirection.getOrientation(meta) && dir != ForgeDirection.getOrientation(meta).getOpposite()) {
					Block block = tile.getWorldObj().getBlock(tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ);
					int blockMeta = tile.getWorldObj().getBlockMetadata(tile.xCoord + dir.offsetX, tile.yCoord + dir.offsetY, tile.zCoord + dir.offsetZ);
					if (block != null) {
						if (block == BlockRegistry.largeDisplayScreen && meta == blockMeta) {
							dirs.add(dir);
						}
					}
				}
			}
		}
		return dirs;

	}

	public void renderInfo(Tessellator tess, TileEntity tile, ForgeDirection side, Info info) {
		LargeScreenSizing sizing = null;
		Object target = FMPHelper.checkObject(tile);
		if (!(target instanceof ILargeDisplay)) {
			target = FMPHelper.getHandler(target);
		}

		if (target instanceof LargeDisplayScreenHandler) {
			LargeDisplayScreenHandler display = (LargeDisplayScreenHandler) target;
			sizing = display.sizing;
		}
		//System.out.print(sizing);
		float pixel = 1.0F / 16F;
		if (info.hasSpecialRender()) {
			info.renderInfo(tess, tile, -0.5F + pixel, -0.4400F, (1.0f - (pixel) * 9), (pixel * 14), -0.207F, 100F);
		} else {
			FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
			InfoRenderer.renderStandardInfo(info, rend, -0.5F + pixel, -0.2085F, (1.0f - (pixel) * 9), (pixel * 6), -0.207F, 120F);
		}
	}
}
