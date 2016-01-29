package sonar.logistics.common.tileentity;

public class TileEntityDataCable extends TileEntityMultiDataCable {

	@Override
	public boolean unlimitedChannels() {
		return false;
	}
}
