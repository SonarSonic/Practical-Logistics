package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import sonar.core.energy.StoredEnergyStack;
import sonar.core.utils.BlockCoords;
import sonar.core.utils.helpers.FontHelper;
import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.api.Info;

public class StoredEnergyInfo extends Info<StoredEnergyInfo> {

	public StoredEnergyStack stack;
	public IdentifiedCoords coords;
	public String rend = "ITEMREND";

	public static StoredEnergyInfo createInfo(IdentifiedCoords coords, StoredEnergyStack stack) {
		StoredEnergyInfo info = new StoredEnergyInfo();
		info.stack = stack;
		info.coords = coords;
		return info;
	}

	@Override
	public String getName() {
		return "StoredEnergy";
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
		return stack != null ? stack.toString() : rend;
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
		stack = StoredEnergyStack.readFromBuf(buf);
		coords = IdentifiedCoords.readCoords(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		StoredEnergyStack.writeToBuf(buf, stack);
		IdentifiedCoords.writeCoords(buf, coords);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.stack = StoredEnergyStack.readFromNBT(tag);
		this.coords = IdentifiedCoords.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		StoredEnergyStack.writeToNBT(tag, stack);
		IdentifiedCoords.writeToNBT(tag, coords);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		/*
		 * if (stack != null && stack.item != null) {
		 * 
		 * GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2,
		 * 0.01f); FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		 * stack.item.stackSize = 1;
		 * 
		 * GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		 * 
		 * GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		 * GL11.glEnable(GL11.GL_CULL_FACE); tess.setColorOpaque_F(1.0f, 1.0f,
		 * 1.0f); double sizing = Math.round(Math.min((maxX - minX), (maxY -
		 * minY))); double itemScale = sizing >= 2 ? (2.5F + sizing - 1 * 1.0F)
		 * : scale >= 120 ? 0.8F : 1.4F; GL11.glTranslated(0.0,
		 * getXTranslate(scale, sizing), zOffset - 0.01);
		 * 
		 * GL11.glScaled(itemScale, itemScale, itemScale);
		 * GL11.glTranslatef(0.0f, 0.0f, +0.25f);
		 * RenderHelper.doRenderItem(stack.item, tile.getWorldObj(), false);
		 * GL11.glDisable(GL11.GL_CULL_FACE);
		 * GL11.glEnable(GL12.GL_RESCALE_NORMAL); GL11.glTranslatef(0.0f, 0.0f,
		 * -0.242f); GL11.glScalef(1.0f / 40.0f, 1.0f / 40.0f, 1.0f / 40.0f);
		 * 
		 * String s1 = FontHelper.formatStackSize(stack.stored);
		 * 
		 * final float scaleFactor = 0.5F; final float inverseScaleFactor = 1.0f
		 * / scaleFactor;
		 * 
		 * GL11.glScaled(scaleFactor, scaleFactor, scaleFactor);
		 * 
		 * final int X = (int) (((float) -8 + 15.0f - rend.getStringWidth(s1) *
		 * scaleFactor) * inverseScaleFactor); final int Y = (int) (((float) -12
		 * + 15.0f - 7.0f * scaleFactor) * inverseScaleFactor);
		 * 
		 * GL11.glDisable(GL11.GL_LIGHTING); rend.drawString(s1, X, Y,
		 * 16777215); }
		 */
		super.renderInfo(tess, tile, minX, minY, maxX, maxY, zOffset, scale);
	}

	public double getXTranslate(float scale, double sizing) {
		if (scale >= 120) {
			return -0.02F;
		}
		return (-0.08F + ((sizing - 1) * 0.17));

	}

	@Override
	public StoredEnergyInfo instance() {
		return new StoredEnergyInfo();
	}

	@Override
	public void writeUpdate(StoredEnergyInfo currentInfo, NBTTagCompound tag) {
		if (currentInfo.stack.hasInput != stack.hasInput) {
			stack.hasInput = currentInfo.stack.hasInput;
			tag.setBoolean("hI", stack.hasInput);
		}
		if (currentInfo.stack.hasOutput != stack.hasOutput) {
			stack.hasOutput = currentInfo.stack.hasOutput;
			tag.setBoolean("hO", stack.hasOutput);
		}
		if (currentInfo.stack.hasStorage != stack.hasStorage) {
			stack.hasStorage = currentInfo.stack.hasStorage;
			tag.setBoolean("hS", stack.hasStorage);
		}
		if (currentInfo.stack.hasUsage != stack.hasUsage) {
			stack.hasUsage = currentInfo.stack.hasUsage;
			tag.setBoolean("hU", stack.hasUsage);
		}
		if (currentInfo.stack.capacity != stack.capacity) {
			stack.capacity = currentInfo.stack.capacity;
			tag.setDouble("c", stack.capacity);
		}
		if (currentInfo.stack.input != stack.input) {
			stack.input = currentInfo.stack.input;
			tag.setDouble("i", stack.input);
		}
		if (currentInfo.stack.output != stack.output) {
			stack.output = currentInfo.stack.output;
			tag.setDouble("o", stack.output);
		}
		if (currentInfo.stack.stored != stack.stored) {
			stack.stored = currentInfo.stack.stored;
			tag.setDouble("s", stack.stored);
		}
		if (currentInfo.stack.usage != stack.usage) {
			stack.usage = currentInfo.stack.usage;
			tag.setDouble("u", stack.usage);
		}
		if (!currentInfo.coords.blockCoords.equals(coords.blockCoords)) {
			coords.blockCoords = currentInfo.coords.blockCoords;
			BlockCoords.writeToNBT(tag, coords.blockCoords);
		}
		if (!ItemStack.areItemStacksEqual(coords.block, currentInfo.coords.block)) {
			coords.block = currentInfo.coords.block;
			coords.block.writeToNBT(tag);
		}
		if (!currentInfo.coords.suffix.equals(coords.suffix)) {
			coords.suffix = currentInfo.coords.suffix;
			tag.setString("suffix", coords.suffix);
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("hI")) {
			stack.hasInput = tag.getBoolean("hI");
		}
		if (tag.hasKey("hO")) {
			stack.hasOutput = tag.getBoolean("hO");
		}
		if (tag.hasKey("hS")) {
			stack.hasStorage = tag.getBoolean("hS");
		}
		if (tag.hasKey("hU")) {
			stack.hasUsage = tag.getBoolean("hU");
		}
		if (tag.hasKey("c")) {
			stack.capacity = tag.getLong("c");
		}
		if (tag.hasKey("i")) {
			stack.input = tag.getLong("i");
		}
		if (tag.hasKey("o")) {
			stack.output = tag.getLong("o");
		}
		if (tag.hasKey("s")) {
			stack.stored = tag.getLong("s");
		}
		if (tag.hasKey("u")) {
			stack.usage = tag.getLong("u");
		}
		if (tag.hasKey("x")) {
			coords.blockCoords = BlockCoords.readFromNBT(tag);
		}
		if (tag.hasKey("id")) {
			coords.block = ItemStack.loadItemStackFromNBT(tag);
		}
		if (tag.hasKey("suffix")) {
			coords.suffix = tag.getString("suffix");
		}
	}

	@Override
	public boolean matches(StoredEnergyInfo currentInfo) {
		return currentInfo.stack.equals(stack) && currentInfo.coords.equals(coords);
	}

}
