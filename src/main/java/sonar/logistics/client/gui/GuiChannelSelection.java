package sonar.logistics.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import sonar.core.helpers.FontHelper;
import sonar.logistics.api.connecting.IChannelledTile;
import sonar.logistics.api.connecting.IOperatorTool;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.RenderBlockSelection;
import sonar.logistics.common.containers.ContainerChannelSelection;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.common.multiparts.InfoReaderPart;
import sonar.logistics.connections.managers.NetworkManager;
import sonar.logistics.connections.monitoring.MonitoredBlockCoords;
import sonar.logistics.connections.monitoring.MonitoredList;
import sonar.logistics.helpers.InfoRenderer;

public class GuiChannelSelection extends GuiSelectionList<MonitoredBlockCoords> {
	IChannelledTile tile;

	public GuiChannelSelection(IChannelledTile tile) {
		super(new ContainerChannelSelection(tile), tile);
		this.tile = tile;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(FontHelper.translate("Channel Selection"), xSize, 6, LogisticsColours.white_text);
		FontHelper.textCentre(String.format("Select the channels you wish to monitor"), xSize, 18, LogisticsColours.grey_text);
	}

	public void selectionPressed(GuiButton button, int buttonID, MonitoredBlockCoords info) {
		if (buttonID == 0) {
			tile.modifyCoords(info);
		} else {
			RenderBlockSelection.addPosition(info.syncCoords.getCoords(), false);
		}
	}

	public void setInfo() {
		infoList = (ArrayList<MonitoredBlockCoords>) NetworkManager.getCoordMap().getOrDefault(tile.getNetworkID(), MonitoredList.<MonitoredBlockCoords>newMonitoredList(tile.getNetworkID())).clone();
	}

	@Override
	public boolean isCategoryHeader(MonitoredBlockCoords info) {
		if (!RenderBlockSelection.positions.isEmpty()) {
			if (RenderBlockSelection.isPositionRenderered(info.syncCoords.getCoords())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isSelectedInfo(MonitoredBlockCoords info) {
		if (info.isValid() && !info.isHeader() && tile.getChannels().contains(info.syncCoords.getCoords())) {
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
