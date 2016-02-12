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

import sonar.core.utils.INBTObject;
import sonar.logistics.api.Info;
import sonar.logistics.api.render.InfoRenderer;
import sonar.logistics.client.renderers.RenderHandlers;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ProgressInfo extends Info<ProgressInfo> {

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
	public String getName() {
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
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		FontRenderer rend = Minecraft.getMinecraft().fontRenderer;
		GL11.glTranslated(0, 0, zOffset);
		float width = stored * (maxX - minX) / max;
		float height = stored * (maxY - minY) / max;
		boolean renderNormal = true;
		/*
		if (fluidID != -1) {
			if (FluidRegistry.getFluid(fluidID) != null) {
				IIcon icon = FluidRegistry.getFluid(fluidID).getIcon();
				renderNormal = false;

				if (icon != null) {
					renderNormal = false;
					Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
					tess.startDrawingQuads();

					double divide = Math.max((0.5 + (maxX - minX)), (0.5 + (maxY - minY)));
					double widthnew = (icon.getMinU() + (width * (icon.getMaxU() - icon.getMinU()) / divide));
					double heightnew = (icon.getMinV() + (maxY * (icon.getMaxV() - icon.getMinV()) / divide));
					tess.addVertexWithUV((minX + 0), maxY / 2, 0, icon.getMinU(), heightnew);
					tess.addVertexWithUV((minX + width), maxY / 2, 0, widthnew, heightnew);
					tess.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, icon.getMinV());
					tess.addVertexWithUV((minX + 0), (minY + 0), 0, icon.getMinU(), icon.getMinV());
					tess.draw();

				}

			}
		}
		*/
		if (renderNormal) {
			Minecraft.getMinecraft().renderEngine.bindTexture(progress);
			tess.startDrawingQuads();

			double widthnew = (0 + (width * (2)));
			double heightnew = (0 + ((maxY - minY) * (2)));

			tess.addVertexWithUV((minX + 0), maxY / 2, 0, 0, heightnew);
			tess.addVertexWithUV((minX + width), maxY / 2, 0, widthnew, heightnew);
			tess.addVertexWithUV((minX + width), (minY + 0), 0, widthnew, 0);
			tess.addVertexWithUV((minX + 0), (minY + 0), 0, 0, 0);

			tess.draw();
		}

		GL11.glTranslatef(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2, 0.01f);
		int sizing = Math.round(Math.min((maxX - minX), (maxY - minY) * 3));
		GL11.glTranslatef(0.0f, (float) (scale >= 120 ? -0.08F : -0.2F + ((sizing - 1) * 0.001)), 0);
		double itemScale = sizing >= 2 ? InfoRenderer.getScale(sizing) : 120;
		GL11.glScaled(1.0f / itemScale, 1.0f / itemScale, 1.0f / itemScale);
		rend.drawString(data, -rend.getStringWidth(data) / 2, -4, -1);
	}

	/*
	 * @Override public boolean isEqualType(Info info) { if (info != null &&
	 * info.getName().equals(this.getName())) { return
	 * info.getCategory().equals(this.getCategory()) &&
	 * info.getSubCategory().equals(getSubCategory()); } return false; }
	 * 
	 * @Override public void emptyData() {
	 * 
	 * }
	 */
	@Override
	public ProgressInfo instance() {
		return new ProgressInfo();
	}

	@Override
	public void writeUpdate(ProgressInfo currentInfo, NBTTagCompound tag) {
		if (currentInfo.max != max) {
			tag.setLong("m", currentInfo.max);
			max = currentInfo.max;
		}

		if (currentInfo.stored != stored) {
			tag.setLong("s", currentInfo.stored);
			stored = currentInfo.stored;
		}
		if (currentInfo.data!=null && !currentInfo.data.equals(this.data)) {
			tag.setString("d", currentInfo.data);
			data = currentInfo.data;
		}
	}

	@Override
	public void readUpdate(NBTTagCompound tag) {
		if (tag.hasKey("m")) {
			max = tag.getLong("m");
		}
		if (tag.hasKey("s")) {
			stored = tag.getLong("s");
		}
		if (tag.hasKey("d")) {
			data = tag.getString("d");
		}
	}

	@Override
	public boolean matches(ProgressInfo currentInfo) {
		return currentInfo.max == max && currentInfo.stored == stored && currentInfo.data.equals(data);
	}

}
