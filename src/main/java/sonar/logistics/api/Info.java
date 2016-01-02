package sonar.logistics.api;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public abstract class Info {

	public abstract String getType();

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

	public abstract boolean hasSpecialRender();

	public abstract void renderInfo(Tessellator tess, TileEntity tile);

	public abstract boolean isEqualType(Info info);

	public boolean isDataEqualType(Info info) {
		if(info==null){
			return false;
		}
		return this.getData().equals(info.getData()) && this.getDisplayableData().equals(info.getDisplayableData());
	}

	public abstract void emptyData();

	public abstract Info newInfo();

}
