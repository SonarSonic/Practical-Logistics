package sonar.logistics.client.gui;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import sonar.core.SonarCore;
import sonar.core.inventory.GuiSonar;
import sonar.core.network.PacketTextField;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.handlers.InfoCreatorHandler;
import sonar.logistics.common.tileentity.TileEntityInfoCreator;

public class GuiInfoCreator extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/signaller.png");
	private GuiTextField subCategory, data;
	public InfoCreatorHandler handler;
	public TileEntity entity;
	
	public GuiInfoCreator(InfoCreatorHandler handler, TileEntity entity) {
		super(new ContainerEmptySync(handler, entity), entity);
		this.handler = handler;
		this.entity= entity;
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
		subCategory = new GuiTextField(this.fontRendererObj, 42, 20, 126, 12);
		subCategory.setMaxStringLength(21);
		subCategory.setText(this.getText(0));

		data = new GuiTextField(this.fontRendererObj, 42, 40, 126, 12);
		data.setMaxStringLength(21);
		data.setText(this.getText(1));
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre("Info Creator", xSize, 6, 0);
		FontHelper.text("Name:", 6, 22, 0);
		FontHelper.text("Data:", 6, 22 + 20, 0);
		subCategory.drawTextBox();
		data.drawTextBox();
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		subCategory.mouseClicked(i - guiLeft, j - guiTop, k);
		data.mouseClicked(i - guiLeft, j - guiTop, k);
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
		}else if (data.isFocused()) {
			if (c == 13 || c == 27) {
				data.setFocused(false);
			} else {
				data.textboxKeyTyped(c, i);
				final String text = data.getText();
				if (text.isEmpty() || text == "" || text == null) {
					setString("", 1);
				} else {
					setString(text, 1);
				}

			}
		}else {
			super.keyTyped(c, i);
		}
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public String getText(int id){
		switch (id) {
		case 1:
			return handler.data.getString();
		case 0:
			return handler.subCategory.getString();
		}
		return " ";
	}

	public void setString(String string, int id){
		SonarCore.network.sendToServer(new PacketTextField(string, entity.xCoord,  entity.yCoord,  entity.zCoord, id));
		switch (id) {
		case 0:
			handler.subCategory.setString(string);
			break;
		case 1:
			handler.data.setString(string);
			break;
		}
	}

}
