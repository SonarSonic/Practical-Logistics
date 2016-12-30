package sonar.logistics.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.RenderHelper;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.logistics.api.settings.InventoryReader.Modes;
import sonar.logistics.client.LogisticsColours;
import sonar.logistics.client.gui.GuiInventoryReader.FilterButton;
import sonar.logistics.common.containers.ContainerArray;
import sonar.logistics.common.multiparts.ArrayPart;
import sonar.logistics.common.multiparts.DataEmitterPart;

public class GuiDataEmitter extends GuiLogistics {
	public DataEmitterPart part;
	private GuiTextField nameField;

	public GuiDataEmitter(DataEmitterPart part) {
		super(new ContainerMultipartSync(part), part);
		this.part = part;
		this.ySize = 40;
	}

	public void initGui() {
		super.initGui();
		nameField = new GuiTextField(0, this.fontRendererObj, 8, 18, 160, 12);
		nameField.setMaxStringLength(20);
		nameField.setText(part.getEmitterName());
	}

	@Override
	public void drawGuiContainerForegroundLayer(int par1, int par2) {
		super.drawGuiContainerForegroundLayer(par1, par2);
		nameField.drawTextBox();
		FontHelper.textCentre(FontHelper.translate("item.DataEmitter.name"), xSize, 6, LogisticsColours.white_text.getRGB());
	}

	@Override
	public void mouseClicked(int i, int j, int k) throws IOException {
		super.mouseClicked(i, j, k);
		nameField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) throws IOException {
		if (nameField.isFocused()) {
			if (c == 13 || c == 27) {
				nameField.setFocused(false);
			} else {
				nameField.textboxKeyTyped(c, i);
				final String text = nameField.getText();
				setString((text.isEmpty() || text == "" || text == null) ? "Unnamed Emitter" : text);
			}
		} else {
			super.keyTyped(c, i);
		}
	}

	public void setString(String string) {
		this.part.emitterName.setObject(string);
		part.sendByteBufPacket(2);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		RenderHelper.restoreBlendState();
	}
}
