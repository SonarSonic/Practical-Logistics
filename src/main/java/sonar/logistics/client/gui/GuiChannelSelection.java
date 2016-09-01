package sonar.logistics.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.RenderBlockSelection;
import sonar.logistics.common.containers.ContainerChannelSelection;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.connections.CacheRegistry;
import sonar.logistics.connections.MonitoredList;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.monitoring.MonitoredBlockCoords;
import sonar.logistics.parts.InfoReaderPart;

public class GuiChannelSelection extends GuiNewSelectionList<MonitoredBlockCoords> {
	IChannelledTile tile;

	public GuiChannelSelection(IOperatorTool tool, IChannelledTile tile) {
		super(new ContainerChannelSelection(tool, tile), tile);
		this.tile = tile;
	}

	public void selectionPressed(GuiButton button, int buttonID, MonitoredBlockCoords info) {
		if (buttonID == 0) {
			part.lastSelected = ((MonitoredBlockCoords) info).coords.getCoords();
			part.sendByteBufPacket(-3);
		} else {
			RenderBlockSelection.addPosition(info.coords.getCoords(), false);
		}
	}

	public void setInfo() {
		infoList = (ArrayList<MonitoredBlockCoords>) CacheRegistry.coordMap.getOrDefault(tile.getNetworkID(), MonitoredList.<MonitoredBlockCoords>newMonitoredList()).clone();
	}

	@Override
	public boolean isCategoryHeader(MonitoredBlockCoords info) {
		if (RenderBlockSelection.positions.isEmpty()) {
			if (RenderBlockSelection.isPositionRenderered(info.coords.getCoords())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSelectedInfo(MonitoredBlockCoords info) {
		if (info.isValid() && tile.getChannels().contains(info.coords.getCoords())) {
			return true;
		}
		return false;
	}

	@Override
	public void renderInfo(MonitoredBlockCoords info, int yPos) {
		InfoRenderer.renderMonitorInfoInGUI(info, yPos + 1, LogisticsColours.white_text.getRGB());
	}

	@Override
	public int getColour(int i, int type) {
		return LogisticsColours.getDefaultSelection().getRGB();
	}

	@Override
	public boolean isPairedInfo(MonitoredBlockCoords info) {
		return false;
	}

}
