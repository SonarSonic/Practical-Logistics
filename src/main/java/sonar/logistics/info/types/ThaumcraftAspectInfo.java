package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.StandardInfo;
import sonar.logistics.api.render.InfoRenderer;
import thaumcraft.api.aspects.Aspect;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ThaumcraftAspectInfo extends StandardInfo {

	public String tex;

	@Override
	public String getName() {
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
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;

		GL11.glTranslated(0, 0, zOffset);
		float width = 1;
		float height = 1;
		double scaled = scale/400;
		GL11.glScaled(scaled, scaled, scaled);
		if (tex != null) {
			GL11.glTranslated(-0.7, -0.55, 0.0);
			Aspect aspect = Aspect.getAspect(tex);
			if (aspect != null) {
				Minecraft.getMinecraft().renderEngine.bindTexture(aspect.getImage());
				GL11.glBlendFunc(770, aspect.getBlend());
				tess.startDrawingQuads();
				tess.setColorRGBA_I(aspect.getColor(), 128);
				tess.addVertexWithUV(minX, 0, 0, 0, 0);
				tess.addVertexWithUV(minX, height, 0, 0, height);
				tess.addVertexWithUV(minX + width, height, 0, width, height);
				tess.addVertexWithUV(minX + width, 0, 0, width, 0);
				tess.draw();
			}
			GL11.glTranslated(0.7, 0.55, 0.0);
		}
		GL11.glTranslated(0.5, 0.00, 0.0);
		GL11.glScaled(1.0 / scaled, 1.0 / scaled, 1.0 / scaled);
		GL11.glTranslated(0, 0, -zOffset);
		InfoRenderer.renderStandardInfo(this, rend, minX, minY, maxX, maxY, zOffset, scale);
	}
	@Override
	public ThaumcraftAspectInfo instance() {
		return new ThaumcraftAspectInfo();
	}
}
