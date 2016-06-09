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
import sonar.logistics.api.connecting.ILargeDisplay;
import sonar.logistics.api.connecting.IInfoScreen.ScreenLayout;
import sonar.logistics.api.info.ILogicInfo;
import sonar.logistics.api.render.LargeScreenSizing;
import sonar.logistics.common.handlers.LargeDisplayScreenHandler;
import sonar.logistics.helpers.DisplayHelper;

public class TileEntityLargeScreen extends TileEntityHandler implements IInfoScreen, ILargeDisplay {

	public LargeDisplayScreenHandler handler = new LargeDisplayScreenHandler(false, this);

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

	public boolean maxRender() {
		return true;
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		for (int i = 0; i < this.handler.dInfo.length; i++)
			SonarCore.sendPacketAround(this, 64, i+10);
	}

	public void onLoaded() {
		super.onLoaded();
		if (!this.worldObj.isRemote)
			DisplayHelper.addScreen(this);
	}

	public void invalidate() {
		if (!this.worldObj.isRemote)
			DisplayHelper.removeScreen(this);
		super.invalidate();
	}

	@Override
	public int registryID() {
		return handler.registryID;
	}

	@Override
	public void setRegistryID(int id) {
		handler.registryID = id;
	}

	@Override
	public int getOrientation() {
		return this.getBlockMetadata();
	}

	@Override
	public LargeScreenSizing getSizing() {
		return handler.sizing;
	}
}