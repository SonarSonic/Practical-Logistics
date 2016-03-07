package sonar.logistics.api.info;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.render.ScreenType;

public abstract class Info<T extends Info> implements INBTObject, IBufObject {

	public String providerID = "";

	public abstract String getName();

	public abstract int getProviderID();

	public abstract String getCategory();

	public abstract String getSubCategory();

	public abstract String getData();

	public abstract String getDisplayableData();

	public abstract int getDataType();

	public abstract void readFromBuf(ByteBuf buf);

	public abstract void writeToBuf(ByteBuf buf);

	public abstract void readFromNBT(NBTTagCompound buf);

	public abstract void writeToNBT(NBTTagCompound buf);

	public abstract void writeUpdate(T currentInfo, NBTTagCompound tag);

	public abstract void readUpdate(NBTTagCompound tag);

	public abstract boolean matches(T currentInfo);

	public boolean areTypesEqual(Info info) {
		return info == null ? false : info.getName().equals(this.getName()) && getProviderID() == info.getProviderID();
	}

	public boolean equals(Object obj) {
		if (obj instanceof Info<?>) {
			T target = (T) obj;
			if (areTypesEqual(target)) {
				return matches(target);
			}
		}
		return false;
	}

	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		LogisticsAPI.getInfoRenderer().renderStandardInfo(this, Minecraft.getMinecraft().fontRenderer, minX, minY, maxX, maxY, zOffset, type);
	}

	public int updateTicks() {
		return 1;
	}

	public boolean isLoadable() {
		return true;
	}
}
