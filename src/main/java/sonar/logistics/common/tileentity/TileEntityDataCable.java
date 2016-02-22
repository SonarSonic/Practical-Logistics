package sonar.logistics.common.tileentity;

import sonar.logistics.api.connecting.CableType;

public class TileEntityDataCable extends TileEntityChannelledCable {

	@Override
	public CableType getCableType() {
		return CableType.DATA_CABLE;
	}
}
