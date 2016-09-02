package sonar.logistics.client.gui;
/*
import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.client.gui.GuiSonar;
import sonar.core.helpers.FontHelper;
import sonar.core.inventory.ContainerMultipartSync;
import sonar.core.network.PacketByteBuf;
import sonar.logistics.parts.DataEmitterPart;

public class GuiRenameEmitter extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/rename.png");
	private GuiTextField nameField;

	public DataEmitterPart entity;

	public GuiRenameEmitter(DataEmitterPart entity) {
		super(new ContainerMultipartSync(entity), entity);
		this.entity = entity;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = 176;
		this.ySize = 52;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		this.buttonList.add(new GuiButton(0, guiLeft + 40, guiTop + 24, 126, 20, this.getProtectionType()));
		nameField = new GuiTextField(0, this.fontRendererObj, 40, 8, 126, 12);
		nameField.setMaxStringLength(19);
		nameField.setText(entity.clientName.getObject());
	}

	public String getProtectionType() {
		boolean type = entity.isPrivate.getObject();
		if (type) {
			return "PRIVATE";
		} else {
			return "PUBLIC";
		}
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == 0) {
				SonarCore.network.sendToServer(new PacketByteBuf(entity, entity.getPos(), 0));
				reset();
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		//FontHelper.text(TextFormatting.BLACK + "Data Emitter", 12, 9, 0);
		FontHelper.text("Name:", 6, 10, 0);
		nameField.drawTextBox();
	}

	@Override
	protected void mouseClicked(int i, int j, int k) throws IOException {
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
				if (text.isEmpty() || text == "" || text == null) {
					setString("Unnamed Emitter");
				} else {
					setString(text);
				}

			}
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public void setString(String string) {
		SonarCore.sendPacketToServer(entity, string, 0);
		entity.clientName.setObject(string);
	}

}
*/