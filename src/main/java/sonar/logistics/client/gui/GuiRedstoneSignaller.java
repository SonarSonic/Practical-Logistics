package sonar.logistics.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import sonar.core.inventory.GuiSonar;
import sonar.core.network.PacketMachineButton;
import sonar.core.network.PacketTextField;
import sonar.core.network.SonarPackets;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.tileentity.TileEntityRedstoneSignaller;

public abstract class GuiRedstoneSignaller extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/signaller.png");
	private GuiTextField stringField;
	private GuiTextField integerField;
	private int type;

	public GuiRedstoneSignaller(Container container, TileEntity entity) {
		super(container, entity);
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
		this.buttonList.add(new GuiButton(0, guiLeft + 80, guiTop + 6, 60, 20, this.getSyncType()));
		type=this.getGuiType();
		if (type == 0) {
			integerField = new GuiTextField(this.fontRendererObj, 96, 47, 70, 12);
			integerField.setMaxStringLength(9);
			integerField.setText("" + this.getInteger());
			this.buttonList.add(new GuiButton(1, guiLeft + 68, guiTop + 43, 20, 20, this.getIntegerTypeString()));
		} else {
			stringField = new GuiTextField(this.fontRendererObj, 6, 47, 164, 12);
			stringField.setMaxStringLength(30);
			stringField.setText(this.getString());
		}

	}

	public String getErrorFlagString() {
		int type = this.getErrorFlag();
		switch (type) {
		case 0:
			return " ";
		case 1:
			return "ERROR: NO DATA";
		case 2:
			return "ERROR: INCOMPATIBLE DATA";
		}
		return null;
	}

	public String getIntegerTypeString() {
		int type = this.getIntegerType();
		switch (type) {
		case 0:
			return "==";
		case 1:
			return ">";
		case 2:
			return "<";
		case 3:
			return "!=";
		}
		return null;
	}

	public String getSyncType() {
		int type = this.getGuiType();
		switch (type) {
		case 0:
			return "NUMBER";
		case 1:
			return "WORD";
		}
		return null;
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.text("Data Type:", 26, 12, 0);

		if (type == 0) {
			FontHelper.textCentre("Emits if", xSize, 32, 0);
			FontHelper.text("INPUT DATA", 6, 49, 0);
			integerField.drawTextBox();
		} else {
			FontHelper.textCentre("Emits if data equals", xSize, 32, 0);
			stringField.drawTextBox();
		}
		FontHelper.textCentre(this.getErrorFlagString(), xSize, 68, 1);
	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		if (type == 0)
			integerField.mouseClicked(i - guiLeft, j - guiTop, k);
		else
			stringField.mouseClicked(i - guiLeft, j - guiTop, k);
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == 0) {
				changeGuiType();
			} else if (button.id == 1) {
				changeIntegerType();
			}
		}
		reset();
	}

	@Override
	protected void keyTyped(char c, int i) {
		if (type == 0 && integerField.isFocused()) {
			if (c == 13 || c == 27) {
				integerField.setFocused(false);
			} else {
				FontHelper.addDigitsToString(integerField, c, i);
				final String text = integerField.getText();
				if (text.isEmpty() || text == "" || text == null) {
					setInteger("0");
				} else {
					setInteger(text);
				}

			}
		} else if (type == 1 && stringField.isFocused()) {
			if (c == 13 || c == 27) {
				stringField.setFocused(false);
			} else {
				stringField.textboxKeyTyped(c, i);
				final String text = stringField.getText();
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

	public abstract String getString();

	public abstract int getInteger();

	public abstract void setString(String string);

	public abstract void setInteger(String string);

	public abstract void changeGuiType();

	public abstract void changeIntegerType();

	public abstract int getGuiType();

	public abstract int getIntegerType();

	public abstract int getErrorFlag();

	public static class RedstoneSignaller extends GuiRedstoneSignaller {

		public TileEntityRedstoneSignaller entity;

		public RedstoneSignaller(TileEntityRedstoneSignaller entity) {
			super(new ContainerEmptySync(entity), entity);
			this.entity = entity;
		}

		@Override
		public String getString() {
			return entity.stringName.getString();
		}

		@Override
		public void setString(String string) {
			SonarPackets.network.sendToServer(new PacketTextField(string, entity.xCoord, entity.yCoord, entity.zCoord, 1));
			entity.stringName.setString(string);
		}

		@Override
		public void changeGuiType() {
			if (!(entity.dataType.getInt() > 0)) {
				entity.dataType.increaseBy(1);
			} else {
				entity.dataType.setInt(0);
			}
			SonarPackets.network.sendToServer(new PacketMachineButton(0, entity.dataType.getInt(), entity.xCoord, entity.yCoord, entity.zCoord));
		}

		@Override
		public void changeIntegerType() {
			if (!(entity.integerEmitType.getInt() > 2)) {
				entity.integerEmitType.increaseBy(1);
			} else {
				entity.integerEmitType.setInt(0);
			}
			SonarPackets.network.sendToServer(new PacketMachineButton(1, entity.integerEmitType.getInt(), entity.xCoord, entity.yCoord, entity.zCoord));

		}

		@Override
		public int getGuiType() {
			return entity.dataType.getInt();

		}

		@Override
		public int getInteger() {
			return entity.integerTarget.getInt();
		}

		@Override
		public void setInteger(String string) {
			SonarPackets.network.sendToServer(new PacketTextField(string, entity.xCoord, entity.yCoord, entity.zCoord, 0));
			entity.integerTarget.setInt(Integer.parseInt(string));
		}

		@Override
		public int getIntegerType() {
			return entity.integerEmitType.getInt();
		}

		@Override
		public int getErrorFlag() {
			return entity.errorFlag.getInt();
		}

	}
}
