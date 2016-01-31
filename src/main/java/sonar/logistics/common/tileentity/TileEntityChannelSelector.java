package sonar.logistics.common.tileentity;

import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.network.utils.ITextField;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.IdentifiedCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.helpers.CableHelper;
import sonar.logistics.info.types.BlockCoordsInfo;
import sonar.logistics.registries.CableRegistry;

public class TileEntityChannelSelector extends TileEntityHandler implements IInfoEmitter, ICableRenderer, IChannelProvider {

	public ChannelSelectorHandler handler = new ChannelSelectorHandler(false, this);

	@Override
	public TileHandler getTileHandler() {
		return handler;
	}

	@Override
	public boolean canConnect(ForgeDirection dir) {
		return handler.canConnect(this, dir);
	}

	@Override
	public Info currentInfo() {
		return BlockCoordsInfo.createInfo("Channel Selector", new BlockCoords(this));
	}

	public boolean maxRender() {
		return true;
	}

	@Override
	public int canRenderConnection(ForgeDirection dir) {
		return handler.canRenderConnection(this, dir);
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public void addConnections() {
		CableHelper.addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));

	}

	@Override
	public void removeConnections() {
		CableHelper.addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public IdentifiedCoords getChannel() {
		return handler.getChannel(this);
	}
}
