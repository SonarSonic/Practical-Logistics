package sonar.logistics.client.gui;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.client.gui.GuiSonar;
import sonar.core.helpers.FontHelper;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.handlers.DataModifierHandler;

public class GuiDataModifier extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/signaller.png");
	private GuiTextField subCategory, prefix, suffix;
	public DataModifierHandler handler;
	public TileEntity entity;

	public GuiDataModifier(DataModifierHandler handler, TileEntity entity) {
		super(new ContainerEmptySync(handler, entity), entity);
		this.handler = handler;
		this.entity = entity;
	}

	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.mc.thePlayer.openContainer = this.inventorySlots;
		this.xSize = 176;
		this.ySize = 80;
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		subCategory = new GuiTextField(0, this.fontRendererObj, 42, 20, 126, 12);
		subCategory.setMaxStringLength(21);
		subCategory.setText(this.getText(0));

		prefix = new GuiTextField(1, this.fontRendererObj, 42, 40, 126, 12);
		prefix.setMaxStringLength(21);
		prefix.setText(this.getText(1));

		suffix = new GuiTextField(2, this.fontRendererObj, 42, 60, 126, 12);
		suffix.setMaxStringLength(21);
		suffix.setText(this.getText(2));
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(FontHelper.translate("tile.DataModifier.name"), xSize, 6, 0);
		FontHelper.text("Name:", 6, 22, 0);
		FontHelper.text("Prefix:", 6, 22 + 20, 0);
		FontHelper.text("Suffix:", 6, 22 + 40, 0);
		subCategory.drawTextBox();
		prefix.drawTextBox();
		suffix.drawTextBox();
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		try {
			super.mouseClicked(i, j, k);
		} catch (IOException e) {
			e.printStackTrace();
		}
		subCategory.mouseClicked(i - guiLeft, j - guiTop, k);
		prefix.mouseClicked(i - guiLeft, j - guiTop, k);
		suffix.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) {
		if (subCategory.isFocused()) {
			if (c == 13 || c == 27) {
				subCategory.setFocused(false);
			} else {
				subCategory.textboxKeyTyped(c, i);
				final String text = subCategory.getText();
				if (text.isEmpty() || text == "" || text == null) {
					setString("", 0);
				} else {
					setString(text, 0);
				}

			}
		} else if (prefix.isFocused()) {
			if (c == 13 || c == 27) {
				prefix.setFocused(false);
			} else {
				prefix.textboxKeyTyped(c, i);
				final String text = prefix.getText();
				if (text.isEmpty() || text == "" || text == null) {
					setString("", 1);
				} else {
					setString(text, 1);
				}

			}
		} else if (suffix.isFocused()) {
			if (c == 13 || c == 27) {
				suffix.setFocused(false);
			} else {
				suffix.textboxKeyTyped(c, i);
				final String text = suffix.getText();
				if (text.isEmpty() || text == "" || text == null) {
					setString("", 2);
				} else {
					setString(text, 2);
				}

			}
		} else {
			try {
				super.keyTyped(c, i);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public String getText(int id) {
		switch (id) {
		case 1:
			return handler.prefix.getObject();
		case 2:
			return handler.suffix.getObject();
		default:
			return handler.subCategory.getObject();
		}
	}

	public void setString(String string, int id) {
		SonarCore.sendPacketToServer(entity, string, id);
		switch (id) {
		case 1:
			handler.prefix.setObject(string);
			break;
		case 2:
			handler.suffix.setObject(string);
			break;
		default:
			handler.subCategory.setObject(string);
			break;
		}
	}

}
