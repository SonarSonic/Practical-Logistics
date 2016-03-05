package sonar.logistics.common.tileentity;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraftforge.common.util.ForgeDirection;
import sonar.core.common.tileentity.TileEntityHandler;
import sonar.core.integration.fmp.FMPHelper;
import sonar.core.integration.fmp.handlers.TileHandler;
import sonar.core.utils.BlockCoords;
import sonar.logistics.api.ExternalCoords;
import sonar.logistics.api.Info;
import sonar.logistics.api.LogisticsAPI;
import sonar.logistics.api.cache.CacheTypes;
import sonar.logistics.api.connecting.CableType;
import sonar.logistics.api.connecting.IChannelProvider;
import sonar.logistics.api.connecting.IConnectionNode;
import sonar.logistics.api.connecting.IInfoEmitter;
import sonar.logistics.api.render.ICableRenderer;
import sonar.logistics.common.handlers.ChannelSelectorHandler;
import sonar.logistics.info.types.BlockCoordsInfo;

public class TileEntityChannelSelector extends TileEntityHandler implements IInfoEmitter, ICableRenderer, IConnectionNode {

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
	public CableType canRenderConnection(ForgeDirection dir) {
		return handler.canRenderConnection(this, dir);
	}

	@Override
	public BlockCoords getCoords() {
		return new BlockCoords(this);
	}

	@Override
	public void addConnections() {
		LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));

	}

	@Override
	public void removeConnections() {
		LogisticsAPI.getCableHelper().addConnection(this, ForgeDirection.getOrientation(FMPHelper.getMeta(this)));
	}

	@Override
	public Map<BlockCoords, ForgeDirection> getConnections() {
		LinkedHashMap<BlockCoords, ForgeDirection> map = new LinkedHashMap();
		try {
			ExternalCoords coords = handler.getChannel(this);
			map.put(coords.blockCoords, coords.dir);
		} catch (Exception exception) {
		}
		return map;
	}
}
