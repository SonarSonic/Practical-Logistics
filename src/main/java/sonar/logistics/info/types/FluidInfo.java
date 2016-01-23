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

import sonar.logistics.api.StandardInfo;

public class FluidInfo extends StandardInfo {

	public int fluidID = -1;

	@Override
	public String getName() {
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
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(0, 0, zOffset);
		float width = (maxX - minX);
		boolean renderNormal = true;
		if (fluidID != -1) {
			if (FluidRegistry.getFluid(fluidID) != null) {
				IIcon icon = FluidRegistry.getFluid(fluidID).getIcon();
				if (icon != null) {
					renderNormal = false;
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

					tess.startDrawingQuads();

					double divide = Math.max((0.5+minX +(maxX - minX)), (0.5+(maxY - minY)));
					double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU())/divide));
					double heightnew = (icon.getMinV() + (maxY * (icon.getMaxV() - icon.getMinV())/divide));
					
					tess.addVertexWithUV((minX + 0), (minY + maxY), 0, icon.getMinU(), heightnew);
					tess.addVertexWithUV((minX + width), (minY + maxY), 0, widthnew, heightnew);
					tess.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, icon.getMinV());
					tess.addVertexWithUV((minX + 0), (minY + 0), 0, icon.getMinU(), icon.getMinV());

					tess.draw();
				}
			}
		}
		GL11.glTranslated(0, 0, -zOffset);
		super.renderInfo(tess, tile, minX, minY, maxX, maxY, zOffset, scale);
	}

	@Override
	public FluidInfo instance() {
		return new FluidInfo();
	}
}
