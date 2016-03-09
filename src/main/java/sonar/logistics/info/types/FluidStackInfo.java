package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;

import org.lwjgl.opengl.GL11;

import sonar.core.fluid.StoredFluidStack;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.ScreenType;
import cpw.mods.fml.common.network.ByteBufUtils;

public class FluidStackInfo extends ILogicInfo<FluidStackInfo> {

	public StoredFluidStack stack;
	public String rend = "FLUIDSTACK";

	public static FluidStackInfo createInfo(StoredFluidStack stack) {
		FluidStackInfo info = new FluidStackInfo();
		info.stack = stack;
		return info;
	}

	@Override
	public String getName() {
		return "FluidStack";
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
		return stack != null ? stack.fluid.getFluid().getLocalizedName(stack.fluid) : rend;
	}

	@Override
	public String getDisplayableData() {

		return getData() + " mB";
	}

	@Override
	public String getData() {
		return (stack != null && stack.capacity != 0 ? String.valueOf(stack.stored) : String.valueOf(0));
	}

	@Override
	public int getDataType() {
		return 0;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.stack = StoredFluidStack.readFromBuf(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		NBTTagCompound tag = new NBTTagCompound();
		StoredFluidStack.writeToBuf(buf, stack);
		ByteBufUtils.writeTag(buf, tag);
	}

	public void readFromNBT(NBTTagCompound tag) {
		this.stack = StoredFluidStack.readFromNBT(tag);
	}

	public void writeToNBT(NBTTagCompound tag) {
		StoredFluidStack.writeToNBT(tag, stack);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(0, 0, zOffset);
		float width = stack.stored * (maxX - minX) / stack.capacity;
		IIcon icon = stack.fluid.getFluid().getIcon();
		if (icon != null) {
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

			tess.startDrawingQuads();
			double divide = Math.max((0.5 + (maxX - minX)), (0.5 + (maxY - minY)));
			double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU()) / divide));
			double heightnew = (icon.getMinV() + ((maxY - minY) * (icon.getMaxV() - icon.getMinV()) / divide));

			double height = (maxY) / 2;
			tess.addVertexWithUV((minX + 0), height, 0, icon.getMinU(), heightnew);
			tess.addVertexWithUV((minX + width), height, 0, widthnew, heightnew);
			tess.addVertexWithUV((minX + width), minY, 0, widthnew, icon.getMinV());
			tess.addVertexWithUV((minX + 0), minY, 0, icon.getMinU(), icon.getMinV());
			tess.draw();

		}

		GL11.glTranslated(0, 0, -zOffset);
		super.renderInfo(tess, tile, minX, minY, maxX, maxY, zOffset, type);
	}

	@Override
	public FluidStackInfo instance() {
		return new FluidStackInfo();
	}

	@Override
	public void writeUpdate(FluidStackInfo currentInfo, NBTTagCompound tag) {
		if (!currentInfo.stack.equalStack(stack.fluid)) {
			NBTTagCompound writeTag = new NBTTagCompound();
			currentInfo.writeToNBT(writeTag);
			tag.setTag("wT", writeTag);
			this.stack = currentInfo.stack;
		} else {
			if (currentInfo.stack.stored != stack.stored) {
				stack.stored = currentInfo.stack.stored;
				tag.setLong("s", stack.stored);
			}
			if (currentInfo.stack.capacity != stack.capacity) {
				tag.setLong("c", currentInfo.stack.capacity);
				stack.capacity = currentInfo.stack.capacity;
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
			if (tag.hasKey("c")) {
				stack.capacity = tag.getLong("c");
			}
		}
	}

	@Override
	public boolean matches(FluidStackInfo currentInfo) {
		return currentInfo.stack.equals(stack);
	}

}
