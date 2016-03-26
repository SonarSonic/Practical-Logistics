package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sonar.core.inventory.StoredItemStack;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;

public class StoredStackInfo extends ILogicInfo<StoredStackInfo> {

	public StoredItemStack stack;
	public String rend = "ITEMREND";
	public int cacheID = -1;

	public static StoredStackInfo createInfo(StoredItemStack stack, int cacheID) {
		StoredStackInfo info = new StoredStackInfo();
		info.stack = stack;
		info.cacheID = cacheID;
		return info;
	}

	@Override
	public String getName() {
		return "StoredStack";
	}

	@Override
	public int getProviderID() {
		return -1;
	}

	@Override
	public String getCategory() {
		return rend;
	}

	@Override
	public String getSubCategory() {
		return stack != null ? stack.item.getDisplayName() : rend;
	}

	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public String getData() {
		return (stack != null ? String.valueOf(stack.stored) : String.valueOf(0));
	}

	@Override
	public int getDataType() {
		return 0;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		stack = StoredItemStack.readFromBuf(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		StoredItemStack.writeToBuf(buf, stack);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.stack = StoredItemStack.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		StoredItemStack.writeToNBT(tag, stack);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		if (stack != null && stack.item != null) {

			GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, 0.01f);
			FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
			stack.item.stackSize = 1;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_CULL_FACE);
			tess.setColorOpaque_F(1.0f, 1.0f, 1.0f);
			double sizing = Math.round(Math.min((maxX - minX), (maxY - minY)));
			double itemScale = sizing >= 2 ? (2.5F + sizing - 1 * 1.0F) : type.isNormalSize() ? 0.8F : 1.4F;
			GL11.glTranslated(0.0, getXTranslate(type.getScaling(), sizing), zOffset - 0.01);

			GL11.glScaled(itemScale, itemScale, itemScale);
			GL11.glTranslatef(0.0f, 0.0f, +0.25f);
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
		}
	}

	public double getXTranslate(float scale, double sizing) {
		if (scale >= 120) {
			return -0.02F;
		}
		return (-0.08F + ((sizing - 1) * 0.17));

	}

	/*
	 * @Override public boolean isEqualType(Info info) { if (info instanceof
	 * StoredStackInfo) { StoredStackInfo stackInfo = (StoredStackInfo) info; if
	 * (stackInfo.stack.stored != this.stack.stored) { return false; } if
	 * (!stackInfo.stack.equalStack(stack.item)) { return false; } return true;
	 * } return false; }
	 * 
	 * @Override public void emptyData() {
	 * 
	 * }
	 */
	@Override
	public StoredStackInfo instance() {
		return new StoredStackInfo();
	}

	@Override
	public void writeUpdate(StoredStackInfo currentInfo, NBTTagCompound tag) {
		if (!currentInfo.stack.equalStack(stack.item)) {
			NBTTagCompound writeTag = new NBTTagCompound();
			currentInfo.writeToNBT(writeTag);
			tag.setTag("wT", writeTag);
			this.stack = currentInfo.stack;
		} else {
			if (currentInfo.stack.stored != stack.stored) {
				tag.setLong("s", currentInfo.stack.stored);
				stack.stored = currentInfo.stack.stored;
			}
		}

	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("wT")) {
			this.readFromNBT(tag.getCompoundTag("wT"));
		} else {
			if (tag.hasKey("s")) {
				stack.stored = tag.getLong("s");
			}
		}

	}

	@Override
	public boolean matches(StoredStackInfo currentInfo) {
		return currentInfo.stack.equals(stack);
	}

}
