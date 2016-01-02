package sonar.logistics.client.renderers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import sonar.core.renderers.ItemModelRender;

public class RenderItemHandlers {

	public static class Reader extends ItemModelRender {

		public Reader(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			GL11.glScaled(1.5, 1.5, 1.5);
			GL11.glTranslated(0.0, 0.0, 0.0);
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	
	public static class Cable extends ItemModelRender {

		public Cable(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			GL11.glScaled(2, 2, 2);
			GL11.glTranslated(0.0, 0.0, 0.0);
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	
	public static class Node extends ItemModelRender {

		public Node(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			GL11.glScaled(1.4, 1.4, 1.4);
			GL11.glTranslated(0.0, 0.4, 0.0);
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	public static class EntityNode extends ItemModelRender {

		public EntityNode(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			GL11.glScaled(1.2, 1.2, 1.2);
			GL11.glTranslated(0.0, -0.2, 0.0);
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	public static class DataBlock extends ItemModelRender {

		public DataBlock(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			GL11.glScaled(1.2, 1.2, 1.2);
			GL11.glTranslated(0.0, 0.1, 0.0);
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	public static class DataModifier extends ItemModelRender {

		public DataModifier(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			GL11.glScaled(1.2, 1.2, 1.2);
			GL11.glTranslated(0.0, 0.1, 0.0);
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glTranslatef(-0.5F, -0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	public static class HolographicDisplay extends ItemModelRender {

		public HolographicDisplay(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glScaled(1.2, 1.2, 1.2);
				GL11.glTranslated(0.3, -1.2, 0.0);
				GL11.glTranslatef(-0.5F, 0.5F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
	public static class Hammer extends ItemModelRender {

		public Hammer(TileEntitySpecialRenderer render, TileEntity entity) {
			super(render, entity);
		}

		@Override
		public void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data) {
			if (type == IItemRenderer.ItemRenderType.INVENTORY) {
				GL11.glScaled(0.4, 0.4, 0.4);
				GL11.glTranslatef(-0.5F, -1.6F, 0.5F);
				GL11.glRotated(90, 0, 1, 0);
			}
			this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
		}
	}
}
