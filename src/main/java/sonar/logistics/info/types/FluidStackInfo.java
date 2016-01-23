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
import sonar.logistics.api.Info;
import cpw.mods.fml.common.network.ByteBufUtils;

public class FluidStackInfo extends Info {

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
	public byte getProviderID() {
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
		return getData() + " mb";
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
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(0, 0, zOffset);
		float width = stack.stored * (maxX - minX) / stack.capacity;
		boolean renderNormal = true;
		IIcon icon = stack.fluid.getFluid().getIcon();
		if (icon != null) {
			renderNormal = false;
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

			tess.startDrawingQuads();

			double divide = Math.max((0.5 + (maxX - minX)), (0.5 + (maxY - minY)));
			double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU()) / divide));
			double heightnew = (icon.getMinV() + (maxY * (icon.getMaxV() - icon.getMinV()) / divide));

			tess.addVertexWithUV((minX + 0), (minY + maxY), 0, icon.getMinU(), heightnew);
			tess.addVertexWithUV((minX + width), (minY + maxY), 0, widthnew, heightnew);
			tess.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, icon.getMinV());
			tess.addVertexWithUV((minX + 0), (minY + 0), 0, icon.getMinU(), icon.getMinV());
			tess.draw();

		}

		GL11.glTranslated(0, 0, -zOffset);
		super.renderInfo(tess, tile, minX, minY, maxX, maxY, zOffset, scale);
	}

	@Override
	public boolean isEqualType(Info info) {
		if (info instanceof FluidStackInfo) {
			FluidStackInfo stackInfo = (FluidStackInfo) info;
			if (stackInfo.stack.stored != this.stack.stored) {
				return false;
			}
			if (stackInfo.stack.capacity != this.stack.capacity) {
				return false;
			}
			if (!stackInfo.stack.equalStack(stack.fluid)) {
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
	public FluidStackInfo instance() {
		return new FluidStackInfo();
	}

}
