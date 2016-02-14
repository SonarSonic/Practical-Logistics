package sonar.logistics.client.gui;

import java.text.SimpleDateFormat;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import sonar.core.SonarCore;
import sonar.core.inventory.GuiSonar;
import sonar.core.network.PacketByteBufServer;
import sonar.core.utils.helpers.FontHelper;
import sonar.logistics.common.containers.ContainerEmptySync;
import sonar.logistics.common.tileentity.TileEntityClock;

public class GuiClock extends GuiSonar {

	public static final ResourceLocation bground = new ResourceLocation("PracticalLogistics:textures/gui/signaller.png");

	public TileEntityClock entity;

	public GuiClock(TileEntityClock entity) {
		super(new ContainerEmptySync(entity), entity);
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
		this.buttonList.add(new GuiButton(2, guiLeft + 130-3, guiTop + 25, 40, 20, "+0.1 s"));
		this.buttonList.add(new GuiButton(3, guiLeft + 130-3, guiTop + 50, 40, 20, "-0.1 s"));
		this.buttonList.add(new GuiButton(4, guiLeft + 90-3, guiTop + 25, 40, 20, "+1 s"));
		this.buttonList.add(new GuiButton(5, guiLeft + 90-3, guiTop + 50, 40, 20, "-1 s"));
		this.buttonList.add(new GuiButton(6, guiLeft + 50-3, guiTop + 25, 40, 20, "+1 min"));
		this.buttonList.add(new GuiButton(7, guiLeft + 50-3, guiTop + 50, 40, 20, "-1 min"));
		this.buttonList.add(new GuiButton(8, guiLeft + 10-3, guiTop + 25, 40, 20, "+1 hr"));
		this.buttonList.add(new GuiButton(9, guiLeft + 10-3, guiTop + 50, 40, 20, "-1 hr"));

	}

	@Override
	public void drawGuiContainerForegroundLayer(int x, int y) {
		super.drawGuiContainerForegroundLayer(x, y);
		GL11.glPushMatrix();
		GL11.glScaled(1.5, 1.5, 1.5);
		FontHelper.textCentre(new SimpleDateFormat("HH:mm:ss:SSS").format(getLong() - (60 * 60 * 1000)).substring(0, 11), (int)(xSize/(1.5)), 6, 0);
		GL11.glPopMatrix();

	}

	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
	}

	protected void actionPerformed(GuiButton button) {
		if (button != null) {
			SonarCore.network.sendToServer(new PacketByteBufServer(entity, entity.xCoord, entity.yCoord, entity.zCoord, button.id));
			
		}
		reset();
	}


	@Override
	public ResourceLocation getBackground() {
		return bground;
	}

	public long getLong() {
		return entity.tickTime.getLong();
	}

	public void setLong(String string) {
		entity.tickTime.setLong(Long.parseLong(string));
		SonarCore.network.sendToServer(new PacketByteBufServer(entity, entity.xCoord, entity.yCoord, entity.zCoord, 1));
	}
}
