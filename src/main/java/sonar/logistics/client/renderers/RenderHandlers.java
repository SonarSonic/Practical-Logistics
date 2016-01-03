package sonar.logistics.client.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import sonar.core.integration.fmp.FMPHelper;
import sonar.core.renderers.SonarTERender;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.Logistics;
import sonar.logistics.client.models.ModelBlockNode;
import sonar.logistics.client.models.ModelDataCable;
import sonar.logistics.client.models.ModelDataModifier;
import sonar.logistics.client.models.ModelDataReceiver;
import sonar.logistics.client.models.ModelDirectionalConnector;
import sonar.logistics.client.models.ModelEntityNode;
import sonar.logistics.client.models.ModelHammer;
import sonar.logistics.client.models.ModelInfoCreator;
import sonar.logistics.client.models.ModelRedstoneSignaller;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.registries.BlockRegistry;

public class RenderHandlers {

	public static String modelFolder = Logistics.modid + ":textures/model/";

	public static class BlockNode extends SonarTERender {
		public ModelDataCable modelCable = new ModelDataCable();
		public String textureCable = modelFolder + "dataCable.png";

		public BlockNode() {
			super(new ModelBlockNode(), modelFolder + "blockNode_black.png");
		}

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, 0, textureCable);
			modelCable.renderTile(entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();

			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			if (entity != null && entity.getWorldObj() != null) {
				int meta = entity.getBlockMetadata();
				if (meta == 5) {
					GL11.glRotated(90, 0, 1.0, 0);
				} else if (meta == 6) {
				} else if (meta == 4) {
					GL11.glTranslated(-1, 1, 0);
					GL11.glRotated(90, 0, 0, -1);
				} else if (meta == 3) {
					GL11.glTranslated(0, 1, -1);
					GL11.glRotated(90, 1, 0, 0);
				} else if (meta == 2) {
					GL11.glTranslated(1, 1, 0);
					GL11.glRotated(90, 0, 0, 1);
				} else if (meta == 1) {
					GL11.glTranslated(1, 1, 0);
					GL11.glRotated(90, 0, 0, 1);
				} else if (meta == 0) {
					GL11.glTranslated(0, 2, 0);
					GL11.glRotated(180, 0, 0, 1);
				} else if (meta == 7) {
					GL11.glTranslated(0, 2, 0);
					GL11.glRotated(180, 0, 0, 1);
				}

			}
			GL11.glScaled(0.5, 0.5, 0.5);
			GL11.glTranslated(0, 1.5, 0);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class BlockCable extends TileEntitySpecialRenderer {
		public ModelDataCable model = new ModelDataCable();
		public String texture = modelFolder + "dataCable.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			model.renderTile(entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class DataModifier extends TileEntitySpecialRenderer {
		public ModelDataCable modelCable = new ModelDataCable();
		public String cableTex = modelFolder + "dataCable.png";

		public ModelDataModifier model = new ModelDataModifier();
		public String modelTex = modelFolder + "dataModifier.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, 0, cableTex);
			modelCable.renderTile(entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();

			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), modelTex);
			GL11.glTranslated(0, 0.0625, 0);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class InfoCreator extends TileEntitySpecialRenderer {
		public ModelDataCable modelCable = new ModelDataCable();
		public String cableTex = modelFolder + "dataCable.png";

		public ModelInfoCreator model = new ModelInfoCreator();
		public String modelTex = modelFolder + "infoCreator.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, 0, cableTex);
			modelCable.renderTile(entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();

			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), modelTex);
			GL11.glTranslated(0, 0.0625, 0);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	/* public static class DataModifier extends TileEntitySpecialRenderer { public ModelDataCable model = new ModelDataCable(); public String texture = modelFolder + "dataRenamer.png";
	 * @Override public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) { RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture); model.renderTile(entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F); RenderHelper.finishRender(); } } */
	public static class InfoNode extends TileEntitySpecialRenderer {
		public ModelDirectionalConnector model = new ModelDirectionalConnector();
		public String texture = modelFolder + "infoNode.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			if (entity != null && entity.getWorldObj() != null) {
				int meta = FMPHelper.getMeta(entity);
				if (meta == 0) {
					GL11.glTranslated(0, 1, -1.0625);
					GL11.glRotated(90, 1, 0, 0);
					
				} else if (meta == 1) {
					GL11.glRotated(90, -1, 0, 0);
					GL11.glTranslated(0, -1.0625, 1);
				}else{
					GL11.glTranslated(0, -0.0625, 0);
				}

			}
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class InventoryReader extends TileEntitySpecialRenderer {
		public ModelDirectionalConnector model = new ModelDirectionalConnector();
		public String texture = modelFolder + "inventoryReader.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			if (entity != null && entity.getWorldObj() != null) {
				int meta = FMPHelper.getMeta(entity);
				if (meta == 0) {
					GL11.glTranslated(0, 1, -1.0625);
					GL11.glRotated(90, 1, 0, 0);
					
				} else if (meta == 1) {
					GL11.glRotated(90, -1, 0, 0);
					GL11.glTranslated(0, -1.0625, 1);
				}else{
					GL11.glTranslated(0, -0.0625, 0);
				}

			}
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class DataReceiver extends TileEntitySpecialRenderer {
		public ModelDataReceiver model = new ModelDataReceiver();
		public String texture = modelFolder + "blockReceiver.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			GL11.glTranslated(0, -0.0625, 0);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class DataEmitter extends TileEntitySpecialRenderer {
		public ModelDataReceiver model = new ModelDataReceiver();
		public String texture = modelFolder + "blockEmitter.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			GL11.glTranslated(0, -0.0625, 0);
			GL11.glRotated(180, 0, 1, 0);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class RedstoneSignaller extends TileEntitySpecialRenderer {
		public ModelRedstoneSignaller model = new ModelRedstoneSignaller();
		public String onTex = modelFolder + "redstoneEmitter_on.png";
		public String offTex = modelFolder + "redstoneEmitter_off.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			boolean on = false;
			if (entity != null && entity.getWorldObj() != null) {
				Block block = entity.getBlockType();
				if (block == BlockRegistry.redstoneSignaller_on) {
					on = true;
				}
			}
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), on ? onTex : offTex);
			GL11.glTranslated(0, -0.0625, 0);
			GL11.glRotated(180, 0, 1, 0);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class Hammer extends TileEntitySpecialRenderer {
		public ModelHammer model = new ModelHammer();
		public String texture = modelFolder + "hammer.png";
		public String textureNew = modelFolder + "hammer_machine.png";
		public ResourceLocation rope = new ResourceLocation(modelFolder + "rope.png");

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), textureNew);
			int progress = 0;
			boolean cooling = false;
			if (entity != null && entity.getWorldObj() != null) {
				TileEntityHammer hammer = (TileEntityHammer) entity;
				if(hammer.coolDown.getInt()!=0){
					progress = hammer.coolDown.getInt();					
					cooling=true;
				}else
					progress = hammer.progress.getInt();
				double move = !cooling? progress * 1.625 / hammer.speed : progress * 1.625 / 200;
				model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, true, move);
			} else {
				model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, false, 0);
			}
			RenderHelper.finishRender();
			if (entity.getWorldObj() != null) {
				GL11.glTranslated(0, 2.75, 0);
				double height = -(!cooling? progress * 1.625 / 100 : progress * 1.625 / 200);
				float width = 0.53F;
				Tessellator tessellator = Tessellator.instance;
				this.bindTexture(rope);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0F);
				GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0F);
				GL11.glTranslated(0.0, 0.70, 0.0);
				float f2 = 20;
				float f4 = -f2 * 0.2F - (float) MathHelper.floor_float(-f2 * 0.1F);
				byte b0 = 1;
				double d3 = (double) f2 * 0.025D * (1.0D - (double) (b0 & 1) * 2.5D);
				GL11.glTranslated(0.0, -0.70, 0.0);
				OpenGlHelper.glBlendFunc(770, 771, 1, 0);
				GL11.glDepthMask(true);
				tessellator.startDrawingQuads();

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
				tessellator.addVertexWithUV(x + width, y + d18, z + width, d22, d26);
				tessellator.addVertexWithUV(x + width, y, z + width, d22, d24);
				tessellator.addVertexWithUV(x + remain, y, z + width, d20, d24);
				tessellator.addVertexWithUV(x + remain, y + d18, z + width, d20, d26);
				tessellator.addVertexWithUV(x + width, y + d18, z + remain, d22, d26);
				tessellator.addVertexWithUV(x + width, y, z + remain, d22, d24);
				tessellator.addVertexWithUV(x + width, y, z + width, d20, d24);
				tessellator.addVertexWithUV(x + width, y + d18, z + width, d20, d26);
				tessellator.addVertexWithUV(x + remain, y + d18, z + width, d22, d26);
				tessellator.addVertexWithUV(x + remain, y, z + width, d22, d24);
				tessellator.addVertexWithUV(x + remain, y, z + remain, d20, d24);
				tessellator.addVertexWithUV(x + remain, y + d18, z + remain, d20, d26);

				tessellator.draw();
				GL11.glDepthMask(true);
				GL11.glTranslated(0, -2.75, 0);
			}

			int[] sides = new int[6];
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);

			if (entity != null && entity.getWorldObj() != null && entity instanceof TileEntityHammer) {
				TileEntityHammer hammer = (TileEntityHammer) entity;
				ItemStack target = null;
				if ((progress == 0 || cooling) && hammer.getStackInSlot(1) != null) {
					target = hammer.getStackInSlot(1);
				} else if(!cooling){
					target = hammer.getStackInSlot(0);
				}

				if (target != null) {
					if (!(target.getItem() instanceof ItemBlock)) {		

						GL11.glRotated(90, 1, 0, 0);

						GL11.glTranslated(0.0625 * 8, 0.3, -0.885);

					} else {
						GL11.glRotated(90, -1, 0, 0);
						//GL11.glTranslated(0.0625 * 8, 0.0625 * 13, 0.4);
						GL11.glTranslated(0.5, -0.7, 0.92);
						if (!cooling && progress > 81) {
							GL11.glTranslated(0, 0, -((progress - 81) * 0.085 / (hammer.speed - 81)));
							GL11.glScaled(1, 1, 1 - ((progress - 81) * 0.85 / (hammer.speed - 81)));
						}
					}
					RenderHelper.renderItem(entity.getWorldObj(), target);
				}
			}
			GL11.glPopMatrix();
		}
	}
	public static class EntityNode extends TileEntitySpecialRenderer {
		public ModelEntityNode model = new ModelEntityNode();
		public String texture = modelFolder + "entity_node.png";
		public ModelDataCable modelCable = new ModelDataCable();
		public String textureCable = modelFolder + "dataCable.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, 0, textureCable);
			modelCable.renderTile(entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();

			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			model.renderTile((TileEntityEntityNode) entity,(Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}
	
}
