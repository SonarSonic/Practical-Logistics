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
import sonar.logistics.info.providers.tile.ManaProvider;
import cpw.mods.fml.common.network.ByteBufUtils;

public class ManaInfo extends ProgressInfo {

	private static final ResourceLocation progress = new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png");
	public long stored, max;
	public int providerID = -1;

	public ManaInfo() {
	}

	public ManaInfo(int providerID, long stored, long max) {
		this.stored = stored;
		this.max = max;
		this.providerID = providerID;
		this.data = " ";
	}

	@Override
	public String getType() {
		return "ManaBar";
	}

	@Override
	public byte getProviderID() {
		return (byte) providerID;
	}

	@Override
	public String getCategory() {
		return new ManaProvider().getCategory((byte) 0);
	}

	@Override
	public String getSubCategory() {
		return "Mana";
	}

	@Override
	public String getData() {
		return String.valueOf(stored);
	}

	@Override
	public String getDisplayableData() {
		return "-";
	}

	@Override
	public int getDataType() {
		return 1;
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		this.stored = buf.readLong();
		this.max = buf.readLong();
		this.providerID = buf.readByte();
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeLong(stored);
		buf.writeLong(max);
		buf.writeByte(providerID);
		
	}
	public void readFromNBT(NBTTagCompound tag) {
		this.stored = tag.getLong("stored");
		this.max = tag.getLong("max");
		this.providerID = tag.getByte("ID");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("stored", stored);
		tag.setLong("max", max);
		tag.setByte("ID", (byte) providerID);
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

		Minecraft.getMinecraft().renderEngine.bindTexture(progress);
		tess.startDrawingQuads();
		tess.addVertexWithUV(start, 0, 0, 0, 0);
		tess.addVertexWithUV(start, height, 0, 0, height);
		tess.addVertexWithUV(start + width, height, 0, width, height);
		tess.addVertexWithUV(start + width, 0, 0, width, 0);
		tess.draw();

		GL11.glTranslated(+0.5, +0.2085, +0.205);
		GL11.glTranslatef(0.0f, -0.04f, -0.20f);
		GL11.glScalef(1.0f / 120.0f, 1.0f / 120.0f, 1.0f / 120.0f);
		String data = getSubCategory();
		rend.drawString(data, -rend.getStringWidth(data) / 2, 0, -1);
	}

	@Override
	public boolean isEqualType(Info info) {
		if (info != null && info.getType().equals(this.getType())) {
			return info.getCategory().equals(this.getCategory()) && info.getSubCategory().equals(getSubCategory());
		}
		return false;
	}

	@Override
	public void emptyData() {

	}

	@Override
	public Info newInfo() {
		return new ManaInfo();
	}

}
