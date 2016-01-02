package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;
import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.render.InfoRenderer;
import thaumcraft.api.aspects.Aspect;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ThaumcraftAspectInfo extends StandardInfo {

	public String tex;

	@Override
	public String getType() {
		return "Aspect-Info";
	}

	public ThaumcraftAspectInfo() {
	}

	public ThaumcraftAspectInfo(byte providerID, String category, String subCategory, Object data, String suffix, String tex) {
		super(providerID, category, subCategory, data, suffix);
		this.tex = tex;
	}

	public ThaumcraftAspectInfo(byte providerID, String category, String subCategory, Object data, String tex) {
		super(providerID, category, subCategory, data);
		this.tex = tex;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		super.readFromBuf(buf);
		tex = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		super.writeToBuf(buf);
		ByteBufUtils.writeUTF8String(buf, tex);
	}
	
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		this.tex = tag.getString("tex");
	}

	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setString("tex", tex.toString());
	}

	@Override
	public boolean hasSpecialRender() {
		return true;
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(-0.5, -0.2085, -0.205);
		float width = 1.0F;
		float start = 0f;
		float top = 0f;
		float height = 1.0F;
		Tessellator t = Tessellator.instance;
		double scale = 0.3;

		GL11.glScaled(scale, scale, scale);
		if (tex != null) {
			GL11.glTranslated(0.5, 0.1, 0.0);
			Aspect aspect = Aspect.getAspect(tex);
			if (aspect != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(aspect.getImage());
				GL11.glBlendFunc(770, aspect.getBlend());
				t.startDrawingQuads();
				t.setColorRGBA_I(aspect.getColor(), 128);
				t.addVertexWithUV(start, 0, 0, 0, 0);
				t.addVertexWithUV(start, height, 0, 0, height);
				t.addVertexWithUV(start + width, height, 0, width, height);
				t.addVertexWithUV(start + width, 0, 0, width, 0);
				t.draw();
			}
			GL11.glTranslated(0.0, -0.1, 0.0);
		}
		GL11.glScaled(1.0 / scale, 1.0 / scale, 1.0 / scale);
		GL11.glTranslated(+0.5, +0.2085, +0.205);
		InfoRenderer.renderStandardInfo(this, rend);
	}
	@Override
	public Info newInfo() {
		return new ThaumcraftAspectInfo();
	}
}
