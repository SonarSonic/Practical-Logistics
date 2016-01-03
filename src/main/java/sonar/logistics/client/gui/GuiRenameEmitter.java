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
import sonar.logistics.common.tileentity.TileEntityDataEmitter;

public abstract class GuiRenameEmitter extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/rename.png");
	private GuiTextField nameField;

	public GuiRenameEmitter(Container container, TileEntity entity) {
		super(container, entity);
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
		nameField = new GuiTextField(this.fontRendererObj, 40, 20, 126, 12);
		nameField.setMaxStringLength(19);
		nameField.setText(this.getText());
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre("Data Emitter", xSize, 6, 0);
		FontHelper.text("Name:", 6, 22, 0);
		nameField.drawTextBox();
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		nameField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	@Override
	protected void keyTyped(char c, int i) {
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

	public abstract String getText();

	public abstract void setString(String string);

	public static class DataEmitter extends GuiRenameEmitter {

		public TileEntityDataEmitter entity;

		public DataEmitter(TileEntityDataEmitter entity) {
			super(new ContainerEmptySync(entity), entity);
			this.entity = entity;
		}

		@Override
		public String getText() {
			return entity.clientName.getString();
		}

		@Override
		public void setString(String string) {
			SonarCore.network.sendToServer(new PacketTextField(string, entity.xCoord, entity.yCoord, entity.zCoord, 0));
			entity.clientName.setString(string);;
		}

	}
}
