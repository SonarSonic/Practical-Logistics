package sonar.logistics.api.info;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.IBufObject;
import sonar.core.utils.INBTObject;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.render.ScreenType;

public abstract class ILogicInfo<T extends ILogicInfo> implements INBTObject, IBufObject {

	// public String providerID = "";
	public int updateTime = 1;

	public ILogicInfo setUpdateTime(int time) {
		this.updateTime = time;
		return this;
	}

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

	// /**do they match*/
	// public abstract boolean matches(T currentInfo);

	/** null means the data is identical, sync means it is the right type of data but needs to be updated, save means it is a different type of data */
	public abstract SyncType isMatchingData(T currentInfo);
	
	/**used to get the next SyncType based off the latest info */
	public final SyncType getNextSyncType(T currentInfo) {
		if (currentInfo == null || !checkInfoTypes(currentInfo)) {
			return SyncType.SAVE;
		} else {
			return isMatchingData(currentInfo);
		}
	}

	/** same type of data */
	public final boolean checkInfoTypes(T info) {
		return info == null ? false : info.getName().equals(this.getName()) && getProviderID() == info.getProviderID();
	}
	/**does the info match (the data isn't accounted for here*/
	public final boolean isMatchingInfo(T currentInfo) {
		SyncType match = getNextSyncType(currentInfo);
		return match == null || match == SyncType.SYNC;
	}
	/**only true if data and info is equal*/
	public final boolean equals(Object obj) {
		if (obj instanceof ILogicInfo<?>) {
			T target = (T) obj;
			if (target instanceof ILogicInfo && checkInfoTypes(target)) {
				return isMatchingData(target) == null;
			}
		}
		return false;
	}

	public void renderInfo(Tessellator tess, TileEntity tile, float minX, float minY, float maxX, float maxY, float zOffset, ScreenType type) {
		LogisticsAPI.getInfoRenderer().renderStandardInfo(this, minX, minY, maxX, maxY, zOffset, type);
	}

	public int updateTicks() {
		return updateTime;
	}

	public boolean isLoadable() {
		return true;
	}
}
