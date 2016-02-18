package sonar.logistics.info.types;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import sonar.core.utils.helpers.RenderHelper;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.render.ScreenType;
import sonar.logistics.client.renderers.RenderHandlers;
import sonar.logistics.info.providers.tile.ManaProvider;

public class ManaInfo extends ProgressInfo {

	private static final ResourceLocation progress = new ResourceLocation(RenderHandlers.modelFolder + "progressBar.png");
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
	public String getName() {
		return "ManaBar";
	}

	@Override
	public int getProviderID() {
		return providerID;
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
		this.providerID = buf.readInt();
	}

	@Override
	public void writeToBuf(ByteBuf buf) {
		buf.writeLong(stored);
		buf.writeLong(max);
		buf.writeInt(providerID);

	}

	public void readFromNBT(NBTTagCompound tag) {
		this.stored = tag.getLong("stored");
		this.max = tag.getLong("max");
		this.providerID = tag.getInteger("ID");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setLong("stored", stored);
		tag.setLong("max", max);
		tag.setInteger("ID", (byte) providerID);
	}

	@Override
	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		GL11.glTranslated(0, 0, zOffset);
		float width = stored * (maxX - minX) / max;
		Minecraft.getMinecraft().renderEngine.bindTexture(progress);
		RenderHelper.drawTexturedModalRect(minX, minY, maxY, width, (maxY - minY));
		LogisticsAPI.getInfoRenderer().renderCenteredString(getSubCategory(), minX, minY, maxX, maxY, type);
	}

	@Override
	public ManaInfo instance() {
		return new ManaInfo();
	}

	@Override
	public boolean matches(ProgressInfo currentInfo) {
		if (currentInfo instanceof ManaInfo) {
			return currentInfo.max == max && currentInfo.stored == stored;
		} else {
			return false;
		}
	}
}
