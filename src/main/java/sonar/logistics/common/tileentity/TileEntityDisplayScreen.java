package sonar.logistics.common.tileentity;

import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.SonarCore;
import sonar.core.api.BlockCoords;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.logistics.api.connecting.IInfoScreen;
import sonar.logistics.api.connecting.IInfoTile;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.common.handlers.DisplayScreenHandler;

public class TileEntityDisplayScreen extends TileEntityHandler implements IInfoScreen {

	public DisplayScreenHandler handler = new DisplayScreenHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public ILogicInfo[] getDisplayInfo() {
		return handler.getDisplayInfo();
	}

	@Override
	public ScreenLayout getScreenLayout() {
		return handler.getScreenLayout();
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		for (int i = 0; i < this.handler.dInfo.length; i++)
			SonarCore.sendPacketAround(this, 64, i+10);
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}
}