package sonar.logistics.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.logistics.api.display.DisplayInfo;
import sonar.logistics.api.info.INameableInfo;
import sonar.logistics.api.info.monitor.IMonitorInfo;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.common.multiparts.DataEmitterPart;
import sonar.logistics.common.multiparts.ScreenMultipart;

public class GuiDisplayScreen extends GuiLogistics {
	public ScreenMultipart part;
	// private GuiTextField nameField;
	private GuiState state = GuiState.LIST;

	private int width = 225;
	private int height = 24;
	private int left = 7;

	public enum GuiState {
		LIST, EDIT, SOURCE, CREATE;
	}

	public GuiDisplayScreen(ScreenMultipart part) {
		super(new ContainerMultipartSync(part), part);
		this.part = part;
		this.ySize = 20 + part.maxInfo()*26;
	}

	public void initGui() {
		super.initGui();

		this.buttonList.add(new GuiButton(2, guiLeft + 130-3, guiTop + 20, 30, 20, "Edit"));
		
		/* nameField = new GuiTextField(0, this.fontRendererObj, 8, 18, 160, 12); nameField.setMaxStringLength(20); nameField.setText(part.getEmitterName()); */
	}

	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		// nameField.drawTextBox();
		FontHelper.textCentre(FontHelper.translate("item.DisplayScreen.name"), xSize, 6, LogisticsColours.white_text.getRGB());
		RenderHelper.saveBlendState();
		int size = part.container.storedInfo.size();
		for (int i = 0; i < part.maxInfo(); i++) {
			drawInfo(i, i < size ? part.container.storedInfo.get(i).getObject() : null);
		}
		RenderHelper.restoreBlendState();
	}

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		// nameField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		super.keyTyped(c, i);
		/* if (nameField.isFocused()) { if (c == 13 || c == 27) { nameField.setFocused(false); } else { nameField.textboxKeyTyped(c, i); final String text = nameField.getText(); setString((text.isEmpty() || text == "" || text == null) ? "Unnamed Emitter" : text); } } else { super.keyTyped(c, i); } */
	}

	public void setString(String string) {
		//this.part.emitterName.setObject(string);
		//part.sendByteBufPacket(2);
	}

	public void drawInfo(int pos, DisplayInfo info) {
		int width = 162;
		int height = 20;
		int left = 7;
		int top = 20 + ((height+6) * pos);
		drawTransparentRect(left, top, left + width, top + height, LogisticsColours.layers[2].getRGB());
		drawTransparentRect(left + 1, top + 1, left - 1 + width, top - 1 + height, LogisticsColours.grey_base.getRGB());
		if (info == null)
			return;

		IMonitorInfo monitorInfo = info.getCachedInfo();
		if (monitorInfo instanceof INameableInfo) {
			INameableInfo directInfo = (INameableInfo) monitorInfo;
			FontHelper.text(directInfo.getClientIdentifier(), 11, top+6, LogisticsColours.white_text.getRGB());
		}
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		RenderHelper.restoreBlendState();
	}
}
