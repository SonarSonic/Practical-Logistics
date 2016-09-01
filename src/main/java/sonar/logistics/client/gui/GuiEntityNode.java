package sonar.logistics.client.gui;
/*
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import sonar.core.SonarCore;
import sonar.core.client.gui.GuiSonar;
import sonar.core.helpers.FontHelper;
import sonar.core.network.PacketByteBuf;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.tileentity.TileEntityEntityNode;

public class GuiEntityNode extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/rename.png");
	private GuiTextField nameField;

	public TileEntityEntityNode entity;

	public GuiEntityNode(TileEntityEntityNode entity) {
		super(new ContainerEmptySync(entity), entity);
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
		this.buttonList.add(new GuiButton(0, guiLeft + 20, guiTop + 18, 126, 20, getEntityTypeString()));
		this.buttonList.add(new GuiButton(1, guiLeft + 148, guiTop + 5, 20, 20, "+1"));
		this.buttonList.add(new GuiButton(2, guiLeft + 148, guiTop + 27, 20, 20, "-1"));
	}

	public String getEntityTypeString() {
		int type = this.getEntityType();
		switch (type) {
		case 0:
			return "ALL";
		case 1:
			return "PLAYERS";
		case 2:
			return "MOBS";
		case 3:
			return "ANIMALS";
		}
		return null;
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			if (button.id == 0) {
				if (!(entity.entityTarget.getObject() > 2)) {
					entity.entityTarget.increaseBy(1);
				} else {
					entity.entityTarget.setObject(0);
				}
			}
			SonarCore.network.sendToServer(new PacketByteBuf(entity, entity.getPos(), button.id));
		}
		reset();
	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(FontHelper.translate("tile.EntityNode.name"), xSize, 6, 0);
		FontHelper.textCentre("Range: " + entity.entityRange.getObject(), xSize, 40, 0);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public int getEntityType() {
		return entity.entityTarget.getObject();
	}

	public void changeEntityType() {

	}
}
*/