package sonar.logistics.client.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
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
import sonar.logistics.client.models.ModelClock;
import sonar.logistics.client.models.ModelDataCable;
import sonar.logistics.client.models.ModelDataModifier;
import sonar.logistics.client.models.ModelDataReceiver;
import sonar.logistics.client.models.ModelDirectionalConnector;
import sonar.logistics.client.models.ModelEntityNode;
import sonar.logistics.client.models.ModelHammer;
import sonar.logistics.client.models.ModelInfoCreator;
import sonar.logistics.client.models.ModelItemRouter;
import sonar.logistics.client.models.ModelRedstoneSignaller;
import sonar.logistics.common.tileentity.TileEntityClock;
import sonar.logistics.common.tileentity.TileEntityEntityNode;
import sonar.logistics.common.tileentity.TileEntityHammer;
import sonar.logistics.common.tileentity.TileEntityItemRouter;
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

	public static class BlockMultiCable extends TileEntitySpecialRenderer {
		public ModelDataCable model = new ModelDataCable();
		public String texture = modelFolder + "dataMultiCable.png";

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

	public static class InfoReader extends TileEntitySpecialRenderer {
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
				} else {
					GL11.glTranslated(0, -0.0625, 0);
				}

			}
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class InventoryReader extends InfoReader {
		public InventoryReader() {
			super.texture = modelFolder + "inventoryReader.png";

		}
	}

	public static class FluidReader extends InfoReader {
		public FluidReader() {
			super.texture = modelFolder + "fluidReader.png";
		}
	}
	
	public static class EnergyReader extends InfoReader {
		public EnergyReader() {
			super.texture = modelFolder + "energyReader.png";
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
				if (hammer.coolDown.getInt() != 0) {
					progress = hammer.coolDown.getInt();
					cooling = true;
				} else
					progress = hammer.progress.getInt();
				double move = !cooling ? progress * 1.625 / hammer.speed : progress * 1.625 / 200;
				model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, true, move);
			} else {
				model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, false, 0);
			}
			RenderHelper.finishRender();
			if (entity.getWorldObj() != null) {
				GL11.glTranslated(0, 2.75, 0);
				double height = -(!cooling ? progress * 1.625 / 100 : progress * 1.625 / 200);
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
				} else if (!cooling) {
					target = hammer.getStackInSlot(0);
				}

				if (target != null) {
					if (!(target.getItem() instanceof ItemBlock)) {

						GL11.glRotated(90, 1, 0, 0);

						GL11.glTranslated(0.0625 * 8, 0.3, -0.885);

					} else {
						GL11.glRotated(90, -1, 0, 0);
						// GL11.glTranslated(0.0625 * 8, 0.0625 * 13, 0.4);
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
			model.renderTile((TileEntityEntityNode) entity, (Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			RenderHelper.finishRender();
		}
	}

	public static class ItemRouter extends TileEntitySpecialRenderer {
		public ModelItemRouter model = new ModelItemRouter();
		public String texture = modelFolder + "itemRouter.png";

		public ResourceLocation none = new ResourceLocation(modelFolder + "router_none.png");
		public ResourceLocation input = new ResourceLocation(modelFolder + "router_input.png");
		public ResourceLocation output = new ResourceLocation(modelFolder + "router_output.png");

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, 0, texture);
			model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
			if (entity != null && entity instanceof TileEntityItemRouter) {
				GL11.glTranslated(-0.5, 0.5, -0.5);
				TileEntityItemRouter router = (TileEntityItemRouter) entity;
				int[] sides = new int[6];

				if (entity.getWorldObj() != null) {
					for (int i = 0; i < 6; i++) {
						sides[i] = router.handler.sideConfigs[i].getInt();
					}

				}
				Tessellator tes = Tessellator.instance;
				// bottom
				tes.startDrawingQuads();

				Minecraft.getMinecraft().getTextureManager().bindTexture(sides[1] == 0 ? none : sides[1] == 1 ? input : output);
				tes.addVertexWithUV(0, 0.0002, 0, 0, 0);
				tes.addVertexWithUV(1, 0.0002, 0, 0, 1);
				tes.addVertexWithUV(1, 0.0002, 1, 1, 1);
				tes.addVertexWithUV(0, 0.0002, 1, 1, 0);

				tes.addVertexWithUV(0, 0.1250, 0, 0, 0);
				tes.addVertexWithUV(0, 0.1250, 1, 1, 0);
				tes.addVertexWithUV(1, 0.1250, 1, 1, 1);
				tes.addVertexWithUV(1, 0.1250, 0, 0, 1);
				tes.draw();

				// top
				tes.startDrawingQuads();
				Minecraft.getMinecraft().getTextureManager().bindTexture(sides[0] == 0 ? none : sides[0] == 1 ? input : output);
				tes.addVertexWithUV(0, 0.875, 0, 0, 0);
				tes.addVertexWithUV(1, 0.875, 0, 0, 1);
				tes.addVertexWithUV(1, 0.875, 1, 1, 1);
				tes.addVertexWithUV(0, 0.875, 1, 1, 0);

				tes.addVertexWithUV(0, 0.9998, 0, 0, 0);
				tes.addVertexWithUV(0, 0.9998, 1, 1, 0);
				tes.addVertexWithUV(1, 0.9998, 1, 1, 1);
				tes.addVertexWithUV(1, 0.9998, 0, 0, 1);
				tes.draw();

				tes.startDrawingQuads();
				Minecraft.getMinecraft().getTextureManager().bindTexture(sides[3] == 0 ? none : sides[3] == 1 ? input : output);
				tes.addVertexWithUV(0, 0, 0.0002, 0, 0);
				tes.addVertexWithUV(0, 1, 0.0002, 0, 1);
				tes.addVertexWithUV(1, 1, 0.0002, 1, 1);
				tes.addVertexWithUV(1, 0, 0.0002, 1, 0);

				tes.addVertexWithUV(0, 1, 0.1250, 0, 1);
				tes.addVertexWithUV(0, 0, 0.1250, 0, 0);
				tes.addVertexWithUV(1, 0, 0.1250, 1, 0);
				tes.addVertexWithUV(1, 1, 0.1250, 1, 1);
				tes.draw();

				tes.startDrawingQuads();
				Minecraft.getMinecraft().getTextureManager().bindTexture(sides[2] == 0 ? none : sides[2] == 1 ? input : output);
				tes.addVertexWithUV(0, 0, 0.875, 0, 0);
				tes.addVertexWithUV(0, 1, 0.875, 0, 1);
				tes.addVertexWithUV(1, 1, 0.875, 1, 1);
				tes.addVertexWithUV(1, 0, 0.875, 1, 0);

				tes.addVertexWithUV(0, 1, 0.9998, 0, 1);
				tes.addVertexWithUV(0, 0, 0.9998, 0, 0);
				tes.addVertexWithUV(1, 0, 0.9998, 1, 0);
				tes.addVertexWithUV(1, 1, 0.9998, 1, 1);
				tes.draw();

				tes.startDrawingQuads();
				Minecraft.getMinecraft().getTextureManager().bindTexture(sides[4] == 0 ? none : sides[4] == 1 ? input : output);
				tes.addVertexWithUV(0.0002, 0, 0, 1, 1);
				tes.addVertexWithUV(0.0002, 0, 1, 1, 0);
				tes.addVertexWithUV(0.0002, 1, 1, 0, 0);
				tes.addVertexWithUV(0.0002, 1, 0, 0, 1);

				tes.addVertexWithUV(0.1250, 0, 1, 1, 0);
				tes.addVertexWithUV(0.1250, 0, 0, 1, 1);
				tes.addVertexWithUV(0.1250, 1, 0, 0, 1);
				tes.addVertexWithUV(0.1250, 1, 1, 0, 0);
				tes.draw();

				tes.startDrawingQuads();
				Minecraft.getMinecraft().getTextureManager().bindTexture(sides[5] == 0 ? none : sides[5] == 1 ? input : output);
				tes.addVertexWithUV(0.875, 0, 0, 1, 1);
				tes.addVertexWithUV(0.875, 0, 1, 1, 0);
				tes.addVertexWithUV(0.875, 1, 1, 0, 0);
				tes.addVertexWithUV(0.875, 1, 0, 0, 1);

				tes.addVertexWithUV(0.9998, 0, 1, 1, 0);
				tes.addVertexWithUV(0.9998, 0, 0, 1, 1);
				tes.addVertexWithUV(0.9998, 1, 0, 0, 1);
				tes.addVertexWithUV(0.9998, 1, 1, 0, 0);
				tes.draw();

			}
			RenderHelper.finishRender();
		}
	}

	public static class ChannelSelector extends DataModifier {

		public ChannelSelector() {
			super.modelTex = modelFolder + "channelSelector.png";
			super.cableTex = modelFolder + "dataMultiCable.png";
		}

	}

	public static class Clock extends TileEntitySpecialRenderer {
		public ModelClock model = new ModelClock();
		public String texture = modelFolder + "clock.png";

		@Override
		public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
			RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
			float rotation = 0;
			if (entity.getWorldObj() != null) {
				TileEntityClock clock = (TileEntityClock) entity;
				rotation = clock.rotation;
			}
			model.render((TileEntity) entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F, rotation);
			RenderHelper.finishRender();
		}
	}

}
