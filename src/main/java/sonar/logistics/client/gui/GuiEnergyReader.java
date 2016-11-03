package sonar.logistics.client.gui;
/*
import java.util.ArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import sonar.core.helpers.FontHelper;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.common.containers.ContainerEnergyReader;
import sonar.logistics.common.containers.ContainerInfoReader;
import sonar.logistics.helpers.InfoRenderer;
import sonar.logistics.monitoring.MonitoredEnergyStack;
import sonar.logistics.parts.EnergyReaderPart;
import sonar.logistics.parts.InfoReaderPart;

public class GuiEnergyReader extends GuiSelectionList<MonitoredEnergyStack> {

	public EnergyReaderPart part;
	
	public GuiEnergyReader(EntityPlayer player, EnergyReaderPart tile) {
		super(new ContainerEnergyReader(player, tile), tile);
		this.part = tile;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(FontHelper.translate("item.InfoReader.name"), xSize, 6, LogisticsColours.white_text);
		FontHelper.textCentre(String.format("Select the data you wish to monitor"), xSize, 18, LogisticsColours.grey_text);
	}

	public void setInfo() {
		infoList = part.getMonitoredList().cloneInfo();
	}

	@Override
	public void selectionPressed(GuiButton button, int buttonID, MonitoredEnergyStack info) {
		if (info.isValid() && !info.isHeader()) {
			part.selectedInfo.setInfo(info);
			part.sendByteBufPacket(buttonID == 0 ? -9 : -10);
		}
	}

	@Override
	public boolean isCategoryHeader(MonitoredEnergyStack info) {
		return info.isHeader();
	}

	@Override
	public boolean isSelectedInfo(MonitoredEnergyStack info) {
		if(!info.isValid() || info.isHeader()){
			return false;
		}
		ArrayList<IMonitorInfo> selectedInfo = part.getSelectedInfo();
		for (IMonitorInfo selected : selectedInfo) {
			if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo((LogicInfo) selected)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isPairedInfo(MonitoredEnergyStack info) {
		if(!info.isValid() || info.isHeader()){
			return false;
		}
		return false;
	}

	@Override
	public void renderInfo(MonitoredEnergyStack info, int yPos) {
		InfoRenderer.renderMonitorInfoInGUI(info, yPos + 1, LogisticsColours.white_text.getRGB());
	}

	@Override
	public int getColour(int i, int type) {
		IMonitorInfo info = (IMonitorInfo) infoList.get(i + start);
		if (info == null || info.isHeader()) {
			return LogisticsColours.layers[1].getRGB();
		}
		ArrayList<IMonitorInfo> selectedInfo = type == 0 ? part.getSelectedInfo() : part.getPairedInfo();
		int pos = 0;
		for (IMonitorInfo selected : selectedInfo) {
			if (selected != null && !selected.isHeader() && info.isMatchingType(selected) && info.isMatchingInfo(selected)) {
				return LogisticsColours.infoColours[pos].getRGB();
			}
			pos++;
		}
		return LogisticsColours.layers[1].getRGB();
	}
}
*/