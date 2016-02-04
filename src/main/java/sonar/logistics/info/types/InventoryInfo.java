package sonar.logistics.info.types;

import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.Info;
import sonar.logistics.common.tileentity.TileEntityInventoryReader;

public class InventoryInfo extends Info {

	// public StoredItemStack stack;
	public BlockCoords reader;
	public String rend = "ITEMINV";

	public static InventoryInfo createInfo(BlockCoords reader) {
		InventoryInfo info = new InventoryInfo();
		info.reader = reader;
		return info;
	}

	@Override
	public String getName() {
		return "InventoryInfo";
	}

	@Override
	public byte getProviderID() {
		return -1;
	}

	@Override
	public String getCategory() {
		return rend;
	}

	@Override
	public String getSubCategory() {
		return rend;
	}

	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public String getData() {
		return "Inventory";
	}

	@Override
	public int getDataType() {
		return 1;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		reader = BlockCoords.readFromBuf(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		BlockCoords.writeToBuf(buf, reader);
	}

	public void readFromNBT(NBTTagCompound tag) {
		reader = BlockCoords.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		BlockCoords.writeToNBT(tag, reader);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		if (reader != null) {
			TileEntity target = reader.getTileEntity();
			if (target != null && target instanceof TileEntityInventoryReader) {
				int xSlots = Math.round(maxX - minX) * 2;
				int ySlots = Math.round(maxY - minY) * 2;
				if (scale == 120) {
					xSlots = 2;
					ySlots = 1;
				}

				int currentSlot = 0;

				if (((TileEntityInventoryReader) target).handler.stacks != null) {
					List<StoredItemStack> currentStacks = (List<StoredItemStack>) ((ArrayList<StoredItemStack>) ((TileEntityInventoryReader) target).handler.stacks).clone();

					GL11.glTranslatef(minX + 0.18f, minY + 0.18f, 0.01f);

					GL11.glTranslatef(0.0f, 0.0f, +0.19f);
					GL11.glTranslated(0.0, 0.07F, zOffset - 0.01);
					float spacing = 0.665f;
					GL11.glScaled(0.75, 0.75, 0.75);

					for (StoredItemStack stack : currentStacks) {
						if (currentSlot < (xSlots * ySlots)) {
							int xLevel = (int) (currentSlot - ((Math.floor((currentSlot / xSlots))) * xSlots));
							int yLevel = (int) (Math.floor((currentSlot / xSlots)));

							FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
							stack.item.stackSize = 1;

							GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

							GL11.glDisable(GL12.GL_RESCALE_NORMAL);
							GL11.glEnable(GL11.GL_CULL_FACE);
							tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
							double sizing = Math.round(Math.min((maxX - minX), (maxY - minY)));
							double itemScale = sizing >= 2 ? (2.5F + sizing - 1 * 1.0F) : scale >= 120 ? 0.8F : 1.4F;

							GL11.glTranslatef(xLevel * spacing, yLevel * spacing, 0);
							RenderHelper.doRenderItem(stack.item, tile.getWorldObj(), false);
							GL11.glDisable(GL11.GL_CULL_FACE);
							GL11.glEnable(GL12.GL_RESCALE_NORMAL);
							GL11.glTranslatef(0.0f, 0.0f, -0.242f);

							GL11.glScalef(1.0f / 40.0f, 1.0f / 40.0f, 1.0f / 40.0f);
							String s1 = FontHelper.formatStackSize(stack.stored);
							final float scaleFactor = 0.5F;
							final float inverseScaleFactor = 1.0f / scaleFactor;
							GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);

							final int X = (int) (((float) -8 + 15.0f - rend.getStringWidth(s1) * scaleFactor) * inverseScaleFactor);
							final int Y = (int) (((float) -12 + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);

							GL11.glDisable(GL11.GL_LIGHTING);
							rend.drawString(s1, X, Y, 16777215);
							GL11.glScaled(inverseScaleFactor, inverseScaleFactor, inverseScaleFactor);
							GL11.glScaled(40.0f, 40.0f, 40.0f);
							GL11.glTranslatef(0.0f, 0.0f, 0.242f);
							GL11.glTranslatef(-xLevel * spacing, -yLevel * spacing, 0);
							currentSlot++;
						}
					}
				}
			}
		}
	}

	public double getXTranslate(float scale, double sizing) {
		if (scale >= 120) {
			return 0.07F;
		}
		return (0.13F + ((sizing - 1) * 0.17));

	}

	@Override
	public boolean isEqualType(Info info) {
		if (info instanceof InventoryInfo) {
			InventoryInfo stackInfo = (InventoryInfo) info;
			if (BlockCoords.equalCoords(stackInfo.reader, reader)) {
				return false;
			}
			return true;
		}
		return false;
	}

	@Override
	public void emptyData() {

	}

	@Override
	public InventoryInfo instance() {
		return new InventoryInfo();
	}

}
