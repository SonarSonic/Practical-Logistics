package sonar.logistics.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;

import sonar.core.SonarCore;
import sonar.core.inventory.GuiSonar;
import sonar.core.network.PacketMachineButton;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.tileentity.TileEntityEntityNode;

public abstract class GuiEntityNode extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/rename.png");
	private GuiTextField nameField;

	public GuiEntityNode(Container container, TileEntity entity) {
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
		this.buttonList.add(new GuiButton(0, guiLeft+ 20, guiTop+ 20, 126, 20, getEntityTypeString()));
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
				changeEntityType();
			}
		}
		reset();
	}
	
	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		FontHelper.textCentre(FontHelper.translate("tile.EntityNode.name"), xSize, 6, 0);
	}

	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public abstract int getEntityType();

	public abstract void changeEntityType();

	public static class EntityNode extends GuiEntityNode {

		public TileEntityEntityNode entity;

		public EntityNode(TileEntityEntityNode entity) {
			super(new ContainerEmptySync(entity), entity);
			this.entity = entity;
		}
		@Override
		public int getEntityType() {
			return entity.entityTarget.getInt();
		}
		@Override
		public void changeEntityType() {
			if (!(entity.entityTarget.getInt() > 2)) {
				entity.entityTarget.increaseBy(1);
			} else {
				entity.entityTarget.setInt(0);
			}
			SonarCore.network.sendToServer(new PacketMachineButton(0, entity.entityTarget.getInt(), entity.xCoord, entity.yCoord, entity.zCoord));
		
		}

	}
}
