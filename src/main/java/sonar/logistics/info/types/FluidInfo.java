package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.render.InfoRenderer;

public class FluidInfo extends StandardInfo {

	public int fluidID = -1;

	@Override
	public String getType() {
		return "Fluid-Info";
	}

	public FluidInfo() {
	}

	public FluidInfo(byte providerID, String category, String subCategory, Object data, String suffix, int fluidID) {
		super(providerID, category, subCategory, data, suffix);
		this.fluidID = fluidID;
	}

	public FluidInfo(byte providerID, String category, String subCategory, Object data, int fluidID) {
		super(providerID, category, subCategory, data);
		this.fluidID = fluidID;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		super.readFromBuf(buf);
		fluidID = buf.readInt();
		
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		super.writeToBuf(buf);
		buf.writeInt(fluidID);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.fluidID = tag.getInteger("fluidID");
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("fluidID", fluidID);
	}

	@Override
	public boolean hasSpecialRender() {
		return true;
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(-0.5, -0.2085, -0.205);
		float width = (1.0f - (0.0625f) * 2);
		float start = 0.0625f;
		float top = 0;
		float height = (float) (0.0625 * 6);
		Tessellator t = Tessellator.instance;
		if (fluidID != -1) {
			if (FluidRegistry.getFluid(fluidID) != null) {
				IIcon icon = FluidRegistry.getFluid(fluidID).getIcon();
				if (icon != null) {
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
					t.startDrawingQuads();

					double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU())));
					double heightnew = (icon.getMinV() + (height * (icon.getMaxV() - icon.getMinV())));

					t.addVertexWithUV((start + 0), (top + height), 0, (double) icon.getMinU(), heightnew);
					t.addVertexWithUV((start + width), (top + height), 0, widthnew, heightnew);
					t.addVertexWithUV((start + width), (top + 0), 0, widthnew, (double) icon.getMinV());
					t.addVertexWithUV((start + 0), (top + 0), 0, (double) icon.getMinU(), (double) icon.getMinV());

					t.draw();
				}
			}
		}
		GL11.glTranslated(+0.5, +0.2085, +0.205);
		// GL11.glTranslatef(0.0f, -0.04f, -0.20f);
		// GL11.glScalef(1.0f / 120.0f, 1.0f / 120.0f, 1.0f / 120.0f);
		// String data = getDisplayableData();
		// rend.drawString(data, -rend.getStringWidth(data) / 2, 0, -1);
		InfoRenderer.renderStandardInfo(this, rend);
	}

	@Override
	public Info newInfo() {
		return new FluidInfo();
	}
}
