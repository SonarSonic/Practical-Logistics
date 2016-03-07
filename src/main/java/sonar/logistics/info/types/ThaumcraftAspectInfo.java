package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.info.StandardInfo;
import sonar.logistics.api.render.ScreenType;
import thaumcraft.api.aspects.Aspect;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ThaumcraftAspectInfo extends StandardInfo<ThaumcraftAspectInfo> {

	public String tex;

	public ThaumcraftAspectInfo() {
	}

	public ThaumcraftAspectInfo(int providerID, String category, String subCategory, Object data, String tex) {
		super(providerID, category, subCategory, data);
		this.tex = tex;
	}

	@Override
	public String getName() {
		return "Aspect-Info";
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
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;

		GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, 0.01f);
		GL11.glTranslated(0, 0, zOffset);
		float width = 1;
		float height = 1;
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
		GL11.glTranslated(0, 0, -zOffset);
		LogisticsAPI.getInfoRenderer().renderStandardInfo(this, rend, minX, minY, maxX, maxY, zOffset, type);
	}

	@Override
	public ThaumcraftAspectInfo instance() {
		return new ThaumcraftAspectInfo();
	}

	@Override
	public boolean matches(ThaumcraftAspectInfo currentInfo) {
		return tex.equals(currentInfo.tex) && currentInfo.getProviderID() == this.providerID && currentInfo.dataType == dataType && currentInfo.category.equals(category) && currentInfo.subCategory.equals(subCategory) && currentInfo.suffix.equals(suffix) && currentInfo.catID == catID && currentInfo.subCatID == subCatID;
	}
}
