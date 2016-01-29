package sonar.logistics.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTObject;
import sonar.logistics.api.render.InfoRenderer;

public abstract class Info<T extends Info> implements INBTObject, IBufObject {

	public String providerID = "";

	public abstract String getName();

	public abstract byte getProviderID();

	public abstract String getCategory();

	public abstract String getSubCategory();

	public abstract String getData();

	public abstract String getDisplayableData();

	public abstract int getDataType();

	public abstract void readFromBuf(ByteBuf buf);

	public abstract void writeToBuf(ByteBuf buf);

	public abstract void readFromNBT(NBTTagCompound buf);

	public abstract void writeToNBT(NBTTagCompound buf);

	public abstract boolean isEqualType(Info info);

	public abstract void emptyData();

	public boolean isDataEqualType(Info info) {
		if (info == null) {
			return false;
		}
		return this.getData().equals(info.getData()) && this.getDisplayableData().equals(info.getDisplayableData());
	}

	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, float scale) {
		InfoRenderer.renderStandardInfo(this, Minecraft.getMinecraft().fontRenderer, minX, minY, maxX, maxY, zOffset, scale);
	}

	public boolean isLoadable() {
		return true;
	}
}
