package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;

import org.lwjgl.opengl.GL11;

import sonar.logistics.api.Info;
import sonar.logistics.client.renderers.RenderHandlers;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ProgressInfo extends Info {

	private static final ResourceLocation progress = new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png");
	public String data;
	public long stored, max;
	public int fluidID = -1;
	public String rend = "Progress";
	
	public ProgressInfo() {
	}
	public ProgressInfo(long stored, long max, String data) {
		this.stored = stored;
		this.max = max;
		this.data = data;
	}

	public ProgressInfo(long stored, long max, String data, int fluidID) {
		this.stored = stored;
		this.max = max;
		this.data = data;
		this.fluidID = fluidID;
	}

	@Override
	public String getType() {
		return "ProgressBar";
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
	public String getData() {
		return data;
	}

	@Override
	public String getDisplayableData() {
		return getData();
	}

	@Override
	public int getDataType() {
		return 1;
	}
	@Override
	public void readFromBuf(ByteBuf buf) {
		this.stored = buf.readLong();
		this.max = buf.readLong();
		this.fluidID = buf.readInt();
		this.data = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeLong(stored);
		buf.writeLong(max);
		buf.writeInt(fluidID);
		ByteBufUtils.writeUTF8String(buf, data);
		
	}
	public void readFromNBT(NBTTagCompound tag) {
		this.stored = tag.getLong("stored");
		this.max = tag.getLong("max");
		this.fluidID = tag.getInteger("fluidID");
		this.data = tag.getString("data");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("stored", stored);
		tag.setLong("max", max);
		tag.setInteger("fluidID", fluidID);
		tag.setString("data", data);
	}

	@Override
	public boolean hasSpecialRender() {
		return true;
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(-0.5, -0.2085, -0.205);
		float width = stored * (1.0f - (0.0625f) * 2) / max;
		float start = 0.0625f;
		float top = 0;
		float height = (float) (0.0625 * 6);
		
		boolean renderNormal = true;
		if (fluidID != -1) {
			if (FluidRegistry.getFluid(fluidID) != null) {
				IIcon icon = FluidRegistry.getFluid(fluidID).getIcon();
				if (icon != null) {
					renderNormal=false;
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
					tess.startDrawingQuads();

					double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU())));
					double heightnew = (icon.getMinV() + (height * (icon.getMaxV() - icon.getMinV())));

					tess.addVertexWithUV((start + 0), (top + height), 0, (double) icon.getMinU(), heightnew);
					tess.addVertexWithUV((start + width), (top + height), 0, widthnew, heightnew);
					tess.addVertexWithUV((start + width), (top + 0), 0, widthnew, (double) icon.getMinV());
					tess.addVertexWithUV((start + 0), (top + 0), 0, (double) icon.getMinU(), (double) icon.getMinV());

					tess.draw();
				}
			}
		}
		if (renderNormal) {
			Minecraft.getMinecraft().renderEngine.bindTexture(progress);
			tess.startDrawingQuads();
			tess.addVertexWithUV(start, 0, 0, 0, 0);
			tess.addVertexWithUV(start, height, 0, 0, height);
			tess.addVertexWithUV(start + width, height, 0, width, height);
			tess.addVertexWithUV(start + width, 0, 0, width, 0);
			tess.draw();
		}		
		GL11.glTranslated(+0.5, +0.2085, +0.205);
		GL11.glTranslatef(0.0f, -0.04f, -0.20f);
		GL11.glScalef(1.0f / 120.0f, 1.0f / 120.0f, 1.0f / 120.0f);
		String data = getDisplayableData();
		rend.drawString(data, -rend.getStringWidth(data) / 2, 0, -1);
	}

	@Override
	public boolean isEqualType(Info info) {
		return false;
	}

	@Override
	public void emptyData() {

	}



	@Override
	public Info newInfo() {
		return new ProgressInfo();
	}

}
