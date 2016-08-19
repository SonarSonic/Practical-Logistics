package sonar.logistics.api.info;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.utils.CustomColour;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.registries.LogicRegistry.RegistryType;

public class DisplayInfo implements IDisplayInfo {

	public CustomColour textColour, backgroundColour;
	
	@Override
	public void writeToBuf(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readFromBuf(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean canSync(SyncType sync) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getTagName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasChanged() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setChanged(boolean set) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IMonitorInfo getCachedInfo() {
		return LogicInfo.buildDirectInfo("Practical Logistics", RegistryType.TILE, "Version 2.0");
	}

	@Override
	public CustomColour getTextColour() {
		return textColour;
	}

	@Override
	public CustomColour getBackgroundColour() {
		return backgroundColour;
	}

	@Override
	public InfoUUID getInfoUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateInfo(ByteBuf buf) {
		// TODO Auto-generated method stub
		
	}

}
